package net.torocraft.flighthud;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class FlightComputer {
  private static final double TICKS_PER_SECOND = 20;

  public Vector3d velocity;
  public double speed;
  public double pitch;
  public double heading;
  public Vector3d flightPath;
  public double flightPitch;
  public double flightHeading;
  public double roll;
  public double altitude;
  public Integer groundLevel;
  public Double distanceFromGround;
  public Double elytraHealth;

  public void update(Minecraft client, float partial) {
    velocity = client.player.getMotion();//  getVelocity();
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

  private Double computeElytraHealth(Minecraft client) {
    ItemStack stack = client.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
    if (stack != null && stack.getItem() == Items.ELYTRA) {
      double remain = ((double)stack.getMaxDamage() - (double)stack.getDamage()) / (double)stack.getMaxDamage();
      return remain * 100d;
    }
    return null;
  }

  private static double computeFlightPitch(Vector3d velocity, double pitch) {
    if (velocity.length() < 0.01) {
      return pitch;
    }
    Vector3d n = velocity.normalize();
    return 90 - Math.toDegrees(Math.acos(n.y));
  }

  private static double computeFlightHeading(Vector3d velocity, double heading) {
    if (velocity.length() < 0.01) {
      return heading;
    }
    return toHeading(Math.toDegrees(-Math.atan2(velocity.x, velocity.z)));
  }

  private static double computeRoll(Minecraft client) {
    return 0;
  }

  private static double computePitch(Minecraft client, float parital) {
    return client.player.getPitch(parital) * -1;
  }

  private static boolean isGround(BlockPos pos, Minecraft client) {
    BlockState block = client.world.getBlockState(pos);
    return !block.isAir(client.world, pos);
  }

  public static BlockPos getPlayerPos(Minecraft client) {
    return client.player.func_233580_cy_();
  }

  public static BlockPos findGround(Minecraft client) {
    BlockPos pos = getPlayerPos(client);
    while (pos.getY() >= 0) {
      pos = pos.down();
      if (isGround(pos, client)) {
        return pos;
      }
    }
    return null;
  }

  private static Integer computeGroundLevel(Minecraft client) {
    BlockPos ground = findGround(client);
    return ground == null ? null : ground.getY();
  }

  private static Double computeDistanceFromGround(Minecraft client, double altitude,
      Integer groundLevel) {
    if (groundLevel == null) {
      return null;
    }
    return Math.max(0d, altitude - groundLevel);
  }

  private static double computeAltitude(Minecraft client) {
    return getPlayerPos(client).getY() - 1;
  }

  private static double computeHeading(Minecraft client) {
    return toHeading(client.player.rotationYaw);
  }

  private static double computeSpeed(Minecraft client) {
    return client.player.getMotion().length() * TICKS_PER_SECOND;
  }

  private static double toHeading(double yawDegrees) {
    return (yawDegrees + 180) % 360;
  }
}
