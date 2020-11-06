package net.runelite.client.plugins.playeritemhighlight;

import lombok.Data;
import net.runelite.api.Player;
import net.runelite.http.api.hiscore.HiscoreResult;

@Data
public class PlayerStats {

    private HiscoreResult result;
    private int combatLevel;
    private int myCombatLevel;

    public PlayerStats(HiscoreResult result, int combatLevel, int myCombatLevel) {
        this.result = result;
        this.combatLevel = combatLevel;
        this.myCombatLevel = myCombatLevel;
    }

    public boolean shouldHighlight(PlayerItemHighlightConfig config) {
        if (PlayerItemHighlight.withinRange(combatLevel, myCombatLevel, config) &&
                result.getHitpoints().getLevel() >= config.hpMin() && result.getHitpoints().getLevel() <= config.hpMax() &&
                result.getRanged().getLevel() >= config.rangeMin() && result.getRanged().getLevel() <= config.rangeMax() &&
                result.getStrength().getLevel() >= config.strengthMin() && result.getStrength().getLevel() <= config.strengthMax()
        )
            return true;
        return false;
    }

    public String getString() {
        return "h"+result.getHitpoints().getLevel() + "/s" +result.getStrength().getLevel()+"/r"+ result.getRanged().getLevel();
    }
}
