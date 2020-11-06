package net.runelite.client.plugins.playeritemhighlight;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("playeritemhighlight")
public interface PlayerItemHighlightConfig extends Config {
    @ConfigItem(
            keyName = "showName",
            name = "showName",
            description = "Whether or not to showName"
    )
    default boolean showName() {
        return true;
    }

    @ConfigItem(
            keyName = "tbowColor",
            name = "tbowColor",
            description = "Color to paint tbowColor"
    )
    default Color getTbowColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "whipColor",
            name = "whipColor",
            description = "Color to paint whipColor"
    )
    default Color getWhipColor() {
        return Color.MAGENTA;
    }

    String statsHighlight = "statsHighlight";
    @ConfigSection(
            name = "Stats highlight",
            description = "",
            position = 1,
            keyName = statsHighlight
    )
    default boolean statsHighlight()
    {
        return false;
    }


    String combatLevelFilter = "combatLevelFilter";
    @ConfigSection(
            name = "Combat Level Filter",
            description = "",
            position = 0,
            keyName = combatLevelFilter
    )
    default boolean combatLevelFilter()
    {
        return false;
    }

    @ConfigItem(
            keyName = "ignoreCombatLevelMin",
            name = "ignoreCombatLevelMin",
            description = "Does not include the level typed in",
            section = combatLevelFilter
    )
    default int ignoreCombatLevelMin() {
        return 100;
    }


    @ConfigItem(
            keyName = "ignoreCombatLevelMax",
            name = "ignoreCombatLevelMax",
            description = "Does not include the level typed in",
            section = combatLevelFilter
    )
    default int ignoreCombatLevelMax() {
        return 126;
    }

    @ConfigItem(
            keyName = "autoBaseOnCurrentLevel",
            name = "autoBaseOnCurrentLevel",
            description = "Override the min-max combat level range",
            position = 0,
            section = combatLevelFilter
    )
    default boolean autoBaseOnCurrentLevel() {
        return true;
    }

    @ConfigItem(
            keyName = "ignoreCombatLevelBelow",
            name = "ignoreCombatLevelBelow",
            description = "Does not include the level typed in",
            position = 0,
            section = combatLevelFilter
    )
    default int ignoreCombatLevelBelow() {
        return 5;
    }

    @ConfigItem(
            keyName = "ignoreCombatLevelAbove",
            name = "ignoreCombatLevelAbove",
            description = "Does not include the level typed in",
            position = 0,
            section = combatLevelFilter
    )
    default int ignoreCombatLevelAbove() {
        return 5;
    }

    @ConfigItem(
            keyName = "statsColor",
            name = "statsColor",
            description = "Color to paint statsColor",
            section = statsHighlight
    )
    default Color getStatsColor() {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "strengthMin",
            name = "strengthMin",
            description = "strengthMin",
            section = statsHighlight
    )
    default int strengthMin() {
        return 90;
    }

    @ConfigItem(
            keyName = "strengthMax",
            name = "strengthMax",
            description = "strengthMax",
            section = statsHighlight
    )
    default int strengthMax() {
        return 99;
    }


    @ConfigItem(
            keyName = "rangeMin",
            name = "rangeMin",
            description = "rangeMin",
            section = statsHighlight
    )
    default int rangeMin() {
        return 90;
    }

    @ConfigItem(
            keyName = "rangeMax",
            name = "rangeMax",
            description = "rangeMax",
            section = statsHighlight
    )
    default int rangeMax() {
        return 99;
    }


    @ConfigItem(
            keyName = "hpMin",
            name = "hpMin",
            description = "hpMin",
            section = statsHighlight
    )
    default int hpMin() {
        return 90;
    }

    @ConfigItem(
            keyName = "hpMax",
            name = "hpMax",
            description = "hpMax",
            section = statsHighlight
    )
    default int hpMax() {
        return 99;
    }
}