package net.torocraft.flighthud.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class PitchIndicator extends HudComponent {
  private final Dimensions dim;
  private final FlightComputer computer;
  private final PitchIndicatorData pitchData = new PitchIndicatorData();

  public PitchIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(MatrixStack m, float partial, Minecraft mc) {
    pitchData.update(dim);

    int horizonOffset = i(computer.pitch * dim.degreesPerPixel);
    int yHorizon = dim.yMid + horizonOffset;

    for (int i = 20; i <= 90; i = i + 20) {
      int offset = i(dim.degreesPerPixel * i);
      drawDegreeBar(mc, m, -i, yHorizon + offset);
      drawDegreeBar(mc, m, i, yHorizon - offset);
    }

    pitchData.l1 -= pitchData.margin;
    pitchData.r2 += pitchData.margin;
    drawDegreeBar(mc, m, 0, yHorizon);

  }

  private void drawDegreeBar(Minecraft mc, MatrixStack m, int degree, int y) {

    if (y < dim.tFrame || y > dim.bFrame) {
      return;
    }

    int dashes = degree < 0 ? 4 : 1;

    drawHorizontalLineDashed(m, pitchData.l1, pitchData.l2, y, COLOR, dashes);
    drawHorizontalLineDashed(m, pitchData.r1, pitchData.r2, y, COLOR, dashes);

    if (degree == 0) {
      int width = i((pitchData.l2 - pitchData.l1) * 0.25d);
      int l1 = pitchData.l2 - width;
      int r2 = pitchData.r1 + width;
      drawHorizontalLineDashed(m, l1, pitchData.l2, y + 3, COLOR, 3);
      drawHorizontalLineDashed(m, pitchData.r1, r2, y + 3, COLOR, 3);
      drawHorizontalLineDashed(m, l1, pitchData.l2, y + 6, COLOR, 3);
      drawHorizontalLineDashed(m, pitchData.r1, r2, y + 6, COLOR, 3);
      return;
    }

    int sideTickHeight = degree >= 0 ? 5 : -5;
    drawVerticalLine(m, pitchData.l1, y, y + sideTickHeight, COLOR);
    drawVerticalLine(m, pitchData.r2, y, y + sideTickHeight, COLOR);

    int fontVerticalOffset = degree >= 0 ? 0 : 6;

    drawFont(mc, m, String.format("%d", Math.abs(degree)), pitchData.r2 + 6,
        (float) y - fontVerticalOffset);

    drawFont(mc, m, String.format("%d", Math.abs(degree)), pitchData.l1 - 17,
        (float) y - fontVerticalOffset);
  }

  private static class PitchIndicatorData {
    public int width;
    public int mid;
    public int margin;
    public int sideWidth;
    public int l1;
    public int l2;
    public int r1;
    public int r2;

    public void update(Dimensions dim) {
      width = i(dim.wScreen / 3);
      int left = width;

      mid = i((width / 2) + left);
      margin = i(width * 0.3d);
      l1 = left + margin;
      l2 = mid - 7;
      sideWidth = l2 - l1;
      r1 = mid + 7;
      r2 = r1 + sideWidth;
    }

    private int i(double d) {
      return (int) Math.round(d);
    }
  }

}
