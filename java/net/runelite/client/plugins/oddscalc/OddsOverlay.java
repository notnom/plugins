package net.runelite.client.plugins.oddscalc;

import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.client.plugins.oddscalc.math.DuelSimulator;
import net.runelite.client.plugins.oddscalc.math.DuelType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.Future;

public class OddsOverlay extends Overlay {
   private final PanelComponent statsPanel = new PanelComponent();
   private final OddsPlugin plugin;
   private final OddsConfig config;
   private final Client client;

   @Inject
   public OddsOverlay(Client client, OddsPlugin plugin, OddsConfig config) {
      this.client = client;
      this.plugin = plugin;
      this.config = config;
      this.setLayer(OverlayLayer.ABOVE_WIDGETS);
      this.setPriority(OverlayPriority.HIGHEST);
      this.setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
   }

   public Dimension render(Graphics2D graphics) {
      if (this.config.interfaceOnly() && this.client.getVar(VarPlayer.DUEL_PENDING) == 0) {
         return null;
      } else {
         try {
            if (this.plugin.getSimulator() != null && this.plugin.getSimulator().isDone()) {
               DuelSimulator simulator = (DuelSimulator)this.plugin.getSimulator().get();
               sendTimedWebhook(simulator);
               this.statsPanel.getChildren().clear();
               this.statsPanel.setPreferredSize(new Dimension(150, 100));
               this.statsPanel.getChildren().add(TitleComponent.builder().text(simulator.getOpponent().getUserName()).build());
               this.statsPanel.getChildren().add(LineComponent.builder().left("Attack").rightColor(this.getColor(simulator.getSelf().getAttackLevel(), simulator.getOpponent().getAttackLevel())).right(String.valueOf(simulator.getOpponent().getAttackLevel())).build());
               this.statsPanel.getChildren().add(LineComponent.builder().left("Strength").rightColor(this.getColor(simulator.getSelf().getStrengthLevel(), simulator.getOpponent().getStrengthLevel())).right(String.valueOf(simulator.getOpponent().getStrengthLevel())).build());
               this.statsPanel.getChildren().add(LineComponent.builder().left("Defence").rightColor(this.getColor(simulator.getSelf().getDefenseLevel(), simulator.getOpponent().getDefenseLevel())).right(String.valueOf(simulator.getOpponent().getDefenseLevel())).build());
               this.statsPanel.getChildren().add(LineComponent.builder().left("Hitpoints").rightColor(this.getColor(simulator.getSelf().getHitpointsLevel(), simulator.getOpponent().getHitpointsLevel())).right(String.valueOf(simulator.getOpponent().getHitpointsLevel())).build());
               this.statsPanel.getChildren().add(LineComponent.builder().left("Ranged").rightColor(this.getColor(simulator.getSelf().getRangedLevel(), simulator.getOpponent().getRangedLevel())).right(String.valueOf(simulator.getOpponent().getRangedLevel())).build());
               Future odds;
               if (this.config.calculateTentacle()) {
                  odds = simulator.getOdds(DuelType.TENTACLE);
                  if (odds.isDone()) {
                     this.statsPanel.getChildren().add(LineComponent.builder().left("Tent Odds").right(String.format("%.2f", odds.get())).build());
                  }
               }

               if (this.config.calculateDDS()) {
                  odds = simulator.getOdds(DuelType.DDS);
                  if (odds.isDone()) {
                     this.statsPanel.getChildren().add(LineComponent.builder().left("DDS Odds").right(String.format("%.2f", odds.get())).build());
                  }
               }

               if (this.config.calculateBoxing()) {
                  odds = simulator.getOdds(DuelType.BOX);
                  if (odds.isDone()) {
                     this.statsPanel.getChildren().add(LineComponent.builder().left("Box Odds").right(String.format("%.2f", odds.get())).build());
                  }
               }

               if (this.config.calculateRanged()) {
                  odds = simulator.getOdds(DuelType.RANGED);
                  if (odds.isDone()) {
                     this.statsPanel.getChildren().add(LineComponent.builder().left("Knife Odds").right(String.format("%.2f", odds.get())).build());
                  }
               }

               this.statsPanel.render(graphics);
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }

         return null;
      }
   }

   private Color getColor(double a, double b) {
      if (a > b) {
         return Color.GREEN;
      } else {
         return b > a ? Color.RED : Color.YELLOW;
      }
   }

   private DecimalFormat df = new DecimalFormat("#.#");
   private void sendTimedWebhook(DuelSimulator sim) {
      Future<Double> odds = sim.getOdds(DuelType.DDS);
      Future<Double> odds2 = sim.getOdds(DuelType.TENTACLE);
      if (!odds.isDone() || !odds2.isDone()) return;
      try {
         plugin.getWebhookText().set(sim.getSelf().getUserName() + " vs " + sim.getOpponent().getUserName() +
                 " - Tent:" + df.format(odds2.get()) + "%, DDS:" + df.format(odds.get()) + "%");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
