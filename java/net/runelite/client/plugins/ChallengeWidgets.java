package net.runelite.client.plugins;

public class ChallengeWidgets {
    public static final int RULE_SCREEN_GROUP_ID = 482;
    public static final int OFFER_SCREEN_GROUP_ID = 481;
    public static final int CONFIRM_SCREEN_GROUP_ID = 476;
    public static final int WIN_SCREEN_GROUP_ID = 372;


    public ChallengeWidgets() {
    }

    public static class ConfirmScreen {
        public static final int ACCEPT_BUTTON = 73;
        public static final int DECLINE_BUTTON = 75;
        public static final int STATUS_LABEL = 51;
        public static final int OPPONENT_NAME_LABEL = 66;

        public ConfirmScreen() {
        }
    }

    public static class OfferScreen {
        public static final int INVENTORY_ITEMS = 31;
        public static final int EQUIPMENT_ITEMS = 34;
        public static final int ACCEPT_BUTTON = 74;
        public static final int DECLINE_BUTTON = 75;
        public static final int OPPONENT_OFFER_VALUE = 27;
        public static final int PLAYER_OFFER_VALUE = 17;
        public static final int STATUS_LABEL = 81;
        public static final int OPPONENT_NAME_LABEL = 24;
        public static final int OPPONENT_INVENTORY = 31;
        public static final int OPPONENT_EQUIPMENT = 34;

        public OfferScreen() {
        }
    }

    public static class RuleScreen {
        public static final int ACCEPT_BUTTON = 106;
        public static final int DECLINE_BUTTON = 104;
        public static final int OPPONENT_NAME_LABEL = 35;
        public static final int LOAD_PRESETS_BUTTON = 110;
        public static final int LOAD_LAST_DUEL_BUTTON = 109;
        public static final int PRESET_INDICATOR = 114;
        public static final int LAST_DUEL_INDICATOR = 115;
        public static final int STATUS_LABEL = 118;

        public RuleScreen() {
        }
    }


    public static class WinScreen {
        public static final int WINNER_NAME = 7;
        public static final int WINNER_COMBAT = 6;
        public static final int LOSER_NAME = 3;
        public static final int LOSER_COMBAT = 2;
        public static final int TAX_AMOUNT = 39;
        public static final int WIN_AMOUNT = 40;
        public static final int CLAIM = 18;
        public static final int TAX_RATE = 38;
        public static final int TAX_TOTAL = 39;
        public static final int TOTAL_STAKED = 32;
        public static final int PLATINUM = 35;
        public static final int GOLD = 37;
        public static final int OPPONENT_NAME_LABEL = 66;

        public WinScreen() {
        }
    }
}
