package net.torocraft.flighthud;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FlightComputer {
  private static final float TICKS_PER_SECOND = 20;

  public Vec3d velocity;
  public float speed;
  public float pitch;
  public float heading;
  public Vec3d flightPath;
  public float flightPitch;
  public float flightHeading;
  public float roll;
  public float altitude;
  public Integer groundLevel;
  public Float distanceFromGround;
  public Float elytraHealth;

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

  private Float computeElytraHealth(MinecraftClient client) {
    ItemStack stack = client.player.getEquippedStack(EquipmentSlot.CHEST);
    if (stack != null && stack.getItem() == Items.ELYTRA) {
      float remain = ((float)stack.getMaxDamage() - (float)stack.getDamage()) / (float)stack.getMaxDamage();
      return remain * 100f;
    }
    return null;
  }

  private static float computeFlightPitch(Vec3d velocity, float pitch) {
    if (velocity.length() < 0.01) {
      return pitch;
    }
    Vec3d n = velocity.normalize();
    return (float) (90 - Math.toDegrees(Math.acos(n.y)));
  }

  private static float computeFlightHeading(Vec3d velocity, float heading) {
    if (velocity.length() < 0.01) {
      return heading;
    }
    return toHeading((float)Math.toDegrees(-Math.atan2(velocity.x, velocity.z)));
  }

  private static float computeRoll(MinecraftClient client) {
    return (float) Math.toDegrees(client.player.elytraRoll);
  }

  private static float computePitch(MinecraftClient client, float parital) {
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

  private static Float computeDistanceFromGround(MinecraftClient client, float altitude,
      Integer groundLevel) {
    if (groundLevel == null) {
      return null;
    }
    return Math.max(0f, altitude - groundLevel);
  }

  private static float computeAltitude(MinecraftClient client) {
    return (float) client.player.getPos().y - 1;
  }

  private static float computeHeading(MinecraftClient client) {
    return toHeading(client.player.getYaw());
  }

  private static float computeSpeed(MinecraftClient client) {
    return (float) client.player.getVelocity().length() * TICKS_PER_SECOND;
  }

  private static float toHeading(float yawDegrees) {
    return (yawDegrees + 180) % 360;
  }
}
