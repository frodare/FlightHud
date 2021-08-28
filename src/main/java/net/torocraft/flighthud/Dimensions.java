package net.torocraft.flighthud;

import net.minecraft.client.Minecraft;
import net.torocraft.flighthud.config.HudConfig;

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

  public void update(Minecraft client) {
    if (HudComponent.CONFIG == null) {
      return;
    }
    HudConfig c = HudComponent.CONFIG;
    hScreen = client.getWindow().getGuiScaledHeight();
    wScreen = client.getWindow().getGuiScaledWidth();

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