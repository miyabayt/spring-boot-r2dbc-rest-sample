package com.bigtreetc.sample.r2dbc.base.web.controller.converter;

import com.bigtreetc.sample.r2dbc.base.domain.model.IntegerValue;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/** 数値の入力値をEnum型に変換する */
@SuppressWarnings({"rawtypes", "unchecked"})
public class IntegerValueEnumConverterFactory implements ConverterFactory<Integer, Enum> {

  @Override
  public <T extends Enum> Converter<Integer, T> getConverter(Class<T> targetType) {
    return source -> {
      if (IntegerValue.class.isAssignableFrom(targetType)) {
        return Stream.of(targetType.getEnumConstants())
            .filter(e -> ((IntegerValue) e).getValue().equals(source))
            .findFirst()
            .orElse(null);
      }
      return (T) Enum.valueOf(targetType, String.valueOf(source));
    };
  }
}
