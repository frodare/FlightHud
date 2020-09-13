package net.torocraft.flighthud;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class SwitchDisplayModeCommand implements Command<ServerCommandSource> {

  @Override
  public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    FlightHud.CONFIG_SETTINGS.toggleDisplayMode();
    return 0;
  }

}
