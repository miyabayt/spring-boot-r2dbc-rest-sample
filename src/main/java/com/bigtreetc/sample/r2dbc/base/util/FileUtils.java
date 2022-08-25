package com.bigtreetc.sample.r2dbc.base.util;

import com.bigtreetc.sample.r2dbc.base.exception.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class FileUtils {

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
      throw new IllegalArgumentException("could not create directory. " + location, e);
    }
  }

  /**
   * ファイルの一覧を取得します。
   *
   * @param location
   * @return
   */
  public static List<Path> listAllFiles(Path location) {
    try (val walk = Files.walk(location, 1)) {
      return walk.filter(path -> !path.equals(location))
          .map(location::relativize)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalArgumentException("failed to list uploaded files. ", e);
    }
  }

  /**
   * ファイルを読み込みます。
   *
   * @param location
   * @param filename
   * @return
   */
  public static Resource loadFile(Path location, String filename) {
    try {
      val file = location.resolve(filename);
      val resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      }

      throw new FileNotFoundException("could not read file. " + filename);

    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(
          "malformed Url resource. [location="
              + location.toString()
              + ", filename="
              + filename
              + "]",
          e);
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
