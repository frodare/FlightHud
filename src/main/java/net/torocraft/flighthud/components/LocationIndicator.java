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
    int x = dim.lFrame;
    int y = dim.bFrame + 2;

    int xLoc = mc.player.getBlockPos().getX();
    int zLoc = mc.player.getBlockPos().getZ();

    drawFont(mc, m, String.format("%d / %d", xLoc, zLoc), x, y);
  }
}