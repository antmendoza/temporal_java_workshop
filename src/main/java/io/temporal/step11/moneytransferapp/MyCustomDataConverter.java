package io.temporal.step11.moneytransferapp;

import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.step11.moneytransferapp.httpserver.CryptCodec;

import java.util.Collections;

public class MyCustomDataConverter extends CodecDataConverter {


    public MyCustomDataConverter() {
        super(DefaultDataConverter.newDefaultInstance(),
                Collections.singletonList(new CryptCodec()));
    }
}
