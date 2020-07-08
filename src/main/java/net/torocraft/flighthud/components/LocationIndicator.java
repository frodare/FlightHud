package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.HudComponent;

public class LocationIndicator extends HudComponent {

  private final Dimensions dim;

  public LocationIndicator(Dimensions dim) {
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient client) {
    TextRenderer fontRenderer = client.textRenderer;
   
    int x = dim.margin;
    int y = dim.height - dim.margin;

    int xLoc = client.player.getBlockPos().getX();
    int zLoc = client.player.getBlockPos().getZ();

    fontRenderer.draw(m, String.format("%d / %d", xLoc, zLoc), x, y, COLOR);
  }
}