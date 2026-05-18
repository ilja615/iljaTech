package com.github.ilja615.iljatech.particles;

import SpriteProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class StarParticle extends TextureSheetParticle {

    protected StarParticle(ClientLevel clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z);
        this.lifetime = 25;
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
            double dx = 0.125d * Math.cos(this.age / 4.0d);
            double dy = 0.04d * Math.cos(this.age / 2.0d);
            double dz = 0.125d * Math.sin(this.age / 4.0d);
            this.move(dx, dy, dz);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            StarParticle starParticle = new StarParticle(clientWorld, x, y, z);
            starParticle.setSprite(this.spriteProvider);
            //starParticle.scale(0.5F);
            return starParticle;
        }
    }
}
