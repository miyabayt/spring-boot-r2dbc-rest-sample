package com.bigtreetc.sample.r2dbc.base.util;

import static com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.buffer.*;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** TSVファイル出力ユーティリティ */
@Slf4j
public class CsvUtils {

  private static final CsvMapper csvMapper = createCsvMapper();

  /**
   * CSVマッパーを生成する。
   *
   * @return
   */
  private static CsvMapper createCsvMapper() {
    val mapper = new CsvMapper();
    mapper.configure(ALWAYS_QUOTE_STRINGS, true);
    mapper.findAndRegisterModules();
    return mapper;
  }

  /**
   * CSVファイルを出力します。
   *
   * @param dataBufferFactory
   * @param clazz
   * @param data
   * @param mapper
   * @return
   */
  public static Flux<DataBuffer> writeCsv(
      DataBufferFactory dataBufferFactory,
      Class<?> clazz,
      Flux<?> data,
      Function<Object, ?> mapper) {
    return write(dataBufferFactory, clazz, data, mapper, StandardCharsets.UTF_8.name(), ',');
  }

  /**
   * CSVファイルを出力します。
   *
   * @param dataBufferFactory
   * @param clazz
   * @param data
   * @param mapper
   * @param charsetName
   * @return
   */
  public static Flux<DataBuffer> writeCsv(
      DataBufferFactory dataBufferFactory,
      Class<?> clazz,
      Flux<?> data,
      Function<Object, ?> mapper,
      String charsetName) {
    return write(dataBufferFactory, clazz, data, mapper, charsetName, ',');
  }

  /**
   * TSVファイルを出力します。
   *
   * @param dataBufferFactory
   * @param clazz
   * @param data
   * @param mapper
   * @return
   */
  public static Flux<DataBuffer> writeTsv(
      DataBufferFactory dataBufferFactory,
      Class<?> clazz,
      Flux<?> data,
      Function<Object, ?> mapper) {
    return write(dataBufferFactory, clazz, data, mapper, StandardCharsets.UTF_8.name(), '\t');
  }

  /**
   * TSVファイルを出力します。
   *
   * @param dataBufferFactory
   * @param clazz
   * @param data
   * @param mapper
   * @param charsetName
   * @return
   */
  public static Flux<DataBuffer> writeTsv(
      DataBufferFactory dataBufferFactory,
      Class<?> clazz,
      Flux<?> data,
      Function<Object, ?> mapper,
      String charsetName) {
    return write(dataBufferFactory, clazz, data, mapper, charsetName, '\t');
  }

  /**
   * CSVファイルを出力します。
   *
   * @param dataBufferFactory
   * @param clazz
   * @param data
   * @param mapper
   * @param charsetName
   * @param delimiter
   * @return
   */
  @SneakyThrows
  private static Flux<DataBuffer> write(
      DataBufferFactory dataBufferFactory,
      Class<?> clazz,
      Flux<?> data,
      Function<Object, ?> mapper,
      String charsetName,
      char delimiter) {
    // CSVスキーマ、デリミタをセットする
    val schema = csvMapper.schemaFor(clazz).withColumnSeparator(delimiter);

    // ヘッダーを書き出す
    val headerDataBuffer =
        mapToCsvLine(dataBufferFactory, mapper, schema.withHeader(), null, charsetName);

    return Flux.concat(
        Mono.just(headerDataBuffer),
        data.concatMap(
            obj -> {
              val withoutHeaderSchema = schema.withoutHeader();
              val lineDataBuffer =
                  mapToCsvLine(dataBufferFactory, mapper, withoutHeaderSchema, obj, charsetName);
              return Mono.just(lineDataBuffer);
            }));
  }

  private static DataBuffer mapToCsvLine(
      DataBufferFactory dataBufferFactory,
      Function<Object, ?> mapper,
      CsvSchema schema,
      Object obj,
      String charsetName) {
    try {
      val mapped = (mapper != null && obj != null) ? mapper.apply(obj) : obj;
      val csvWriter = csvMapper.writer(schema);
      val csvLine = csvWriter.writeValueAsString(mapped);
      val dataBuffer = dataBufferFactory.wrap(csvLine.getBytes(charsetName));
      return DataBufferUtils.retain(dataBuffer);
    } catch (Exception e) {
      throw Exceptions.propagate(e);
    }
  }
}
