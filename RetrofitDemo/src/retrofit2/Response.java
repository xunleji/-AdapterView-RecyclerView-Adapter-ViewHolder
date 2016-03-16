/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.ResponseBody;

/** An HTTP response. */
public final class Response<T> {
  /** Create a synthetic successful response with {@code body} as the deserialized body. */
  public static <T> Response<T> success(T body) {
    return success(body, new com.squareup.okhttp.Response.Builder() //
        .code(200)
        .message("OK")
        .protocol(Protocol.HTTP_1_1)
        .request(new com.squareup.okhttp.Request.Builder().url("http://localhost").build())
        .build());
  }

  /**
   * Create a successful response from {@code rawResponse} with {@code body} as the deserialized
   * body.
   */
  public static <T> Response<T> success(T body, com.squareup.okhttp.Response rawResponse) {
    if (rawResponse == null) throw new NullPointerException("rawResponse == null");
    if (!rawResponse.isSuccessful()) {
      throw new IllegalArgumentException("rawResponse must be successful response");
    }
    return new Response<>(rawResponse, body, null);
  }

  /**
   * Create a synthetic error response with an HTTP status code of {@code code} and {@code body}
   * as the error body.
   */
  public static <T> Response<T> error(int code, ResponseBody body) {
    if (code < 400) throw new IllegalArgumentException("code < 400: " + code);
    return error(body, new com.squareup.okhttp.Response.Builder() //
        .code(code)
        .protocol(Protocol.HTTP_1_1)
        .request(new com.squareup.okhttp.Request.Builder().url("http://localhost").build())
        .build());
  }

  /** Create an error response from {@code rawResponse} with {@code body} as the error body. */
  public static <T> Response<T> error(ResponseBody body, com.squareup.okhttp.Response rawResponse) {
    if (body == null) throw new NullPointerException("body == null");
    if (rawResponse == null) throw new NullPointerException("rawResponse == null");
    if (rawResponse.isSuccessful()) {
      throw new IllegalArgumentException("rawResponse should not be successful response");
    }
    return new Response<>(rawResponse, null, body);
  }

  private final com.squareup.okhttp.Response rawResponse;
  private final T body;
  private final ResponseBody errorBody;

  private Response(com.squareup.okhttp.Response rawResponse, T body, ResponseBody errorBody) {
    this.rawResponse = rawResponse;
    this.body = body;
    this.errorBody = errorBody;
  }

  /** The raw response from the HTTP client. */
  public com.squareup.okhttp.Response raw() {
    return rawResponse;
  }

  /** HTTP status code. */
  public int code() {
    return rawResponse.code();
  }

  /** HTTP status message or null if unknown. */
  public String message() {
    return rawResponse.message();
  }

  /** HTTP headers. */
  public Headers headers() {
    return rawResponse.headers();
  }

  /** {@code true} if {@link #code()} is in the range [200..300). */
  public boolean isSuccess() {
    return rawResponse.isSuccessful();
  }

  /** The deserialized response body of a {@linkplain #isSuccess() successful} response. */
  public T body() {
    return body;
  }

  /** The raw response body of an {@linkplain #isSuccess() unsuccessful} response. */
  public ResponseBody errorBody() {
    return errorBody;
  }
}
