package com.bigtreetc.sample.r2dbc.base.domain.helper;

import com.bigtreetc.sample.r2dbc.base.exception.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

/** ファイルアップロード */
@Component
public class FileHelper {

  /**
   * ファイルの一覧を取得します。
   *
   * @param location
   * @return
   */
  public List<Path> listAllFiles(Path location) {
    try (val walk = Files.walk(location, 1)) {
      return walk.filter(path -> !path.equals(location)).map(location::relativize).toList();
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
  public Resource loadFile(Path location, String filename) {
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
}
