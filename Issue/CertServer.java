import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class CertServer {

  public static void main(String[] args) throws Exception {
    int port = 6969;
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/issue", new IssueHandler());
    server.setExecutor(null);
    System.out.println("🚀 Certificate Issuer running at http://localhost:" + port + "/issue");
    server.start();
  }

  static class IssueHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        return;
      }

      InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
      BufferedReader br = new BufferedReader(isr);
      StringBuilder requestBody = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        requestBody.append(line);
      }

      try {
        String json = requestBody.toString();
        System.out.println(json);

        String issuer = extractJsonField(json, "issuer");
        String achievement = extractJsonField(json, "achievement");
        String inWhat = extractJsonField(json, "inWhat");
        String who = extractJsonField(json, "who");
        String fromWhere = extractJsonField(json, "fromWhere");
        String heldAt = extractJsonField(json, "heldAt");

        System.out.println("Going to create certificate");
        Certificate cert = new Certificate(issuer, achievement, inWhat, who, fromWhere, heldAt);

        System.out.println("Going to sign pdf");
        cert.generateAndSign();

        // Generate PDF and get the path
        System.out.println("Going to create pdf");
        String outputPath = "certificates/" + who.replaceAll("\\s+", "_") + "_Certificate.pdf";
        cert.generateReadablePDF(outputPath);
        System.out.println("pdf created");

        // Read the PDF file to return as binary
        File pdfFile = new File(outputPath);
        byte[] pdfBytes = new byte[(int) pdfFile.length()];
        try (FileInputStream fis = new FileInputStream(pdfFile)) {
          fis.read(pdfBytes);
        }
        System.out.println("Read pdf, sending it back");

        exchange.getResponseHeaders().add("Content-Type", "application/pdf");
        exchange.sendResponseHeaders(200, pdfBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(pdfBytes);
        System.out.println("Wrote pdf to stream");
        os.close();
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
