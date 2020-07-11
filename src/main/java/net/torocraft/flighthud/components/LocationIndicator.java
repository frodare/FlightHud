package net.torocraft.flighthud.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class LocationIndicator extends HudComponent {

  private final Dimensions dim;

  public LocationIndicator(Dimensions dim) {
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, Minecraft mc) {
    int x = dim.lFrame;
    int y = dim.bFrame + 2;

    int xLoc = FlightComputer.getPlayerPos(mc).getX();
    int zLoc = FlightComputer.getPlayerPos(mc).getZ();

    drawFont(mc, m, String.format("%d / %d", xLoc, zLoc), x, y);
  }
}