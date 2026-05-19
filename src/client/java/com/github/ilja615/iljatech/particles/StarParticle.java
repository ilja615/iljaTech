package com.github.ilja615.iljatech.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class StarParticle extends TextureSheetParticle {

    protected StarParticle(ClientLevel clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z);
        this.lifetime = 25;
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
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
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            StarParticle starParticle = new StarParticle(clientWorld, x, y, z);
            starParticle.pickSprite(this.spriteProvider);
            //starParticle.scale(0.5F);
            return starParticle;
        }
    }
}
