// java
package com.pablorotten;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestClient {
  private static final HttpClient CLIENT = HttpClient.newHttpClient();

  public static String getBooking(String id) throws IOException, InterruptedException {
    HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create("https://restful-booker.herokuapp.com/booking/" + id))
        .header("Accept", "application/json")
        // Set a User-Agent similar to curl/browser to avoid server blocking
        .header("User-Agent", "curl/7.86.0")
        .GET()
        .build();

    HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());

    if (resp.statusCode() == 200) {
      return resp.body();
    } else {
      // return status + body for debugging (or throw an exception in real code)
      return resp.statusCode() + " " + resp.body();
    }
  }
}
