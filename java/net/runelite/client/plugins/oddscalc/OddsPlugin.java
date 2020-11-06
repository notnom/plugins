package net.runelite.client.plugins.oddscalc;


import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.SoundEffectID;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.*;
import net.runelite.client.plugins.oddscalc.input.InputSender;
import net.runelite.client.plugins.oddscalc.math.DuelSimulator;
import net.runelite.client.plugins.oddscalc.math.RSPlayer;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.QuantityFormatter;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Extension
@PluginDescriptor(name = "Staking Odds", description = "Overlays to provide staking odds", type = PluginType.UTILITY)
public class OddsPlugin extends Plugin
{
   @Inject
   private Client client;
   @Inject
   private OverlayManager overlayManager;
   @Inject
   private OddsOverlay overlay;
   @Inject
   private InputSender inputSender;
   @Inject
   private OddsConfig config;
   @Inject
   private ScheduledExecutorService executorService;
   private Future<DuelSimulator> simulator;
   private int pendingDuelVarp;

   public OddsPlugin() {
      this.pendingDuelVarp = 0;
   }

   @Provides
   OddsConfig getConfig(final ConfigManager configManager) {
      return (OddsConfig)configManager.getConfig((Class)OddsConfig.class);
   }

   protected void startUp() {
      this.overlayManager.add((Overlay)this.overlay);
      menuManager.get().addPlayerMenuItem(LOOKUP);
   }

   protected void shutDown() {
      this.overlayManager.remove((Overlay)this.overlay);
      if (this.simulator != null) {
         this.simulator.cancel(true);
         this.simulator = null;
      }
      menuManager.get().removePlayerMenuItem(LOOKUP);
   }


   @Subscribe
   public void onConfigChanged(ConfigChanged event)
   {
      if (event.getGroup().equals("odds") && event.getKey().equals("toggleManualCalc") && config.toggleManualCalc())
      {
         this.simulator = this.executorService.submit(() -> {
            RSPlayer opp = RSPlayer.fromStats(config.attack(),config.strength(),config.defence(),config.hitpoints(),config.range());
            return new DuelSimulator((ExecutorService)this.executorService, this.config.getTrialCount(), RSPlayer.fromSelf(this.client), opp);
         });
      }
   }

   @Getter
   private AtomicReference<String> webhookText = new AtomicReference<>("default text");

   @Subscribe
   public void onChatMessage(final ChatMessage event) {
      if (event.getMessage().toLowerCase().contains("stake and due")) { //accepted stake and duel options.
         DiscordWebhook webhook = new DiscordWebhook();
         webhook.setContent(webhookText.get());
         webhook.execute(executorService);
      }
      if (event.getType() == ChatMessageType.CHALREQ_TRADE && event.getMessage().toLowerCase().endsWith("duel with you.")) {
         final Pattern p = Pattern.compile(".*?(?=\\s+wishes)");
         final Matcher m = p.matcher(event.getMessage());
         if (m.find()) {
            final String user = m.group(0);
            if (this.config.autoAccept()) {
               final Widget[] parent = new Widget[1];
               final Widget[] array = new Widget[0];
               final int[] length = new int[1];
               final int[] i = {0};
               final Widget[] child = new Widget[1];
               final CharSequence s = null;
               final Thread t = new Thread(() -> {
                  try {
                     Thread.sleep(ThreadLocalRandom.current().nextInt(850, 1000));
                  }
                  catch (InterruptedException e) {
                     e.printStackTrace();
                  }
                  parent[0] = this.client.getWidget(162, 58);
                  if (parent[0] != null) {
                     parent[0].getDynamicChildren();
                     length[0] = array.length;
                     while (i[0] < length[0]) {
                        child[0] = array[i[0]];
                        if (child[0] != null && child[0].getText().contains(s) && child[0].getText().toLowerCase().endsWith("wishes to duel with you.</col>")) {
                           this.inputSender.leftClick(child[0].getBounds());
                           break;
                        }
                        else {
                           ++i[0];
                        }
                     }
                  }
                  return;
               });
               t.start();
            }
            if (this.config.challengeSound()) {
               this.client.playSoundEffect(SoundEffectID.GE_ADD_OFFER_DINGALING);
            }

            this.simulator = this.executorService.submit(() -> {
               RSPlayer opp = RSPlayer.fromName(user);
               if (opp == null) return null;
               return new DuelSimulator((ExecutorService)this.executorService, this.config.getTrialCount(), RSPlayer.fromSelf(this.client), opp);
            });
         }
      }
//      if (event.getType() == ChatMessageType.UNKNOWN && event.getMessage().toLowerCase().startsWith("challenging")) {
//         final Pattern p = Pattern.compile("(?<=\\s)(.*)(?=...)");
//         final Matcher m = p.matcher(event.getMessage());
//         if (m.find()) {
//            final String user = m.group(0);
//            final String user2 = null;
//            this.simulator = this.executorService.submit(() -> new DuelSimulator((ExecutorService)this.executorService, this.config.getTrialCount(), RSPlayer.fromSelf(this.client), RSPlayer.fromName(user2)));
//         }
//      }
   }

   @Subscribe
   public void onVarbitChanged(final VarbitChanged event) {
      final int val = this.client.getVar(VarPlayer.DUEL_PENDING);
      if (this.pendingDuelVarp != 0 && val == 0 && this.simulator != null) {
         this.simulator.cancel(true);
         this.simulator = null;
      }
      this.pendingDuelVarp = val;
   }

   public Future<DuelSimulator> getSimulator() {
      return this.simulator;
   }


   @Subscribe
   private void onWidgetLoaded(WidgetLoaded widgetLoaded)
   {
      if (widgetLoaded.getGroupId() == ChallengeWidgets.WIN_SCREEN_GROUP_ID) {
         log.info("Duel Win screen");
         Widget winnerName = client.getWidget(ChallengeWidgets.WIN_SCREEN_GROUP_ID,ChallengeWidgets.WinScreen.WINNER_NAME);
         Widget loserName = client.getWidget(ChallengeWidgets.WIN_SCREEN_GROUP_ID,ChallengeWidgets.WinScreen.LOSER_NAME);
         Widget winAmount = client.getWidget(ChallengeWidgets.WIN_SCREEN_GROUP_ID,ChallengeWidgets.WinScreen.WIN_AMOUNT);
         Widget taxAmount = client.getWidget(ChallengeWidgets.WIN_SCREEN_GROUP_ID,ChallengeWidgets.WinScreen.TAX_AMOUNT);

         if (winnerName == null || loserName == null || winAmount == null || taxAmount == null) {
            return;
         }
         final Pattern valuePattern = Pattern.compile("\\d{1,3}(,\\d{3})+");
         Matcher matcher = valuePattern.matcher(winAmount.getText());
         Matcher matcherTax = valuePattern.matcher(taxAmount.getText());
         if (!matcher.find() || !matcherTax.find()) return;
         int wonGP = Integer.parseInt(matcher.group(0).replaceAll(",",""));
         int taxGP = Integer.parseInt(matcherTax.group(0).replaceAll(",",""));
         String enemyName;
         int finalAmount;
         if (loserName.getText().equalsIgnoreCase(client.getLocalPlayer().getName())) {
            finalAmount = calcAmount(wonGP,taxGP,false);
            enemyName = winnerName.getText() + " -"+finalAmount;
            finalAmount *= -1;
         } else {
            finalAmount = calcAmount(wonGP,taxGP,true);
            enemyName = loserName.getText() + " +"+finalAmount;
         }

         DiscordWebhook webhook = new DiscordWebhook();
         webhook.setContent(client.getLocalPlayer().getName() + " vs " + enemyName + ":" + QuantityFormatter.quantityToStackSize(finalAmount));
         webhook.execute(executorService);
      }
   }

   public static int calcAmount(int wonGP, int taxAmount, boolean won) {
      if (won) {
         return wonGP/2;
      } else {
         return (wonGP+taxAmount)/2;
      }
   }

   @Inject
   private Provider<MenuManager> menuManager;
   private static final String LOOKUP = "Stake Odds";

   Pattern pattern = Pattern.compile("^(.* {2})\\(.*");
   @Subscribe
   public void onPlayerMenuOptionClicked(PlayerMenuOptionClicked event)
   {
      log.info(event.getMenuOption() + " Lookup " + Text.removeTags(event.getMenuTarget()));
      if (event.getMenuOption().equals(LOOKUP))
      {
//         Matcher m = pattern.matcher(Text.removeTags(event.getMenuTarget()));
//         log.info("Pattern matching " + Text.removeTags(event.getMenuTarget()));
//         if (m.find()) {
//            final String user = m.group(0);
//
//         }
         this.simulator = this.executorService.submit(() -> {
            RSPlayer opp = RSPlayer.fromName(Text.removeTags(event.getMenuTarget()));
            if (opp == null) return null;
            return new DuelSimulator((ExecutorService)this.executorService, this.config.getTrialCount(), RSPlayer.fromSelf(this.client), opp);
         });
      }
   }
}
