package net.runelite.client.plugins.dueloptions;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("duelnotifier")
public interface DuelNotifierConfig extends Config {
    @ConfigItem(
            keyName = "overlayEnabled",
            name = "Enable overlay",
            description = "Whether or not to paint over duel screen"
    )
    default boolean overlayEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "overlayColor",
            name = "Overlay color",
            description = "Color to paint duel screen"
    )
    default Color getOverlayColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "chatPid",
            name = "Enable chatPid overlay",
            description = "Whether or not to display chat pid"
    )
    default boolean chatPidOverlayEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "ignoredUsers",
            name = "Ignored Users",
            description = "Configures ignored users. Format: (item), (item)",
            titleSection = "ignoredUsers"
    )
    default String getIgnoredUsers()
    {
        return "Woox, A Friend, Jagex";
    }
}