package net.torocraft.flighthud;

import net.minecraft.client.MinecraftClient;
import net.torocraft.flighthud.config.Config;

public class Dimensions {

  public float hScreen;
  public float wScreen;
  public float degreesPerPixel;
  public float xMid;
  public float yMid;

  public float wFrame;
  public float hFrame;
  public float lFrame;
  public float rFrame;
  public float tFrame;
  public float bFrame;

  public void update(MinecraftClient client) {
    Config c = FlightHud.CONFIG;
    hScreen = client.getWindow().getScaledHeight();
    wScreen = client.getWindow().getScaledWidth();

    if (c.scale != 1d && c.scale > 0) {
      hScreen = hScreen * c.scale;
      wScreen = wScreen * c.scale;
    }

    degreesPerPixel = (float) (hScreen / client.options.fov);
    xMid = wScreen / 2;
    yMid = hScreen / 2;

    wFrame = wScreen * c.width;
    hFrame = hScreen * c.height;

    lFrame = ((wScreen - wFrame) / 2) + c.xOffset;
    rFrame = lFrame + wFrame;

    tFrame = ((hScreen - hFrame) / 2) + c.yOffset;
    bFrame = tFrame + hFrame;
  }

}