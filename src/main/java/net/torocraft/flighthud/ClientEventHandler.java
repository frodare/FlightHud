package net.torocraft.flighthud;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = FlightHud.MODID, bus = EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT)
public class ClientEventHandler {
  private static final HudRenderer hud = new HudRenderer();

  @SubscribeEvent
  public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
    hud.render(event.getPoseStack(), event.getPartialTick(), Minecraft.getInstance());
  }
}
