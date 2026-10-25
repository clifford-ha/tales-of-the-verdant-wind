package cliffordha.totvw.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import oshi.util.tuples.Triplet;

public class VWCodecs {
    private static <A, B, C> Codec<Triplet<A, B, C>> tripletCodec(Codec<A> firstCodec, Codec<B> secondCodec, Codec<C> thirdCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                firstCodec.fieldOf("first").forGetter(Triplet::getA),
                secondCodec.fieldOf("second").forGetter(Triplet::getB),
                thirdCodec.fieldOf("third").forGetter(Triplet::getC)
        ).apply(instance, Triplet::new));
    }
    public static <B extends ByteBuf, A, X, C> StreamCodec<B, Triplet<A, X, C>> tripletStreamCodec(
            StreamCodec<? super B, A> first,
            StreamCodec<? super B, X> second,
            StreamCodec<? super B, C> third) {
        return StreamCodec.composite(
                first, Triplet::getA,
                second, Triplet::getB,
                third, Triplet::getC,
                Triplet::new
        );
    }
    public static final Codec<Triplet<String, Integer, Integer>> STRING_INT_INT_CODEC = tripletCodec(Codec.STRING, Codec.INT, Codec.INT);


    private static <A, B, C> Codec<Triplet<A, B, C>> wolfArmorCodec(Codec<A> firstCodec, Codec<B> secondCodec, Codec<C> thirdCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                firstCodec.fieldOf("first").forGetter(Triplet::getA),
                secondCodec.fieldOf("second").forGetter(Triplet::getB),
                thirdCodec.fieldOf("third").forGetter(Triplet::getC)
        ).apply(instance, Triplet::new));
    }
    public static final Codec<Triplet<Boolean, Integer, Integer>> WOLF_ARMOR_CODEC = wolfArmorCodec(Codec.BOOL, Codec.INT, Codec.INT);
}
