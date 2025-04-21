import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class StorageServer {
  public static void main(String[] args) throws Exception {
    int port = 8082;
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/store", new StoreHandler(new SQLiteCertificateAdapter()));
    server.setExecutor(null);
    System.out.println("💾 StorageServer running at http://localhost:" + port + "/store");
    server.start();
  }

  static class StoreHandler implements HttpHandler {
    private final DatabaseAdapter db;

    public StoreHandler(DatabaseAdapter db) {
      this.db = db;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        return;
      }

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
      StringBuilder body = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        body.append(line);
      }

      try {
        String json = body.toString();
        String id = extractJsonField(json, "id");
        String certPem = extractJsonField(json, "certPem");

        boolean result = db.insertCertificate(id, certPem);

        String response = result ? "Certificate stored successfully." : "Failed to store certificate.";
        int code = result ? 200 : 400;

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();

      } catch (Exception e) {
        e.printStackTrace();
        exchange.sendResponseHeaders(400, -1); // Bad Request
      }
    }

    private String extractJsonField(String json, String field) {
      String pattern = "\"" + field + "\":\\s*\"([^\"]+)\"";
      return json.replaceAll(".*" + pattern + ".*", "$1");
    }
  }
}
