package net.torocraft.flighthud.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class ElytraHealthIndicator extends HudComponent {

  private final Dimensions dim;
  private final FlightComputer computer;

  public ElytraHealthIndicator(FlightComputer computer, Dimensions dim) {
    this.dim = dim;
    this.computer = computer;
  }

  @Override
  public void render(MatrixStack m, float partial, Minecraft mc) {
    if (computer.elytraHealth == null) {
      return;
    }
    int x = dim.xMid;
    int y = dim.bFrame + 3;

    drawBox(m, x - 4, y - 2, 30, 10);
    drawFont(mc, m, "E", x - 10, y);
    drawFont(mc, m, String.format("%d", i(computer.elytraHealth)) + "%", x, y);
  }
}