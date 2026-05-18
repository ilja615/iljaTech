package com.github.ilja615.iljatech.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Util;
import org.joml.Vector2f;

import java.util.List;

public class ExtraCodecs {

    public static final Codec<Vector2f> VECTOR_2F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        return Util.decodeFixedLengthList(list, 2).map((listx) -> {
            return new Vector2f((Float)listx.get(0), (Float)listx.get(1));
        });
    }, (vec3f) -> {
        return List.of(vec3f.x(), vec3f.y());
    });

    public static final PacketCodec<ByteBuf, Vector2f> VECTOR_2F_PACKET = new PacketCodec<ByteBuf, Vector2f>() {
        public Vector2f decode(ByteBuf byteBuf) {
            return readVector2f(byteBuf);
        }

        public void encode(ByteBuf byteBuf, Vector2f vector2f) {
            writeVector2f(byteBuf, vector2f);
        }
    };

    public static void writeVector2f(ByteBuf buf, Vector2f vector) {
        buf.writeFloat(vector.x());
        buf.writeFloat(vector.y());
    }

    public static Vector2f readVector2f(ByteBuf buf) {
        return new Vector2f(buf.readFloat(), buf.readFloat());
    }
}
