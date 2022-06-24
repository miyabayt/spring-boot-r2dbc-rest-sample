package com.bigtreetc.sample.r2dbc.base.util;

import static com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import java.io.OutputStreamWriter;
import java.io.Writer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.buffer.DataBuffer;

/** TSVファイル出力ユーティリティ */
@Slf4j
public class CsvUtils {

  public static final String CHARSET__WINDOWS_31J = "Windows-31J";

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
   * @param clazz
   * @param data
   * @param dataBuffer
   */
  public static void writeCsv(Class<?> clazz, Object data, DataBuffer dataBuffer) {
    write(dataBuffer, clazz, data, CHARSET__WINDOWS_31J, ',');
  }

  /**
   * CSVファイルを出力します。
   *
   * @param clazz
   * @param data
   * @param dataBuffer
   * @param charsetName
   */
  public static void writeCsv(
      Class<?> clazz, Object data, DataBuffer dataBuffer, String charsetName) {
    write(dataBuffer, clazz, data, charsetName, ',');
  }

  /**
   * TSVファイルを出力します。
   *
   * @param clazz
   * @param data
   * @throws Exception
   */
  public static void writeTsv(DataBuffer dataBuffer, Class<?> clazz, Object data) {
    write(dataBuffer, clazz, data, CHARSET__WINDOWS_31J, '\t');
  }

  /**
   * TSVファイルを出力します。
   *
   * @param clazz
   * @param data
   * @param charsetName
   * @throws Exception
   */
  public static void writeTsv(
      DataBuffer dataBuffer, Class<?> clazz, Object data, String charsetName) {
    write(dataBuffer, clazz, data, charsetName, '\t');
  }

  /**
   * CSVファイルを出力します。
   *
   * @param dataBuffer
   * @param clazz
   * @param data
   * @param charsetName
   * @param delimiter
   * @return
   * @throws Exception
   */
  @SneakyThrows
  private static void write(
      DataBuffer dataBuffer, Class<?> clazz, Object data, String charsetName, char delimiter) {
    // CSVヘッダをオブジェクトから作成する
    val schema = csvMapper.schemaFor(clazz).withHeader().withColumnSeparator(delimiter);

    // 書き出し
    try (Writer writer = new OutputStreamWriter(dataBuffer.asOutputStream(), charsetName)) {
      csvMapper.writer(schema).writeValue(writer, data);
    }
  }
}
