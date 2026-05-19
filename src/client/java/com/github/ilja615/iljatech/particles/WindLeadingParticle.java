package com.github.ilja615.iljatech.particles;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.blocks.windmill.WindParticleEffect;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector2f;

public class WindLeadingParticle extends WindParticle {
    private Vector2f vector;

    public WindLeadingParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(new WindParticleEffect(Wind.getWindDirectionUnitVectorAt(null, (int) x >> 4, (int) z >> 4)),
                world, Math.floor(x), y, Math.floor(z), velocityX, velocityY, velocityZ, spriteProvider);
        this.lifetime = 80;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || onGround) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
            if (this.age % 4 == 0) {
                vector = Wind.getWindDirectionUnitVectorAt(null, (int) x >> 4, (int) z >> 4);
                int heightY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int) this.x, (int) this.z);
                int deltaY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int) (this.x + vector.x * 4), (int) (this.z + vector.y * 4)) - heightY;
                if (deltaY > 4) {
                    this.move(0, 0.5f, 0);
                } else if (deltaY > 1) {
                    this.move(0, 0.25f, 0);
                }
                if (deltaY < -4) {
                    this.move(0, -0.5f, 0);
                } else if (deltaY < -1) {
                    this.move(0, -0.25f, 0);
                }
                WindParticleEffect parameters = new WindParticleEffect(vector);
                level.addParticle(parameters, x, y, z, 0, 0, 0);
                if (deltaY > 4) {
                    this.move(vector.x, 0.5f, vector.y);
                } else if (deltaY > 1) {
                    this.move(vector.x, 0.25f, vector.y);
                } else if (deltaY < -4) {
                    this.move(vector.x, -0.5f, vector.y);
                } else if (deltaY < -1) {
                    this.move(vector.x, -0.25f, vector.y);
                } else {
                    this.move(vector.x, 0.0f, vector.y);
                }
                this.vector = this.vector;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            WindLeadingParticle windParticle = new WindLeadingParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            return windParticle;
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        return;
    }
}