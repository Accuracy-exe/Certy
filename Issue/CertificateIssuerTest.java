public class CertificateIssuerTest {
  public static void main(String[] args) {
    try {
      // Certificate details
      String issuer = "Certy";
      String achievement = "2nd Place";
      String inWhat = "Drone Design Challenge 2025";
      String who = "Team Avions";
      String fromWhere = "SAE ISS";
      String heldAt = "Bangalore";

      // Create the certificate
      Certificate cert = new Certificate(issuer, achievement, inWhat, who, fromWhere, heldAt);

      // Generate cryptographic X.509 + store it
      cert.generateAndSign();

      // Generate a human-readable PDF with embedded QR
      String pathStored = cert.generateReadablePDF("certificates/" + who.replaceAll("\\s+", "_") + "_Certificate.pdf");
      System.out.println("Certificate stored at: " + pathStored);
      System.out.println("✅ Certificate generated successfully.");

    } catch (Exception e) {
      System.err.println("❌ Error during certificate generation:");
      e.printStackTrace();
    }
  }
}
