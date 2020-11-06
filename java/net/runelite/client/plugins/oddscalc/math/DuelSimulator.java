package net.runelite.client.plugins.oddscalc.math;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class DuelSimulator {
   private final RSPlayer self;
   private final RSPlayer opponent;
   private final int trials;
   private final Map<DuelType, Future<Double>> results;
   private final ExecutorService executorService;
   private final List<RSPlayer> players = new ArrayList<>(2);

   public DuelSimulator(ExecutorService executorService, int trials, RSPlayer self, RSPlayer opponent) {
      this.executorService = executorService;
      this.trials = trials;
      this.self = self;
      this.opponent = opponent;
      this.results = new HashMap<>(DuelType.values().length);
      this.players.add(self);
      this.players.add(opponent);
   }

   private Double calculateOdds(final DuelType type) {
      double wins = 0.0D;
      DuelType mainType = type;
      for(int i = 0; i < this.trials; ++i) {
         Collections.shuffle(this.players);
         RSPlayer a = (RSPlayer)this.players.get(0);
         a.resetHP();
         RSPlayer b = (RSPlayer)this.players.get(1);
         b.resetHP();
         if (type == DuelType.DDS) {
            for(int j = 0; j < 8; ++j) {
               if (getHitChance(DuelType.DDS, b, a) > ThreadLocalRandom.current().nextDouble()) {
                  a.applyDamage((double)ThreadLocalRandom.current().nextInt(0, (int)b.getMaxHit(DuelType.DDS) + 1));
                  if (a.isDead()) {
                     wins += b.equals(this.self) ? 1.0D : 0.0D;
                     break;
                  }
               }

               if (getHitChance(DuelType.DDS, a, b) > ThreadLocalRandom.current().nextDouble()) {
                  b.applyDamage((double)ThreadLocalRandom.current().nextInt(0, (int)a.getMaxHit(DuelType.DDS) + 1));
                  if (b.isDead()) {
                     wins += a.equals(this.self) ? 1.0D : 0.0D;
                     break;
                  }
               }
            }

            mainType = this.self.getAttackLevel() >= 75.0D ? DuelType.TENTACLE : DuelType.SCIM;
         }

         while(!this.self.isDead() && !this.opponent.isDead()) {
            if (getHitChance(mainType, b, a) > ThreadLocalRandom.current().nextDouble()) {
               a.applyDamage((double)ThreadLocalRandom.current().nextInt(0, (int)b.getMaxHit(mainType) + 1));
               if (a.isDead()) {
                  wins += b.equals(this.self) ? 1.0D : 0.0D;
                  break;
               }
            }

            if (getHitChance(mainType, a, b) > ThreadLocalRandom.current().nextDouble()) {
               b.applyDamage((double)ThreadLocalRandom.current().nextInt(0, (int)a.getMaxHit(mainType) + 1));
               if (b.isDead()) {
                  wins += a.equals(this.self) ? 1.0D : 0.0D;
                  break;
               }
            }
         }
      }

      return 100.0D * wins / (double)this.trials;
   }

   public Future<Double> getOdds(DuelType type) {
      if (!this.results.containsKey(type)) {
         this.results.put(type, this.executorService.submit(() -> {
            return this.calculateOdds(type);
         }));
      }

      return (Future)this.results.get(type);
   }

   private static double getHitChance(DuelType type, RSPlayer a, RSPlayer b) {
      double attack = a.getAccuracyRoll(type);
      double defense = b.getDefensiveRoll(type);
      return attack > defense ? 1.0D - (defense + 2.0D) / (2.0D * (attack + 1.0D)) : attack / (2.0D * (defense + 1.0D));
   }

   public RSPlayer getSelf() {
      return this.self;
   }

   public RSPlayer getOpponent() {
      return this.opponent;
   }

   public int getTrials() {
      return this.trials;
   }

   public Map<DuelType, Future<Double>> getResults() {
      return this.results;
   }
}
