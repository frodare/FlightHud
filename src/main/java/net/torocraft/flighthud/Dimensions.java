package net.torocraft.flighthud;

import net.minecraft.client.MinecraftClient;

public class Dimensions {
  public int height;
  public int width;
  public int xMid;
  public int yMid;
  public double degreesPerPixel;
  public int margin;

  public void update(MinecraftClient client) {
    height = client.getWindow().getScaledHeight();
    width = client.getWindow().getScaledWidth();
    degreesPerPixel = i(height / client.options.fov);
    xMid = i(width / 2);
    yMid = i(height / 2);
    margin = i(width * 0.1d);
  }

  private int i(double d) {
    return (int) Math.round(d);
  }
}