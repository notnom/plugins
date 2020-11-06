package net.runelite.client.plugins.autochatfilter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("autochatfilter")
public interface AutoChatFilterConfig extends Config {
    @ConfigItem(
            keyName = "filterAutochat",
            name = "filterAutochat",
            description = "Whether or not to filterAutochat"
    )
    default boolean filterAutochat() {
        return true;
    }

    @ConfigItem(
            keyName = "filterBotSpam",
            name = "filterBotSpam",
            description = "Whether or not to filterBotSpam"
    )
    default boolean filterBotSpam() {
        return true;
    }

    @ConfigItem(
            keyName = "repeatCount",
            name = "repeatCount",
            description = "If repeatCount >= _ , filter as bot spam"
    )
    default int repeatCount() {
        return 3;
    }

    @ConfigItem(
            keyName = "combatLevelMin",
            name = "combatLevelMin",
            description = "Only show chat of combat levels between min-max level"
    )
    default int combatLevelMin() {
        return 90;
    }

    @ConfigItem(
            keyName = "combatLevelMax",
            name = "combatLevelMax",
            description = "Only show chat of combat levels between min-max level"
    )
    default int combatLevelMax() {
        return 100;
    }

    @ConfigItem(
            keyName = "duplicateDelay",
            name = "duplicateDelay",
            description = "If latest chat is duplicated after _ seconds, filter as bot spam"
    )
    default int duplicateDelay() {
        return 30;
    }
}