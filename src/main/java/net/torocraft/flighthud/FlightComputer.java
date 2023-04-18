package net.torocraft.flighthud;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;

public class FlightComputer {
  private static final float TICKS_PER_SECOND = 20;

  private float previousRollAngle = 0.0f;

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

  public void update(MinecraftClient client, Matrix3f normalMatrix) {
    heading = computeHeading(client, normalMatrix);
    pitch = computePitch(client, normalMatrix);
    roll = computeRoll(client, normalMatrix);

    velocity = client.player.getVelocity();
    speed = computeSpeed(client);
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
      float remain = ((float) stack.getMaxDamage() - (float) stack.getDamage()) / (float) stack.getMaxDamage();
      return remain * 100f;
    }
    return null;
  }

  private float computeFlightPitch(Vec3d velocity, float pitch) {
    if (velocity.length() < 0.01) {
      return pitch;
    }
    Vec3d n = velocity.normalize();
    return (float) (90 - Math.toDegrees(Math.acos(n.y)));
  }

  private float computeFlightHeading(Vec3d velocity, float heading) {
    if (velocity.length() < 0.01) {
      return heading;
    }
    return toHeading((float) Math.toDegrees(-Math.atan2(velocity.x, velocity.z)));
  }

  //region Camera Angles

  private float computeRoll(MinecraftClient client, Matrix3f normalMatrix) {
    if (!FlightHud.CONFIG_SETTINGS.calculateRoll) {
      return 0.0f;
    }

    float y = normalMatrix.getRowColumn(0, 1);
    float x = normalMatrix.getRowColumn(1, 1);
    return (float) Math.toDegrees(Math.atan2(y, x));
  }

  private float computePitch(MinecraftClient client, Matrix3f normalMatrix) {
    if (client.player == null) {
      return 0.0f;
    }

    return -client.player.getPitch();
  }

  private float computeHeading(MinecraftClient client, Matrix3f normalMatrix) {
    if (client.player == null) {
      return 0.0f;
    }

    return toHeading(client.player.getYaw());
  }

  //endregion

  private boolean isGround(BlockPos pos, MinecraftClient client) {
    BlockState block = client.world.getBlockState(pos);
    return !block.isAir();
  }

  public BlockPos findGround(MinecraftClient client) {
    BlockPos pos = client.player.getBlockPos();
    while (pos.getY() >= 0) {
      pos = pos.down();
      if (isGround(pos, client)) {
        return pos;
      }
    }
    return null;
  }

  private Integer computeGroundLevel(MinecraftClient client) {
    BlockPos ground = findGround(client);
    return ground == null ? null : ground.getY();
  }

  private Float computeDistanceFromGround(MinecraftClient client, float altitude,
      Integer groundLevel) {
    if (groundLevel == null) {
      return null;
    }
    return Math.max(0f, altitude - groundLevel);
  }

  private float computeAltitude(MinecraftClient client) {
    return (float) client.player.getPos().y - 1;
  }

  private float computeSpeed(MinecraftClient client) {
    float speed = 0;
    var player = client.player;
    if (player.hasVehicle()) {
      Entity entity = player.getVehicle();
      speed = (float) entity.getVelocity().length() * TICKS_PER_SECOND;
    } else {
      speed = (float) client.player.getVelocity().length() * TICKS_PER_SECOND;
    }
    return speed;
  }

  private float toHeading(float yawDegrees) {
    return (yawDegrees + 180) % 360;
  }
}
