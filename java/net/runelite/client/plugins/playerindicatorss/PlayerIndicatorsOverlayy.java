/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class PlayerIndicatorsOverlayy extends Overlay
{
	private static final int ACTOR_OVERHEAD_TEXT_MARGIN = 40;
	private static final int ACTOR_HORIZONTAL_TEXT_MARGIN = 10;

	private final PlayerIndicatorsPluginn plugin;
	private final PlayerIndicatorsConfigg config;
	private final PlayerIndicatorsServicee playerIndicatorsServicee;

	@Inject
	private Client client;

	@Inject
	public PlayerIndicatorsOverlayy(PlayerIndicatorsPluginn plugin, PlayerIndicatorsConfigg config, PlayerIndicatorsServicee playerIndicatorsServicee)
	{
		this.plugin = plugin;
		this.config = config;
		this.playerIndicatorsServicee = playerIndicatorsServicee;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		playerIndicatorsServicee.forEachPlayer((player, playerRelationn) -> drawSceneOverlays(graphics, player, playerRelationn));
		return null;
	}

	private void drawSceneOverlays(Graphics2D graphics, Player actor, PlayerIndicatorsPluginn.PlayerRelationn relation)
	{
		if (actor.getName() == null || !plugin.getLocationHashMap().containsKey(relation))
		{
			return;
		}

		final List indicationLocations = Arrays.asList(plugin.getLocationHashMap().get(relation));
		final Color color = plugin.getRelationColorHashMap().get(relation);
		final String name = actor.getName();
		final int zOffset = actor.getLogicalHeight() + ACTOR_OVERHEAD_TEXT_MARGIN;
		final Point textLocation = actor.getCanvasTextLocation(graphics, name, zOffset);

		if (indicationLocations.contains(PlayerIndicatorsPluginn.PlayerIndicationLocationn.ABOVE_HEAD))
		{
			final StringBuilder nameSb = new StringBuilder(name);
			final String builtString = nameSb.toString();
			final int x = graphics.getFontMetrics().stringWidth(builtString);
			final int y = graphics.getFontMetrics().getHeight();

			OverlayUtil.renderActorTextOverlay(graphics, actor, builtString, color);
		}
		if (actor.getConvexHull() != null && indicationLocations.contains(PlayerIndicatorsPluginn.PlayerIndicationLocationn.HULL))
		{
			OverlayUtil.renderPolygon(graphics, actor.getConvexHull(), color);
		}

		if (indicationLocations.contains(PlayerIndicatorsPluginn.PlayerIndicationLocationn.TILE))
		{
			if (actor.getCanvasTilePoly() != null)
			{
				OverlayUtil.renderPolygon(graphics, actor.getCanvasTilePoly(), color);
			}
		}
	}

}
