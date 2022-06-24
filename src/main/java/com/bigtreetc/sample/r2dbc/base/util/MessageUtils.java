package com.bigtreetc.sample.r2dbc.base.util;

import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

@Slf4j
public class MessageUtils {

  private static MessageSource messageSource;

  public static void init(MessageSource messageSource) {
    MessageUtils.messageSource = messageSource;
  }

  /**
   * メッセージを取得します。
   *
   * @param key
   * @param args
   * @return
   */
  public static String getMessage(String key, Object... args) {
    val locale = Locale.getDefault();
    return MessageUtils.messageSource.getMessage(key, args, locale);
  }

  /**
   * ロケールを指定してメッセージを取得します。
   *
   * @param key
   * @param locale
   * @param args
   * @return
   */
  public static String getMessage(String key, Locale locale, Object... args) {
    return MessageUtils.messageSource.getMessage(key, args, locale);
  }

  /**
   * メッセージを取得します。
   *
   * @param resolvable
   * @return
   */
  public static String getMessage(MessageSourceResolvable resolvable) {
    val locale = Locale.getDefault();
    return MessageUtils.messageSource.getMessage(resolvable, locale);
  }
}
