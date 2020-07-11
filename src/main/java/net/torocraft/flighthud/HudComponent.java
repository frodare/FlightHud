package net.torocraft.flighthud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public abstract class HudComponent extends AbstractGui {
  public static int COLOR = Color.GREEN.getRGB();
  public static int COLOR_WARN = Color.RED.getRGB();

  public abstract void render(MatrixStack m, float partial, Minecraft mc);

  protected int i(double d) {
    return (int) Math.round(d);
  }

  protected void drawHorizontalLine(MatrixStack m, int x1, int x2, int y, int color) {
    func_238465_a_(m, x1, x2, y, color);
  }

  protected void drawVerticalLine(MatrixStack m, int x, int y1, int y2, int color) {
    func_238473_b_(m, x, y1, y2, color);
  }

  @SuppressWarnings("deprecation")
  protected void drawPointer(MatrixStack m, int x, int y, float rot) {
    GlStateManager.pushMatrix();
    GlStateManager.translated((float) x, (float) y, 0);
    GlStateManager.rotatef(45f + rot, 0, 0, 1f);
    drawVerticalLine(m, 0, 0, 5, COLOR);
    drawHorizontalLine(m, 0, 5, 0, COLOR);
    GlStateManager.popMatrix();
  }

  protected double wrapHeading(double degrees) {
    degrees = degrees % 360;
    while (degrees < 0) {
      degrees += 360;
    }
    return degrees;
  }

  protected void drawFont(Minecraft mc, MatrixStack m, String s, float x, float y) {
    drawFont(mc, m, s, x, y, COLOR);
  }

  protected void drawFont(Minecraft mc, MatrixStack m, String s, float x, float y, int color) {
    mc.fontRenderer.func_238421_b_(m, s, x, y, COLOR);
  }

  protected void drawRightAlignedFont(Minecraft mc, MatrixStack m, String s, int x, int y) {
    int w = mc.fontRenderer.getStringWidth(s);
    drawFont(mc, m, s, x - w, y);
  }

  protected void drawBox(MatrixStack m, int x, int y, int w, int h) {
    drawHorizontalLine(m, x, x + w, y, COLOR);
    drawHorizontalLine(m, x, x + w, y + h, COLOR);
    drawVerticalLine(m, x, y, y + h, COLOR);
    drawVerticalLine(m, x + w, y, y + h, COLOR);
  }

  protected void drawHorizontalLineDashed(MatrixStack m, int x1, int x2, int y, int c,
      int dashCount) {
    int width = x2 - x1;
    int segmentCount = dashCount * 2 - 1;
    double dashSize = width / segmentCount;
    for (int i = 0; i < segmentCount; i++) {
      if (i % 2 != 0) {
        continue;
      }
      int dx1 = i(i * dashSize) + x1;
      int dx2;
      if (i == segmentCount - 1) {
        dx2 = x2;
      } else {
        dx2 = i((i + 1) * dashSize) + x1;
      }
      drawHorizontalLine(m, dx1, dx2, y, c);
    }
  }
}
