package net.runelite.client.plugins.oddscalc;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("odds")
public interface OddsConfig extends Config {
   String manualCalc = "manualCalc";
   @ConfigSection(
           name = "Manual Calc",
           description = "",
           position = 0,
           keyName = manualCalc
   )
   default boolean manualCalc()
   {
      return false;
   }


   @ConfigItem(
           keyName = "toggleManualCalc",
           name = "ToggleToStart",
           description = "Toggle off and on to start calc",
           section = manualCalc
   )
   default boolean toggleManualCalc() {
      return false;
   }

   @ConfigItem(
           keyName = "strengthManualCalc",
           name = "Strength",
           description = "Strength",
           section = manualCalc
   )
   default int strength() {
      return 99;
   }
   @ConfigItem(
           keyName = "attackManualCalc",
           name = "Attack",
           description = "Attack",
           section = manualCalc
   )
   default int attack() {
      return 99;
   }
   @ConfigItem(
           keyName = "rangeManualCalc",
           name = "Range",
           description = "Range",
           section = manualCalc
   )
   default int range() {
      return 99;
   }
   @ConfigItem(
           keyName = "defenceManualCalc",
           name = "Defence",
           description = "Defence",
           section = manualCalc
   )
   default int defence() {
      return 99;
   }
   @ConfigItem(
           keyName = "hitpointsManualCalc",
           name = "Hitpoints",
           description = "Hitpoints",
           section = manualCalc
   )
   default int hitpoints() {
      return 99;
   }
   
   
   
   @ConfigItem(
      keyName = "trials",
      name = "Trial Count",
      description = "Number of trials to run during simulations"
   )
   default int getTrialCount() {
      return 100000;
   }

   @ConfigItem(
      keyName = "tent",
      name = "Tentacle Odds",
      description = "Toggles calculation of tentacle odds"
   )
   default boolean calculateTentacle() {
      return true;
   }

   @ConfigItem(
      keyName = "dds",
      name = "DDS Odds",
      description = "Toggles calculation of DDS odds"
   )
   default boolean calculateDDS() {
      return false;
   }

   @ConfigItem(
      keyName = "box",
      name = "Boxing Odds",
      description = "Toggles calculation of boxing odds"
   )
   default boolean calculateBoxing() {
      return false;
   }

   @ConfigItem(
      keyName = "knives",
      name = "Knives Odds",
      description = "Toggles calculation of ranging odds"
   )
   default boolean calculateRanged() {
      return false;
   }

   @ConfigItem(
      keyName = "autoAccept",
      name = "Auto Acceptor (BOT)",
      description = "Toggles auto acception of duels. Use at your own risk"
   )
   default boolean autoAccept() {
      return false;
   }

   @ConfigItem(
      keyName = "challengeSound",
      name = "Sound on Challenge",
      description = "Plays a sound when a challenge is received"
   )
   default boolean challengeSound() {
      return false;
   }

   @ConfigItem(
      keyName = "interfaceOnly",
      name = "Duel Interface Only",
      description = "If enabled the odds will only be displayed when the duel interface is open"
   )
   default boolean interfaceOnly() {
      return false;
   }
}
