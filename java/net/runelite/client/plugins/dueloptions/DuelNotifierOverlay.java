package net.runelite.client.plugins.dueloptions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class DuelNotifierOverlay extends Overlay {
    private final DuelNotifierConfig config;
    private Instant instant;
    private Instant toggleInstant;
    private boolean toggle;
    private boolean showOverlay;
    private Client client;

    @Inject
    private DuelNotifierOverlay(DuelNotifierConfig config, Client client) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.config = config;
        this.client = client;
    }

    public Dimension render(Graphics2D graphics) {
        if (!showOverlay) return null;
        if (this.config.overlayEnabled() && this.instant != null && this.toggleInstant != null) {
            if (Duration.between(this.instant, Instant.now()).toMillis() > 30000L) {
                this.resetTimer();
            }

            if (Duration.between(this.toggleInstant, Instant.now()).toMillis() > 500L) {
                this.toggle = !this.toggle;
                this.toggleInstant = Instant.now();
            }

            if (this.toggle) {
                Color color = this.config.getOverlayColor();
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
                graphics.fill(this.client.getCanvas().getBounds());
            }
        }

        return null;
    }

    public void startTimer() {
        this.instant = Instant.now();
        this.toggleInstant = Instant.now();
        this.toggle = true;
        this.showOverlay = true;
    }

    public void resetTimer() {
        this.toggle = false;
        this.showOverlay = false;
    }
}
