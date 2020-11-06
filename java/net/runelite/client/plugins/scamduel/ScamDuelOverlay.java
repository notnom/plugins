/*
 * Copyright (c) 2018 kulers
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
package net.runelite.client.plugins.scamduel;

import net.runelite.api.ItemDefinition;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Singleton
public class ScamDuelOverlay extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final ScamDuelPlugin plugin;

	@Inject
	private ScamDuelOverlay(final ItemManager itemManager, final ScamDuelPlugin plugin)
	{
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.showOnInterfaces(481);
	}


	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{

		if (System.currentTimeMillis() % 4000 < 2000) return;

		ItemDefinition id = itemManager.getItemDefinition(itemId);
		if (id == null) return;
		if (plugin.getConfig().getScamColor() == null) return;
		if (plugin.getHiddenItemList().contains(id.getName().toLowerCase())) {
			Rectangle bounds = itemWidget.getCanvasBounds();
			final BufferedImage outline = itemManager.getItemOutline(itemId, itemWidget.getQuantity(), plugin.getConfig().getScamColor());
			graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
		}
	}
}
