package com.github.ilja615.iljatech.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class SteamParticle extends AnimatedParticle {
    public SteamParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
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
    public void tick()
    {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
            if (this.age % (this.maxAge / 8) < 1)
                this.scale += 0.05f;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            SteamParticle steamParticle = new SteamParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            return steamParticle;
        }
    }
}
