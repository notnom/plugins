package net.runelite.client.plugins.oddscalc.input;

import net.runelite.api.Client;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.ThreadLocalRandom;

public class InputSender {
   private final Client client;

   @Inject
   public InputSender(Client client) {
      this.client = client;
   }

   public void moveMouse(int x, int y) {
      MouseEvent mouseMoved;
      if (!this.isOnCanvas(this.client.getMouseCanvasPosition().getX(), this.client.getMouseCanvasPosition().getY()) && this.isOnCanvas(x, y)) {
         mouseMoved = new MouseEvent(this.client.getCanvas(), 504, System.currentTimeMillis(), 0, x, y, 0, false);
         this.client.getCanvas().dispatchEvent(mouseMoved);
      }

      if (this.isOnCanvas(this.client.getMouseCanvasPosition().getX(), this.client.getMouseCanvasPosition().getY()) && !this.isOnCanvas(x, y)) {
         mouseMoved = new MouseEvent(this.client.getCanvas(), 505, System.currentTimeMillis(), 0, x, y, 0, false);
         this.client.getCanvas().dispatchEvent(mouseMoved);
      }

      if (this.isOnCanvas(this.client.getMouseCanvasPosition().getX(), this.client.getMouseCanvasPosition().getY()) && this.isOnCanvas(x, y)) {
         mouseMoved = new MouseEvent(this.client.getCanvas(), 503, System.currentTimeMillis(), 0, x, y, 0, false);
         this.client.getCanvas().dispatchEvent(mouseMoved);
      }

   }

   public void leftClick() {
      MouseEvent mousePressed = new MouseEvent(this.client.getCanvas(), 501, System.currentTimeMillis(), 0, this.client.getMouseCanvasPosition().getX(), this.client.getMouseCanvasPosition().getY(), 1, false, 1);
      this.client.getCanvas().dispatchEvent(mousePressed);
      MouseEvent mouseReleased = new MouseEvent(this.client.getCanvas(), 502, System.currentTimeMillis(), 0, this.client.getMouseCanvasPosition().getX(), this.client.getMouseCanvasPosition().getY(), 1, false, 1);
      this.client.getCanvas().dispatchEvent(mouseReleased);
      MouseEvent mouseClicked = new MouseEvent(this.client.getCanvas(), 500, System.currentTimeMillis(), 0, this.client.getMouseCanvasPosition().getX(), this.client.getMouseCanvasPosition().getY(), 1, false, 1);
      this.client.getCanvas().dispatchEvent(mouseClicked);
   }

   private boolean isOnCanvas(int x, int y) {
      return x > 0 && x < this.client.getCanvas().getWidth() && y > 0 && y < this.client.getCanvas().getHeight();
   }

   public void leftClick(Rectangle rect) {
      if (rect != null) {
         int rx = (int)Math.round(ThreadLocalRandom.current().nextDouble(rect.getMinX() + 1.0D, rect.getMaxX() - 1.0D));
         int ry = (int)Math.round(ThreadLocalRandom.current().nextDouble(rect.getMinY() + 1.0D, rect.getMaxY() - 1.0D));
         this.moveMouse(rx, ry);
         this.leftClick();
      }

   }
}
