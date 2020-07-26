package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.HudComponent;

public class LocationIndicator extends HudComponent {

  private final Dimensions dim;

  public LocationIndicator(Dimensions dim) {
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, MinecraftClient mc) {
    if (!CONFIG.location_showReadout) {
      return;
    }

    float x = dim.wScreen * CONFIG.location_x;
    float y = dim.hScreen * CONFIG.location_y;

    int xLoc = mc.player.getBlockPos().getX();
    int zLoc = mc.player.getBlockPos().getZ();

    drawFont(mc, m, String.format("%d / %d", xLoc, zLoc), x, y);
  }
}
