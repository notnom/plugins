/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.playerindicatorss;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.runelite.api.MenuOpcode.*;

@Extension
@PluginDescriptor(
	name = "Nom Player Indicators",
	description = "Highlight players on-screen and/or on the minimap",
	tags = {"highlight", "minimap", "overlay", "players", "pklite"},
	type = PluginType.UTILITY
)
@Getter(AccessLevel.PACKAGE)
public class PlayerIndicatorsPluginn extends Plugin
{
	private final Map<Player, PlayerRelationn> colorizedMenus = new ConcurrentHashMap<>();
	private final Map<PlayerRelationn, Color> relationColorHashMap = new ConcurrentHashMap<>();
	private final Map<PlayerRelationn, Object[]> locationHashMap = new ConcurrentHashMap<>();
	@Inject
	@Getter(AccessLevel.NONE)
	private OverlayManager overlayManager;

	@Inject
	@Getter(AccessLevel.NONE)
	private PlayerIndicatorsConfigg config;

	@Inject
	@Getter(AccessLevel.NONE)
	private PlayerIndicatorsOverlayy playerIndicatorsOverlayy;

	@Inject
	@Getter(AccessLevel.NONE)
	private PlayerIndicatorsMinimapOverlayy playerIndicatorsMinimapOverlayy;

	@Inject
	@Getter(AccessLevel.NONE)
	private Client client;

	@Provides
	PlayerIndicatorsConfigg provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlayerIndicatorsConfigg.class);
	}

	@Override
	protected void startUp()
	{
		updateConfig();
		overlayManager.add(playerIndicatorsOverlayy);
		overlayManager.add(playerIndicatorsMinimapOverlayy);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(playerIndicatorsOverlayy);
		overlayManager.remove(playerIndicatorsMinimapOverlayy);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("playerindicatorss"))
		{
			return;
		}

		updateConfig();
	}


	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded menuEntryAdded)
	{
		int type = menuEntryAdded.getOpcode();

		if (type >= 2000)
		{
			type -= 2000;
		}

		int identifier = menuEntryAdded.getIdentifier();
		if (type == FOLLOW.getId() || type == TRADE.getId()
			|| type == SPELL_CAST_ON_PLAYER.getId() || type == ITEM_USE_ON_PLAYER.getId()
			|| type == PLAYER_FIRST_OPTION.getId()
			|| type == PLAYER_SECOND_OPTION.getId()
			|| type == PLAYER_THIRD_OPTION.getId()
			|| type == PLAYER_FOURTH_OPTION.getId()
			|| type == PLAYER_FIFTH_OPTION.getId()
			|| type == PLAYER_SIXTH_OPTION.getId()
			|| type == PLAYER_SEVENTH_OPTION.getId()
			|| type == PLAYER_EIGTH_OPTION.getId()
			|| type == RUNELITE.getId())
		{
			final Player localPlayer = client.getLocalPlayer();
			final Player[] players = client.getCachedPlayers();
			Player player = null;

			if (identifier >= 0 && identifier < players.length)
			{
				player = players[identifier];
			}

			if (player == null)
			{
				return;
			}

			int image = -1;
			int image2 = -1;
			Color color = null;


			//NOM
			if (config.highlightFriends() && isFriend(player))
			{
				if (Arrays.asList(this.locationHashMap.get(PlayerRelationn.FRIEND)).contains(PlayerIndicationLocationn.MENU))
				{
					color = relationColorHashMap.get(PlayerRelationn.FRIEND);
				}
			}
			if (color != null)
			{
				final MenuEntry[] menuEntries = client.getMenuEntries();
				final MenuEntry lastEntry = menuEntries[menuEntries.length - 1];

					// strip out existing <col...
					String target = lastEntry.getTarget();
					final int idx = target.indexOf('>');
					if (idx != -1)
					{
						target = target.substring(idx + 1);
					}

					lastEntry.setTarget(ColorUtil.prependColorTag(target, color));
				client.setMenuEntries(menuEntries);
			}
		}
	}

	public boolean isFriend(Player player)
	{
		if (player == null || player.getName() == null) return false;
		return client.isFriended(player.getName(), false) ||
		friendList.stream().anyMatch(s -> Text.toJagexName(player.getName()).equalsIgnoreCase(s));
	}

	@Getter
	private List<String> friendList = new CopyOnWriteArrayList<>();
	private void updateConfig()
	{
		locationHashMap.clear();
		relationColorHashMap.clear();

		if (config.highlightFriends())
		{
			friendList = Text.fromCSV(config.getFriendList());
			relationColorHashMap.put(PlayerRelationn.FRIEND, config.getFriendColor());
			if (config.friendIndicatorMode() != null)
			{
				locationHashMap.put(PlayerRelationn.FRIEND, config.friendIndicatorMode().toArray());
			}
		}
	}

	public enum PlayerIndicationLocationn
	{
		/**
		 * Indicates the player by rendering their username above their head
		 */
		ABOVE_HEAD,
		/**
		 * Indicates the player by outlining the player model's hull.
		 * NOTE: this may cause FPS lag if enabled for lots of players
		 */
		HULL,
		/**
		 * Indicates the player by rendering their username on the minimap
		 */
		MINIMAP,
		/**
		 * Indicates the player by colorizing their right click menu
		 */
		MENU,
		/**
		 * Indicates the player by rendering a tile marker underneath them
		 */
		TILE
	}

	public enum PlayerRelationn
	{
		FRIEND
	}
}
