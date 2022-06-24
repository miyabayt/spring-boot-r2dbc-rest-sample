package com.bigtreetc.sample.r2dbc.base.util;

import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class RequestUtils {

  public static final String X_REQUESTED_WITH = "X-Requested-With";

  public static final String XMLHTTP_REQUEST = "XMLHttpRequest";

  /**
   * User-Agentを取得します。
   *
   * @param request
   * @return
   */
  public static String getUserAgent(ServerHttpRequest request) {
    val ua = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
    if (ua == null) {
      return null;
    }
    return ua.trim();
  }

  /**
   * Ajax通信であるかを示す値を返します。
   *
   * @param request
   * @return
   */
  public static boolean isAjaxRequest(ServerHttpRequest request) {
    val header = request.getHeaders().getFirst(X_REQUESTED_WITH);
    return XMLHTTP_REQUEST.equalsIgnoreCase(header);
  }
}
