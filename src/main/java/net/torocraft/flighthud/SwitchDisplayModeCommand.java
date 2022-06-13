package net.torocraft.flighthud;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class SwitchDisplayModeCommand implements Command<FabricClientCommandSource> {

  @Override
  public int run(CommandContext<FabricClientCommandSource> context) {
    FlightHud.CONFIG_SETTINGS.toggleDisplayMode();
    return 0;
  }

}
