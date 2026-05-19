package com.github.ilja615.iljatech.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class SteamParticle extends SimpleAnimatedParticle {
    public SteamParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, (float) velocityY);
        this.xd = velocityX + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.yd = velocityY + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.zd = velocityZ + (Math.random() * 2.0d - 1.0d) * 0.03d;
        this.lifetime = 32 + this.random.nextInt(16);
        this.quadSize += 0.2;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            this.setSpriteFromAge(this.sprites);
            if (this.age % (this.lifetime / 8) < 1)
                this.quadSize += 0.05f;
            this.move(this.xd, this.yd, this.zd);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            SteamParticle steamParticle = new SteamParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            return steamParticle;
        }
    }
}
