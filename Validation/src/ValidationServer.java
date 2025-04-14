// CertServer.java
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;





// I definitely don't have to explain this... do I?
// Even so here are the important take-aways...
//
//
// Expected data: 
// {
//     "certificate": "<b64>",
//     "publicKey"  : "<b64>"
// }
//
//
// Response:
// {
//     "status":"valid"
// }
//
// ^ If it is valid ofc, else "invalid"






public class ValidationServer {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/validate", new ValidateHandler());
        server.setExecutor(null);
        System.out.println("🚀 Server running at http://localhost:" + port + "/validate");
        server.start();
    }

    static class ValidateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            // Read JSON input
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                body.append(line);
            }

            try {
                String json = body.toString();
                String certBase64 = extractJsonField(json, "certificate");
                String pubKeyBase64 = extractJsonField(json, "publicKey");

                X509Certificate cert = loadCertificateFromBase64(certBase64);
                PublicKey pubKey = loadPublicKeyFromBase64(pubKeyBase64);

                ValidationEngine validator = ValidationEngine.getInstance();
                boolean valid = validator.validateCertificate(cert, pubKey);

                String response = "{\"status\": \"" + (valid ? "valid" : "invalid") + "\"}";
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(400, -1); // Bad Request
            }
        }

        private String extractJsonField(String json, String field) {
            // Simple JSON field extractor (assumes no escaping etc)
            String pattern = "\"" + field + "\":\\s*\"([^\"]+)\"";
            return json.replaceAll(".*" + pattern + ".*", "$1");
        }

        private X509Certificate loadCertificateFromBase64(String base64Cert) throws Exception {
            byte[] certBytes = Base64.getDecoder().decode(base64Cert);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certBytes));
        }

        private PublicKey loadPublicKeyFromBase64(String base64Key) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }
    }
}
