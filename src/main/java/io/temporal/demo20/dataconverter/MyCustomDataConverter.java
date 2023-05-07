package io.temporal.demo20.dataconverter;

import io.temporal.common.converter.CodecDataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.demo20.dataconverter.httpserver.CryptCodec;
import java.util.Collections;

public class MyCustomDataConverter extends CodecDataConverter {

  public MyCustomDataConverter() {
    super(DefaultDataConverter.newDefaultInstance(), Collections.singletonList(new CryptCodec()));
  }
}
