package net.torocraft.flighthud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
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
  public void render(MatrixStack m, float partial, MinecraftClient client) {
    if (computer.elytraHealth == null) {
      return;
    }
    TextRenderer fontRenderer = client.textRenderer;
    int x = dim.xMid;
    int y = dim.bFrame + 3;

    drawBox(m, x - 4, y - 2, 30, 10);
    fontRenderer.draw(m, "E", x - 10, y, COLOR);
    fontRenderer.draw(m, String.format("%d", i(computer.elytraHealth)) + "%", x, y, COLOR);
  }
}