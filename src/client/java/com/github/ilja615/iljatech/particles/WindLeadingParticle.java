package com.github.ilja615.iljatech.particles;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.blocks.windmill.WindParticleType;
import com.github.ilja615.iljatech.init.ModParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.Heightmap;
import org.joml.Quaternionf;

public class WindLeadingParticle extends WindParticle {
    private Vec2f vector;

    public WindLeadingParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(Wind.getWindDirectionAt(null,(int) x >> 4, (int) z >> 4), world, Math.floor(x), y, Math.floor(z), velocityX, velocityY, velocityZ, spriteProvider);
        this.maxAge = 80;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge || onGround) {
            this.markDead();
        } else {
            this.setSpriteForAge(this.spriteProvider);
            if (this.age % 4 == 0) {
                vector = Wind.getWindDirectionUnitVectorAt(null, (int) x >> 4, (int) z >> 4);
                int heightY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, (int) this.x, (int) this.z);
                int deltaY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, (int) (this.x + vector.x * 4), (int) (this.z + vector.y * 4)) - heightY;
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
                WindParticleType parameters = (WindParticleType) ModParticles.WIND;
                world.addParticle(parameters, x, y, z, 0, 0, 0);
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
    public static class Factory implements ParticleFactory<WindParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(WindParticleType windParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            WindParticle windParticle = new WindParticle(windParticleType.getDirection(), clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            return windParticle;
        }
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        return;
    }
}