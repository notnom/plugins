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

import net.runelite.client.config.*;

import java.awt.*;
import java.util.EnumSet;

@ConfigGroup("playerindicatorss")
public interface PlayerIndicatorsConfigg extends Config
{
	EnumSet<PlayerIndicatorsPluginn.PlayerIndicationLocationn> defaultPlayerIndicatorMode = EnumSet.complementOf(EnumSet.of(PlayerIndicatorsPluginn.PlayerIndicationLocationn.HULL));


	@ConfigSection(
		name = "Friends",
		description = "",
		position = 1,
		keyName = "friendsSection"
	)
	default boolean friendsSection()
	{
		return false;
	}
	@ConfigItem(
		position = 0,
		keyName = "drawFriendNames",
		name = "Highlight friends",
		description = "Configures whether or not friends should be highlighted",
		section = "friendsSection"
	)
	default boolean highlightFriends()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "friendNameColor",
		name = "Friend color",
		description = "Color of friend names",
		section = "friendsSection"
	)
	default Color getFriendColor()
	{
		return new Color(0, 200, 83);
	}

	@ConfigItem(
		position = 2,
		keyName = "friendIndicatorMode",
		name = "Indicator Mode",
		description = "Location(s) of the overlay",
		section = "friendsSection",
		enumClass = PlayerIndicatorsPluginn.PlayerIndicationLocationn.class

	)
	default EnumSet<PlayerIndicatorsPluginn.PlayerIndicationLocationn> friendIndicatorMode()
	{
		return defaultPlayerIndicatorMode;
	}

	@ConfigItem(
			position = 3,
			keyName = "friendList",
			name = "Friend List",
			description = "Configures friends. Format: (item), (item)",
			titleSection = "friendList"
	)
	default String getFriendList()
	{
		return "Woox, A Friend, Jagex";
	}
}
