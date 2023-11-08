package net.torocraft.flighthud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.torocraft.flighthud.config.HudConfig;

public abstract class HudComponent extends GuiComponent {

  public abstract void render(PoseStack m, float partial, Minecraft client);

  public static HudConfig CONFIG;

  protected int i(double d) {
    return (int) Math.round(d);
  }

  protected void drawPointer(PoseStack m, float x, float y, float rot) {
    m.pushPose();
    m.translate(x, y, 0);
    m.mulPose(Vector3f.ZP.rotationDegrees(rot + 45));
    vLine(m, 0, 0, 5, CONFIG.color);
    hLine(m, 0, 5, 0, CONFIG.color);
    m.popPose();
  }

  protected float wrapHeading(float degrees) {
    degrees = degrees % 360;
    while (degrees < 0) {
      degrees += 360;
    }
    return degrees;
  }

  protected void drawFont(Minecraft mc, PoseStack m, String s, float x, float y) {
    drawFont(mc, m, s, x, y, CONFIG.color);
  }

  protected void drawFont(Minecraft mc, PoseStack m, String s, float x, float y,
      int color) {
    mc.font.draw(m, s, x, y, CONFIG.color);
  }

  protected void drawRightAlignedFont(Minecraft mc, PoseStack m, String s, float x,
      float y) {
    int w = mc.font.width(s);
    drawFont(mc, m, s, x - w, y);
  }

  protected void drawBox(PoseStack m, float x, float y, float w, float h) {
    drawHorizontalLine(m, x, x + w, y);
    drawHorizontalLine(m, x, x + w, y + h);
    drawVerticalLine(m, x, y, y + h);
    drawVerticalLine(m, x + w, y, y + h);
  }

  protected void drawHorizontalLineDashed(PoseStack m, float x1, float x2, float y,
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

  protected void drawHorizontalLine(PoseStack matrices, float x1, float x2, float y) {
    if (x2 < x1) {
      float i = x1;
      x1 = x2;
      x2 = i;
    }
    fill(matrices, x1 - CONFIG.halfThickness, y - CONFIG.halfThickness, x2 + CONFIG.halfThickness,
        y + CONFIG.halfThickness);
  }

  protected void drawVerticalLine(PoseStack matrices, float x, float y1, float y2) {
    if (y2 < y1) {
      float i = y1;
      y1 = y2;
      y2 = i;
    }

    fill(matrices, x - CONFIG.halfThickness, y1 + CONFIG.halfThickness, x + CONFIG.halfThickness,
        y2 - CONFIG.halfThickness);
  }


  public static void fill(PoseStack matrices, float x1, float y1, float x2, float y2) {
    fill(matrices.last().pose(), x1, y1, x2, y2);
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
    BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
    RenderSystem.enableBlend();
    RenderSystem.disableTexture();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(r, g, b, alpha).endVertex();
    bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, alpha).endVertex();
    bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(r, g, b, alpha).endVertex();
    bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(r, g, b, alpha).endVertex();
    //bufferBuilder.end();

    //BufferUploader.end(bufferBuilder);
    BufferUploader.drawWithShader(bufferBuilder.end());

    RenderSystem.enableTexture();
    RenderSystem.disableBlend();
  }
}
