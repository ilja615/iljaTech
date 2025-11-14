package com.github.ilja615.iljatech.particles;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.joml.Quaternionf;

public class WindParticle extends AnimatedParticle {

    public WindParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, (float) velocityY);
        this.velocityX = velocityX + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.velocityY = velocityY + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.velocityZ = velocityZ + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.maxAge = 32 + this.random.nextInt(16);
        this.scale += 0.2;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternionf quaternion = new Quaternionf();
        Vec2f vector = Wind.getWindVectorAt(world, (int) x >> 4, (int) z >> 4);
        quaternion.rotationY( (float)MathHelper.atan2(vector.y, vector.x));
        this.method_60373(vertexConsumer, camera, quaternion, tickDelta);
        quaternion.rotateY(-3.1415927F);
        this.method_60373(vertexConsumer, camera, quaternion, tickDelta);
    }

    @Override
    public void tick()
    {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            WindParticle windParticle = new WindParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            return windParticle;
        }
    }
}
