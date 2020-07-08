package net.torocraft.flighthud;

import net.minecraft.client.MinecraftClient;

public class Dimensions {

  public static final double FRAME_RATIO = 0.6d;

  public int hScreen;
  public int wScreen;
  public double degreesPerPixel;
  public int xMid;
  public int yMid;

  public int wFrame;
  public int hFrame;
  public int lFrame;
  public int rFrame;
  public int tFrame;
  public int bFrame;

  @Deprecated
  public int margin;

  public void update(MinecraftClient client) {
    hScreen = client.getWindow().getScaledHeight();
    wScreen = client.getWindow().getScaledWidth();
    degreesPerPixel = i(hScreen / client.options.fov);
    xMid = i(wScreen / 2);
    yMid = i(hScreen / 2);
    margin = i(wScreen * 0.1d);

    wFrame = i(wScreen * FRAME_RATIO);
    hFrame = i(hScreen * FRAME_RATIO);

    lFrame = i(((double)wScreen - (double)wFrame) / 2);
    rFrame = lFrame + wFrame;

    tFrame = i(((double)hScreen - (double)hFrame) / 2);
    bFrame = tFrame + hFrame;
  }

  private int i(double d) {
    return (int) Math.round(d);
  }
}