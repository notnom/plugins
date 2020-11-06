package net.runelite.client.plugins.dueloptions;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.ChallengeWidgets;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Extension
@Slf4j
@PluginDescriptor(
        name = "Nom Duel Notifier",
        description = "Highlight screen when a duel request is received",
        tags = {"highlight", "duel", "notifier", "loudpacks"},
        type = PluginType.UTILITY
)
public class DuelNotifierPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private DuelNotifierConfig config;
    @Inject
    private DuelNotifierOverlay duelNotifierOverlay;
    @Inject
    private ChatPidOverlay chatPidOverlay;
    @Inject
    private Client client;
    @Inject
    private EventBus eventBus;

    private List<String> ignoredUserList = new CopyOnWriteArrayList<>();

    public DuelNotifierPlugin() {
    }

    @Provides
    DuelNotifierConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(DuelNotifierConfig.class);
    }

    @Override
    protected void startUp() {
        this.overlayManager.add(duelNotifierOverlay);
        this.overlayManager.add(chatPidOverlay);
        ignoredUserList = Text.fromCSV(config.getIgnoredUsers());
    }

    @Override
    protected void shutDown() {
        this.overlayManager.remove(duelNotifierOverlay);
        this.overlayManager.remove(chatPidOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.CHALREQ_TRADE && event.getMessage().toLowerCase().endsWith("duel with you.")) {
//            if (shouldFilterPlayerMessage(event.getName()) || shouldFilterPlayerMessage(event.getSender())) return;
            log.info(event.getSender());
            log.info(event.getName());
            log.info(event.getMessageNode().getName());
            if (shouldFilterPlayerMessage(event.getMessage())) return;
            duelNotifierOverlay.startTimer();
        }
        if (event.getType() == ChatMessageType.TRADEREQ || event.getType() == ChatMessageType.TRADE_SENT) {
            if (shouldFilterPlayerMessage(event.getMessage())) return;
            duelNotifierOverlay.startTimer();
        }
    }

    @Subscribe
    public void onWidgetHiddenChanged(WidgetHiddenChanged event) {
        if (event.getWidget().equals(this.client.getWidget(ChallengeWidgets.RULE_SCREEN_GROUP_ID, ChallengeWidgets.RuleScreen.ACCEPT_BUTTON))) {
            duelNotifierOverlay.resetTimer();
        }

    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == ChallengeWidgets.RULE_SCREEN_GROUP_ID) {
            duelNotifierOverlay.resetTimer();
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("duelnotifier"))
        {
            client.refreshChat();
            ignoredUserList = Text.fromCSV(config.getIgnoredUsers());
            log.info(config.getIgnoredUsers());
        }
    }

    @Subscribe
    void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if (!"chatFilterCheck".equals(event.getEventName()))
        {
            return;
        }
//        log.info("Chat filter event");
        int[] intStack = client.getIntStack();
        int intStackSize = client.getIntStackSize();
        int messageType = intStack[intStackSize - 2];
        int messageId = intStack[intStackSize - 1];

        ChatMessageType chatMessageType = ChatMessageType.of(messageType);

        // Only filter public chat and private messages
//        switch (chatMessageType)
//        {
//            case PUBLICCHAT:
//            case MODCHAT:
//            case AUTOTYPER:
//                intStack[intStackSize - 3] = 0;
//            case PRIVATECHAT:
//            case MODPRIVATECHAT:
//            case FRIENDSCHAT:
//            case GAMEMESSAGE:
//                break;
//            case LOGINLOGOUTNOTIFICATION:
//                intStack[intStackSize - 3] = 0;
//                return;
//            default:
//                return;
//        }

        MessageNode messageNode = (MessageNode)client.getMessages().get(messageId);
        String name = messageNode.getName();

        if (client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null && client.getLocalPlayer().getName().equals(messageNode.getName()))
        {
            return;
        }

        String[] stringStack = client.getStringStack();
        int stringStackSize = client.getStringStackSize();

        String message = stringStack[stringStackSize - 1];
//        log.info("Chat message " + name+ " " + message);
        if (shouldFilterPlayerName(name))
        {
            // Block the message
            intStack[intStackSize - 3] = 0;
        }
    }

    @Subscribe
    private void onOverheadTextChanged(OverheadTextChanged event)
    {
//        log.info(event.getActor().getName() + " overhead text" + event.getActor().getOverheadText());
        if (shouldFilterPlayerName(event.getActor().getName()))
        event.getActor().setOverheadText(" ");
    }

    boolean shouldFilterPlayerName(String playerName)
    {
        return ignoredUserList.stream().anyMatch(s -> Text.toJagexName(playerName).equalsIgnoreCase(s));
    }

    boolean shouldFilterPlayerMessage(String message)
    {
        return ignoredUserList.stream().anyMatch(s -> Text.toJagexName(message).contains(s));
    }
}
