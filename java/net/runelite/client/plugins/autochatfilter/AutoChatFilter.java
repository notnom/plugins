package net.runelite.client.plugins.autochatfilter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;
import org.pf4j.util.StringUtils;

import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.CopyOnWriteArraySet;

import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.events.ConfigChanged;

@Extension
@Slf4j
@PluginDescriptor(
        name = "Nom Auto Chat Filter",
        description = "Auto Chat Filter",
        tags = {"autochat"},
        type = PluginType.UTILITY,
        enabledByDefault = true
)
public class AutoChatFilter extends Plugin {
    @Inject
    private AutoChatFilterConfig config;
    @Inject
    private Client client;

    public AutoChatFilter() {
    }

    @Provides
    AutoChatFilterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoChatFilterConfig.class);
    }

    @Override
    protected void startUp() {
    }

    @Override
    protected void shutDown() {
    }

    private ConcurrentHashMap<String, Instant> chatHistory = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> chatCount = new ConcurrentHashMap<>();
    private CopyOnWriteArraySet<String> spamBots = new CopyOnWriteArraySet<>();
    @Subscribe
    void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if (!"chatFilterCheck".equals(event.getEventName()))
        {
            return;
        }
        int[] intStack = client.getIntStack();
        int intStackSize = client.getIntStackSize();
        int messageType = intStack[intStackSize - 2];
        int messageId = intStack[intStackSize - 1];

        ChatMessageType chatMessageType = ChatMessageType.of(messageType);

        //Only filter public chat
        switch (chatMessageType)
        {
            case PUBLICCHAT:
            case MODCHAT:
            case AUTOTYPER:
                break;
            default:
                return;
        }
        if (config.filterAutochat() && chatMessageType == ChatMessageType.AUTOTYPER) {
            intStack[intStackSize - 3] = 0;
            return;
        }
        if (!config.filterBotSpam()) return;
        MessageNode messageNode = (MessageNode)client.getMessages().get(messageId);
        String message = messageNode.getValue();
        String name = messageNode.getName();
        if (StringUtils.isNullOrEmpty(message) || StringUtils.isNullOrEmpty(name)) return;

        if (spamBots.contains(name)) {
            intStack[intStackSize - 3] = 0;
            return;
        }


        if (chatCount.containsKey(name+message) && chatCount.get(name+message) >= config.repeatCount()) {
            intStack[intStackSize - 3] = 0;
            log.info("Filtering " + name+message);
        }

        if (chatHistory.containsKey(name+message) && chatHistory.get(name+message).isBefore(Instant.now())) {
            intStack[intStackSize - 3] = 0;
            log.info("Filtering " + name+message);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        MessageNode messageNode = chatMessage.getMessageNode();
        ChatMessageType chatMessageType = chatMessage.getType();
        //Only filter public chat
        switch (chatMessageType)
        {
            case PUBLICCHAT:
            case MODCHAT:
            case AUTOTYPER:
                break;
            default:
                return;
        }

        String message = messageNode.getValue();
        String name = messageNode.getName();
//        log.info(name+message+"chatmessageevent FUKKKKKKKKK");
        if (StringUtils.isNullOrEmpty(message) || StringUtils.isNullOrEmpty(name)) return;
        String combined = name+message;

        String jagexName = Text.toJagexName(name);
        if (jagexName.equals("8ald")) {
            log.info(name + " " + message);
        }
        chatCount.merge(combined,1,Integer::sum);
        if (chatHistory.containsKey(combined) && chatCount.containsKey(combined)) {
            Instant lastTime = chatHistory.get(combined);
            int count = chatCount.get(combined);
            if (jagexName.equals("8ald")) {
                log.info(combined + " " + lastTime.toString());
            }

            if (lastTime.plusSeconds(config.duplicateDelay()).isBefore(Instant.now())) {
//                chatHistory.remove(combined);
                chatCount.put(combined,0);
            } else if (count > config.repeatCount()) {
                chatHistory.put(combined, Instant.now());
            }
        } else {
            chatHistory.put(combined, Instant.now().plusSeconds(config.duplicateDelay()));
        }
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event)
    {
        if (event == null) return;
        if (!(event.getActor() instanceof Player))
        {
            return;
        }

        Player p = (Player)event.getActor();

        String name = p.getName();
        String message = event.getOverheadText();
        if (name == null || message == null) return;

        if (p.getCombatLevel() < config.combatLevelMin() || p.getCombatLevel() > config.combatLevelMax()) {
            spamBots.add(name);
        }
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged e)
    {
        if (e.getGroup().equals("autochatfilter")) {
            if (e.getKey().contains("combatLevel")) {
                spamBots.clear();
            }
        }
    }

}
