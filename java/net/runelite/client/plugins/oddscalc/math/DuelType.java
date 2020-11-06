package net.runelite.client.plugins.oddscalc.math;

public enum DuelType {
   DDS(Equipment.DDS),
   TENTACLE(Equipment.TENTACLE),
   SCIM(Equipment.D_SCIM),
   RANGED(Equipment.RUNE_KNIFE),
   BOX(Equipment.BOX);

   private final Equipment equipment;

   private DuelType(Equipment equipment) {
      this.equipment = equipment;
   }

   public Equipment getEquipment() {
      return this.equipment;
   }
}
