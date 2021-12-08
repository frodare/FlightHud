package net.torocraft.flighthud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Matrix4f;
import net.torocraft.flighthud.config.HudConfig;

public abstract class HudComponent extends DrawableHelper {

  public abstract void render(MatrixStack m, float partial, MinecraftClient client);

  public static HudConfig CONFIG;

  protected int i(double d) {
    return (int) Math.round(d);
  }

  protected void drawPointer(MatrixStack m, float x, float y, float rot) {
    m.push();
    m.translate(x, y, 0);
    m.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rot + 45));
    drawVerticalLine(m, 0, 0, 5, CONFIG.color);
    drawHorizontalLine(m, 0, 5, 0, CONFIG.color);
    m.pop();
  }

  protected float wrapHeading(float degrees) {
    degrees = degrees % 360;
    while (degrees < 0) {
      degrees += 360;
    }
    return degrees;
  }

  protected void drawFont(MinecraftClient mc, MatrixStack m, String s, float x, float y) {
    drawFont(mc, m, s, x, y, CONFIG.color);
  }

  protected void drawFont(MinecraftClient mc, MatrixStack m, String s, float x, float y,
      int color) {
    mc.textRenderer.draw(m, s, x, y, CONFIG.color);
  }

  protected void drawRightAlignedFont(MinecraftClient mc, MatrixStack m, String s, float x,
      float y) {
    int w = mc.textRenderer.getWidth(s);
    drawFont(mc, m, s, x - w, y);
  }

  protected void drawBox(MatrixStack m, float x, float y, float w, float h) {
    drawHorizontalLine(m, x, x + w, y);
    drawHorizontalLine(m, x, x + w, y + h);
    drawVerticalLine(m, x, y, y + h);
    drawVerticalLine(m, x + w, y, y + h);
  }

  protected void drawHorizontalLineDashed(MatrixStack m, float x1, float x2, float y,
      int dashCount) {
    float width = x2 - x1;
    int segmentCount = dashCount * 2 - 1;
    float dashSize = width / segmentCount;
    for (int i = 0; i < segmentCount; i++) {
      if (i % 2 != 0) {
        continue;
      }
      float dx1 = i * dashSize + x1;
      float dx2;
      if (i == segmentCount - 1) {
        dx2 = x2;
      } else {
        dx2 = ((i + 1) * dashSize) + x1;
      }
      drawHorizontalLine(m, dx1, dx2, y);
    }
  }

  protected void drawHorizontalLine(MatrixStack matrices, float x1, float x2, float y) {
    if (x2 < x1) {
      float i = x1;
      x1 = x2;
      x2 = i;
    }
    fill(matrices, x1 - CONFIG.halfThickness, y - CONFIG.halfThickness, x2 + CONFIG.halfThickness,
        y + CONFIG.halfThickness);
  }

  protected void drawVerticalLine(MatrixStack matrices, float x, float y1, float y2) {
    if (y2 < y1) {
      float i = y1;
      y1 = y2;
      y2 = i;
    }

    fill(matrices, x - CONFIG.halfThickness, y1 + CONFIG.halfThickness, x + CONFIG.halfThickness,
        y2 - CONFIG.halfThickness);
  }

  public static void fill(MatrixStack matrices, float x1, float y1, float x2, float y2) {
    fill(matrices.peek().getPositionMatrix(), x1, y1, x2, y2);
  }

  private static void fill(Matrix4f matrix, float x1, float y1, float x2, float y2) {
    float j;

    if (x1 < x2) {
      j = x1;
      x1 = x2;
      x2 = j;
    }

    if (y1 < y2) {
      j = y1;
      y1 = y2;
      y2 = j;
    }
    int color = CONFIG.color;
    float alpha = (float) (color >> 24 & 255) / 255.0F;
    float r = (float) (color >> 16 & 255) / 255.0F;
    float g = (float) (color >> 8 & 255) / 255.0F;
    float b = (float) (color & 255) / 255.0F;
    BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
    RenderSystem.enableBlend();
    RenderSystem.disableTexture();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(r, g, b, alpha).next();
    bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, alpha).next();
    bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(r, g, b, alpha).next();
    bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(r, g, b, alpha).next();
    bufferBuilder.end();
    BufferRenderer.draw(bufferBuilder);
    RenderSystem.enableTexture();
    RenderSystem.disableBlend();
  }
}
