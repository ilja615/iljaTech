package com.github.ilja615.iljatech.particles;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.init.ModParticles;
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

public class WindLeadingParticle extends WindParticle {

    public WindLeadingParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        this.maxAge = 80;
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
            Vec2f vector = Wind.getWindDirectionUnitVectorAt(null,(int) x >> 4, (int) z >> 4);
            if (this.age % 3 == 0) {
                world.addParticle(ModParticles.WIND, x, y, z, 0, 0, 0);
                this.move(vector.x, 0.0f, vector.y);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            WindLeadingParticle windParticle = new WindLeadingParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            return windParticle;
        }
    }
}
