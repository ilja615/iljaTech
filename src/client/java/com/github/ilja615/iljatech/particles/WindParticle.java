package com.github.ilja615.iljatech.particles;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.blocks.windmill.WindDirection;
import com.github.ilja615.iljatech.blocks.windmill.WindParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class WindParticle extends AnimatedParticle {

    private Vec2f vector;
    private int deltaY;
    private final static float INCREMENT = 0.25f;

    public WindParticle(WindDirection direction, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, (float) velocityY);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.maxAge = 120;
        this.scale = 0.5f;
        this.alpha = 0.0f;

        //this.vector = Wind.getWindDirectionUnitVectorAt(null,(int) x >> 4, (int) z >> 4);
        this.vector = direction.getUnitVector();

        int heightY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, (int) this.x, (int) this.z);
        this.deltaY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, (int) (this.x + this.vector.x * 4), (int) (this.z + this.vector.y * 4)) - heightY;
        if (deltaY > 1 && deltaY <= 4) {
            this.setSprite(spriteProvider.getSprite(3, 4));
        } else if (deltaY > 4) {
            this.setSprite(spriteProvider.getSprite(4, 4));
        } else if (deltaY < -1 && deltaY >= -4) {
            this.setSprite(spriteProvider.getSprite(1, 4));
        } else if (deltaY < -4) {
            this.setSprite(spriteProvider.getSprite(0, 4));
        } else {
            this.setSprite(spriteProvider.getSprite(2, 4));
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternionf quaternion = new Quaternionf();
        if (this.vector == null) {
            this.vector = Wind.getWindDirectionUnitVectorAt(null,(int) x >> 4, (int) z >> 4);
        }
        quaternion.rotationY( (float)MathHelper.atan2(-vector.y, vector.x));
        this.method_60373(vertexConsumer, camera, quaternion, tickDelta);
        this.method_60373b(vertexConsumer, camera, quaternion, tickDelta);
    }

    protected void method_60373b(VertexConsumer vertexConsumer, Camera camera, Quaternionf quaternionf, float f) {
        Vec3d vec3d = camera.getPos();
        float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3d.getX());
        float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3d.getY());
        float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3d.getZ());
        this.method_60374b(vertexConsumer, quaternionf, g, h, i, f);
    }

    protected void method_60374b(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i) {
        float j = this.getSize(i);
        float k = this.getMinU();
        float l = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(i);
        // Opposite winding order of vertices, and -j instead of j to flip upsidedown
        this.method_60375b(vertexConsumer, quaternionf, f, g, h, -1.0F, -1.0F, j, l, m, o);
        this.method_60375b(vertexConsumer, quaternionf, f, g, h, -1.0F, 1.0F, j, l, n, o);
        this.method_60375b(vertexConsumer, quaternionf, f, g, h, 1.0F, 1.0F, j, k, n, o);
        this.method_60375b(vertexConsumer, quaternionf, f, g, h, 1.0F, -1.0F, j, k, m, o);
    }

    private void method_60375b(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n) {
        Vector3f vector3f = (new Vector3f(i, j, 0.0F)).rotate(quaternionf).mul(k).add(f, g, h);
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(l, m).color(this.red, this.green, this.blue, this.alpha).light(n);
    }

    @Override
    public void tick()
    {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge || onGround) {
            this.markDead();
        } else {
            if (this.age >= this.maxAge - 4/INCREMENT && this.age % 4 == 0 && this.alpha > 0.0f) {
                this.alpha -= INCREMENT;
            } else if (this.age <= 8/INCREMENT && this.age % 8 == 0 && this.alpha < 1.0f) {
                this.alpha += INCREMENT;
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
}
