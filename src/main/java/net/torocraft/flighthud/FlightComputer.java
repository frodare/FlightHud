package net.torocraft.flighthud;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FlightComputer {
  private static final double TICKS_PER_SECOND = 20;

  public Vec3d velocity;
  public double speed;
  public double pitch;
  public double heading;
  public Vec3d flightPath;
  public double flightPitch;
  public double flightHeading;
  public double roll;
  public double altitude;
  public Integer groundLevel;
  public Double distanceFromGround;
  public Double elytraHealth;

  public void update(MinecraftClient client, float partial) {
    velocity = client.player.getVelocity();
    pitch = computePitch(client, partial);
    speed = computeSpeed(client);
    roll = computeRoll(client);
    heading = computeHeading(client);
    altitude = computeAltitude(client);
    groundLevel = computeGroundLevel(client);
    distanceFromGround = computeDistanceFromGround(client, altitude, groundLevel);
    flightPitch = computeFlightPitch(velocity, pitch);
    flightHeading = computeFlightHeading(velocity, heading);
    elytraHealth = computeElytraHealth(client);
  }

  private Double computeElytraHealth(MinecraftClient client) {
    ItemStack stack = client.player.getEquippedStack(EquipmentSlot.CHEST);
    if (stack != null && stack.getItem() == Items.ELYTRA) {
      double remain = ((double)stack.getMaxDamage() - (double)stack.getDamage()) / (double)stack.getMaxDamage();
      return remain * 100d;
    }
    return null;
  }

  private static double computeFlightPitch(Vec3d velocity, double pitch) {
    if (velocity.length() < 0.01) {
      return pitch;
    }
    Vec3d n = velocity.normalize();
    return 90 - Math.toDegrees(Math.acos(n.y));
  }

  private static double computeFlightHeading(Vec3d velocity, double heading) {
    if (velocity.length() < 0.01) {
      return heading;
    }
    return toHeading(Math.toDegrees(-Math.atan2(velocity.x, velocity.z)));
  }

  private static double computeRoll(MinecraftClient client) {
    return Math.toDegrees(client.player.elytraRoll);
  }

  private static double computePitch(MinecraftClient client, float parital) {
    return client.player.getPitch(parital) * -1;
  }

  private static boolean isGround(BlockPos pos, MinecraftClient client) {
    BlockState block = client.world.getBlockState(pos);
    return !block.isAir();
  }

  public static BlockPos findGround(MinecraftClient client) {
    BlockPos pos = client.player.getBlockPos();
    while (pos.getY() >= 0) {
      pos = pos.down();
      if (isGround(pos, client)) {
        return pos;
      }
    }
    return null;
  }

  private static Integer computeGroundLevel(MinecraftClient client) {
    BlockPos ground = findGround(client);
    return ground == null ? null : ground.getY();
  }

  private static Double computeDistanceFromGround(MinecraftClient client, double altitude,
      Integer groundLevel) {
    if (groundLevel == null) {
      return null;
    }
    return Math.max(0d, altitude - groundLevel);
  }

  private static double computeAltitude(MinecraftClient client) {
    return client.player.getPos().y - 1;
  }

  private static double computeHeading(MinecraftClient client) {
    return toHeading(client.player.yaw);
  }

  private static double computeSpeed(MinecraftClient client) {
    return client.player.getVelocity().length() * TICKS_PER_SECOND;
  }

  private static double toHeading(double yawDegrees) {
    return (yawDegrees + 180) % 360;
  }
}
