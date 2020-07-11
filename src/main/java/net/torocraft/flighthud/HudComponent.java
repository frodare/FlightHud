package net.torocraft.flighthud;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public abstract class HudComponent extends DrawableHelper {
  public static int COLOR = Color.GREEN.getRGB();
  public static int COLOR_WARN = Color.RED.getRGB();

  public abstract void render(MatrixStack m, float partial, MinecraftClient client);

  protected int i(double d) {
    return (int) Math.round(d);
  }

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

  protected void drawFont(MinecraftClient mc, MatrixStack m, String s, float x, float y) {
    drawFont(mc, m, s, x, y, COLOR);
  }

  protected void drawFont(MinecraftClient mc, MatrixStack m, String s, float x, float y, int color) {
    mc.textRenderer.draw(m, s, x, y, COLOR);
  }

  protected void drawRightAlignedFont(MinecraftClient mc, MatrixStack m, String s, int x,
      int y) {
    int w = mc.textRenderer.getWidth(s);
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
