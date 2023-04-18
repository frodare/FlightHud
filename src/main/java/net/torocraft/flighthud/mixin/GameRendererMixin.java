package net.torocraft.flighthud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.torocraft.flighthud.FlightHud.computer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    MinecraftClient client;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setInverseViewRotationMatrix(Lorg/joml/Matrix3f;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderWorld(
            float tickDelta,
            long limitTime,
            MatrixStack matrices,
            CallbackInfo ci
    ) {
        Matrix3f inverseViewRotationMatrix = RenderSystem.getInverseViewRotationMatrix();
        computer.update(client, inverseViewRotationMatrix.invert());
    }
}
