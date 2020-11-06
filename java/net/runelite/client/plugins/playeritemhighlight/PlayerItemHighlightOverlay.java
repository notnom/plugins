package net.runelite.client.plugins.playeritemhighlight;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.kit.KitType;
import net.runelite.api.util.Text;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class PlayerItemHighlightOverlay extends Overlay {
    private final PlayerItemHighlight plugin;
    private final PlayerItemHighlightConfig config;
    private Client client;

    @Inject
    private PlayerItemHighlightOverlay(PlayerItemHighlight plugin, PlayerItemHighlightConfig config, Client client) {
        this.plugin = plugin;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        for (Player player : client.getPlayers()) {
            if (player == null) continue;

            PlayerAppearance appearance = player.getPlayerAppearance();
            if (appearance == null) continue;
            if (appearance.getEquipmentId(KitType.WEAPON) == ItemID.TWISTED_BOW) {
                highlightPlayer(graphics,player,config.getTbowColor());
            }
            if (appearance.getEquipmentId(KitType.WEAPON) == ItemID.ABYSSAL_WHIP) {
                highlightPlayer(graphics,player,config.getWhipColor());
            }
        }
        for (Player p : plugin.getHighlightPlayers()) {
            if (p == null || p.getName() == null) continue;
            PlayerStats stats = plugin.getPlayerStatsMap().getOrDefault(p.getName(),null);
            if (stats == null) continue;
            if (stats.shouldHighlight(config))
                highlightPlayer(graphics, p, p.getName() + ":"+stats.getString(), config.getStatsColor());
        }
        return null;
    }

    private boolean statsHighlight(Graphics2D g, Player player) {
        return false;
    }

    private void highlightPlayer(Graphics2D g, Player player, String text, Color color) {
        if (player == null) return;
        String s = player.getName();
        if (s == null) return;

        Point textLocation = player.getCanvasTextLocation(g, text, player.getLogicalHeight() + 40);

        if (textLocation != null)
        {
            OverlayUtil.renderTextLocation(g, textLocation, text, color);
        }

        LocalPoint lp = player.getLocalLocation();
        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, 1);

        renderPoly(g, color, tilePoly);
    }

    private void highlightPlayer(Graphics2D g, Player player, Color color) {
        if (player == null) return;
        String s = player.getName();
        if (s == null) return;
        String npcName = Text.removeTags(s);

        highlightPlayer(g, player, npcName, color);
    }


    private void renderPoly(Graphics2D graphics, Color color, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(polygon);
        }
    }
}
