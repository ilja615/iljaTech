package com.github.ilja615.iljatech.particles;

import com.github.ilja615.iljatech.blocks.windmill.Wind;
import com.github.ilja615.iljatech.blocks.windmill.WindDirection;
import com.github.ilja615.iljatech.blocks.windmill.WindParticleEffect;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class WindParticle extends SimpleAnimatedParticle {

    private Vector2f vector;
    private int deltaY;
    private final static float INCREMENT = 0.25f;

    public WindParticle(WindParticleEffect parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, (float) velocityY);
        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;
        this.lifetime = 120;
        this.quadSize = 0.5f;
        this.alpha = 0.0f;

        //this.vector = Wind.getWindDirectionUnitVectorAt(null,(int) x >> 4, (int) z >> 4);
        this.vector = parameters.getDirection();

        int heightY = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int) this.x, (int) this.z);
        this.deltaY = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int) (this.x + this.vector.x * 4), (int) (this.z + this.vector.y * 4)) - heightY;
        if (deltaY > 1 && deltaY <= 4) {
            this.setSprite(spriteProvider.get(3, 4));
        } else if (deltaY > 4) {
            this.setSprite(spriteProvider.get(4, 4));
        } else if (deltaY < -1 && deltaY >= -4) {
            this.setSprite(spriteProvider.get(1, 4));
        } else if (deltaY < -4) {
            this.setSprite(spriteProvider.get(0, 4));
        } else {
            this.setSprite(spriteProvider.get(2, 4));
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternionf quaternion = new Quaternionf();
        if (this.vector == null) {
            this.vector = Wind.getWindDirectionUnitVectorAt(null,(int) x >> 4, (int) z >> 4);
        }
        quaternion.rotationY( (float)Mth.atan2(-vector.y, vector.x));
        this.renderRotatedQuad(vertexConsumer, camera, quaternion, tickDelta);
        this.method_60373b(vertexConsumer, camera, quaternion, tickDelta);
    }

    protected void method_60373b(VertexConsumer vertexConsumer, Camera camera, Quaternionf quaternionf, float f) {
        Vec3 vec3d = camera.getPosition();
        float g = (float)(Mth.lerp((double)f, this.xo, this.x) - vec3d.x());
        float h = (float)(Mth.lerp((double)f, this.yo, this.y) - vec3d.y());
        float i = (float)(Mth.lerp((double)f, this.zo, this.z) - vec3d.z());
        this.method_60374b(vertexConsumer, quaternionf, g, h, i, f);
    }

    protected void method_60374b(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i) {
        float j = this.getQuadSize(i);
        float k = this.getU0();
        float l = this.getU1();
        float m = this.getV0();
        float n = this.getV1();
        int o = this.getLightColor(i);
        // Opposite winding order of vertices, and -j instead of j to flip upsidedown
        this.method_60375b(vertexConsumer, quaternionf, f, g, h, -1.0F, -1.0F, j, l, m, o);
        this.method_60375b(vertexConsumer, quaternionf, f, g, h, -1.0F, 1.0F, j, l, n, o);
        this.method_60375b(vertexConsumer, quaternionf, f, g, h, 1.0F, 1.0F, j, k, n, o);
        this.method_60375b(vertexConsumer, quaternionf, f, g, h, 1.0F, -1.0F, j, k, m, o);
    }

    private void method_60375b(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n) {
        Vector3f vector3f = (new Vector3f(i, j, 0.0F)).rotate(quaternionf).mul(k).add(f, g, h);
        vertexConsumer.addVertex(vector3f.x(), vector3f.y(), vector3f.z()).setUv(l, m).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(n);
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || onGround) {
            this.remove();
        } else {
            if (this.age >= this.lifetime - 4/INCREMENT && this.age % 4 == 0 && this.alpha > 0.0f) {
                this.alpha -= INCREMENT;
            } else if (this.age <= 8/INCREMENT && this.age % 8 == 0 && this.alpha < 1.0f) {
                this.alpha += INCREMENT;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<WindParticleEffect> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(WindParticleEffect parameters, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            WindParticle windParticle = new WindParticle(parameters, clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
            return windParticle;
        }
    }
}
