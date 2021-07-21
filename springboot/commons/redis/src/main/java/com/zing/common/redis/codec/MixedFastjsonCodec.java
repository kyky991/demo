package com.zing.common.redis.codec;

import org.redisson.client.codec.BaseCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

public class MixedFastjsonCodec extends BaseCodec {

    public static final MixedFastjsonCodec INSTANCE = new MixedFastjsonCodec();

    @Override
    public Decoder<Object> getValueDecoder() {
        return FastjsonCodec.INSTANCE.getValueDecoder();
    }

    @Override
    public Encoder getValueEncoder() {
        return StringCodec.INSTANCE.getValueEncoder();
    }

}