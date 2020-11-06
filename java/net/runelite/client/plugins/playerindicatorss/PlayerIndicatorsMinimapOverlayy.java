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

import net.runelite.api.Player;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PlayerIndicatorsMinimapOverlayy extends Overlay
{
	private final PlayerIndicatorsServicee playerIndicatorsServicee;
	private final PlayerIndicatorsPluginn plugin;
	private final PlayerIndicatorsConfigg config;

	@Inject
	private PlayerIndicatorsMinimapOverlayy(final PlayerIndicatorsPluginn plugin, final PlayerIndicatorsConfigg config, final PlayerIndicatorsServicee playerIndicatorsServicee)
	{
		this.plugin = plugin;
		this.config = config;
		this.playerIndicatorsServicee = playerIndicatorsServicee;
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
	}

	private void renderMinimapOverlays(Graphics2D graphics, Player actor, PlayerIndicatorsPluginn.PlayerRelationn relation)
	{
		if (!plugin.getLocationHashMap().containsKey(relation) || actor.getName() == null)
		{
			return;
		}

		final List indicationLocations = Arrays.asList(plugin.getLocationHashMap().get(relation));
		final Color color = plugin.getRelationColorHashMap().get(relation);

		if (indicationLocations.contains(PlayerIndicatorsPluginn.PlayerIndicationLocationn.MINIMAP))
		{
			String name = actor.getName().replace('\u00A0', ' ');
			String tag = "";
			String prefix = "tag_";

			name += tag;

			net.runelite.api.Point minimapLocation = actor.getMinimapLocation();

			if (minimapLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, minimapLocation, name, color);
			}
		}
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		playerIndicatorsServicee.forEachPlayer((player, playerRelationn) -> renderMinimapOverlays(graphics, player, playerRelationn));
		return null;
	}
}
