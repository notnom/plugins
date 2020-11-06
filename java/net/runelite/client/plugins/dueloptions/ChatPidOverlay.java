package net.runelite.client.plugins.dueloptions;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.QuantityFormatter;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class ChatPidOverlay extends Overlay {
    private final DuelNotifierConfig config;
    private Client client;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private ChatPidOverlay(DuelNotifierConfig config, Client client) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        if (this.config.chatPidOverlayEnabled()) {

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Local player index:")
                    .right(client.getLocalPlayerIndex() + "")
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Login index:")
                    .right(client.getLoginIndex() + "")
                    .build());
            return panelComponent.render(graphics);
        }
        return panelComponent.getChildren().isEmpty() ? null : panelComponent.render(graphics);
    }
}
