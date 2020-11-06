package net.runelite.client.plugins.playeritemhighlight;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.http.api.RuneLiteAPI;
import net.runelite.http.api.hiscore.HiscoreClient;
import net.runelite.http.api.hiscore.HiscoreEndpoint;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

@Extension
@Slf4j
@PluginDescriptor(
        name = "Nom Player Item highlight",
        description = "Highlight player with items",
        tags = {"highlight"},
        type = PluginType.UTILITY
)
public class PlayerItemHighlight extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PlayerItemHighlightConfig config;
    @Inject
    private PlayerItemHighlightOverlay playerItemHighlightOverlay;
    @Inject
    private Client client;
    @Inject
    private EventBus eventBus;

    private static final HiscoreClient hiscoreClient = new HiscoreClient(RuneLiteAPI.CLIENT);


    public PlayerItemHighlight() {
    }

    @Provides
    PlayerItemHighlightConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PlayerItemHighlightConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(playerItemHighlightOverlay);
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(playerItemHighlightOverlay);
    }

    private AtomicBoolean isSearching = new AtomicBoolean();
    @Getter
    private ConcurrentHashMap<String,PlayerStats> playerStatsMap = new ConcurrentHashMap<>();
    @Getter
    private CopyOnWriteArraySet<Player> highlightPlayers = new CopyOnWriteArraySet<>();
    @Subscribe
    public void onGameTick(final GameTick event)
    {
        if (isSearching.get()) return;
        Player local = client.getLocalPlayer();
        if (local == null) return;
        //https://stackoverflow.com/questions/29670116/remove-duplicates-from-a-list-of-objects-based-on-property-in-java-8
        HashSet<Object> seen=new HashSet<>();
        highlightPlayers.removeIf(e->!seen.add(e.getName()));


        for (Player player : client.getPlayers()) {
            if (isSearching.get()) return;
            if (player == null || player.getName() == null) continue;
            if (playerStatsMap.containsKey(player.getName())) continue;

            if (!withinRange(player.getCombatLevel(), local.getCombatLevel(), config)) continue;

            log.info("Starting search "+player.getName());
            isSearching.set(true);
            hiscoreClient.lookupAsync(Text.toJagexName(player.getName()), HiscoreEndpoint.NORMAL).whenCompleteAsync((result, ex) ->
                SwingUtilities.invokeLater(() ->
                {
                    if (result == null || ex != null)
                    {
                        if (ex != null)
                        {
                            log.info("Error fetching Hiscore data " + ex.getMessage());
                        }
                        isSearching.set(false);
                        return;
                    }

                    //successful player search
                    PlayerStats playerStats = new PlayerStats(result, player.getCombatLevel(), local.getCombatLevel());
                    this.playerStatsMap.put(player.getName(), playerStats);
                    highlightPlayers.add(player);
                    log.info("Found " + player.getName() +" Should highlight " + playerStats.shouldHighlight(config));
                    isSearching.set(false);
                }));
        }
    }

    @Subscribe
    public void onPlayerSpawned(PlayerSpawned event)
    {
        final Player local = client.getLocalPlayer();
        final Player player = event.getPlayer();

        if (player.getName() == null) return;
        if (player != local && playerStatsMap.containsKey(player.getName()))
        {
            highlightPlayers.add(player);
        }
    }


    @Subscribe
    public void onPlayerDespawned(PlayerDespawned event)
    {
        final Player local = client.getLocalPlayer();
        final Player player = event.getPlayer();
        if (player == null || player.getName() == null) return;
        highlightPlayers.removeIf(player1 -> player.getName().equals(player1.getName()));
    }

    public static boolean withinRange(int combatLevel, int myCombatLevel, PlayerItemHighlightConfig config) {
        if (config.autoBaseOnCurrentLevel()) {
            return combatLevel >= myCombatLevel - config.ignoreCombatLevelBelow() &&
                    combatLevel <= myCombatLevel + config.ignoreCombatLevelAbove();
        } else {
            return combatLevel >= config.ignoreCombatLevelMin() && combatLevel <= config.ignoreCombatLevelMax();
        }
    }
}
