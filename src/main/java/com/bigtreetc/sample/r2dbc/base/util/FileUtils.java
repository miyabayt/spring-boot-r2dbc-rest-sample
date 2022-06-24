package com.bigtreetc.sample.r2dbc.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import lombok.val;

public class FileUtils {

  /**
   * ディレクトリがない場合は作成します。
   *
   * @param location
   */
  public static void createDirectory(Path location) {
    try {
      Files.createDirectory(location);
    } catch (FileAlreadyExistsException ignore) {
      // ignore
    } catch (IOException e) {
      throw new IllegalArgumentException("could not create directory. " + location.toString(), e);
    }
  }

  /**
   * 親ディレクトリを含めてディレクトリがない場合は作成します。
   *
   * @param location
   */
  public static void createDirectories(Path location) {
    try {
      Files.createDirectories(location);
    } catch (FileAlreadyExistsException ignore) {
      // ignore
    } catch (IOException e) {
      throw new IllegalArgumentException("could not create directory. " + location.toString(), e);
    }
  }

  /**
   * ファイルを保存します。
   *
   * @param location
   * @param filename
   * @param is
   */
  public static void saveFile(Path location, String filename, InputStream is) {
    Objects.requireNonNull(filename, "filename can't be null");
    try (val inputStream = is) {
      // ディレクトリがない場合は作成する
      FileUtils.createDirectories(location);

      // インプットストリームをファイルに書き出す
      Files.copy(inputStream, location.resolve(filename));

    } catch (IOException e) {
      throw new IllegalStateException("failed to save file. " + filename, e);
    }
  }
}
