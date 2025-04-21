import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.google.zxing.*;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.*;
import java.util.Base64;
import java.util.Date;

public class Certificate {
  private String issuer, achievement, inWhat, who, fromWhere, heldAt;
  private X509Certificate signedCert;
  private static final String KEY_DIR = "Keys/";

  public Certificate(String issuer, String achievement, String inWhat, String who, String fromWhere, String heldAt) {
    this.issuer = issuer;
    this.achievement = achievement;
    this.inWhat = inWhat;
    this.who = who;
    this.fromWhere = fromWhere;
    this.heldAt = heldAt;
  }

  public void generateAndSign() throws Exception {
    Security.addProvider(new BouncyCastleProvider());
    KeyPair keyPair = getOrCreateKeyPair();

    X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
    X509Principal subject = new X509Principal(
        "CN=" + who + ", OU=" + achievement + ", O=" + fromWhere + ", L=" + heldAt + ", C=IN");

    certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
    certGen.setSubjectDN(subject);
    certGen.setIssuerDN(new X509Principal("CN=" + issuer));
    certGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60));
    certGen.setNotAfter(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365));
    certGen.setPublicKey(keyPair.getPublic());
    certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

    signedCert = certGen.generate(keyPair.getPrivate(), "BC");

    // Files.createDirectories(Paths.get("certificates"));
  }

  private KeyPair getOrCreateKeyPair() throws Exception {
    File pubFile = new File(KEY_DIR + "public.key");
    File privFile = new File(KEY_DIR + "private.key");

    if (pubFile.exists() && privFile.exists()) {
      PublicKey pubKey = KeyFactory.getInstance("RSA")
          .generatePublic(new X509EncodedKeySpec(Files.readAllBytes(pubFile.toPath())));
      PrivateKey privKey = KeyFactory.getInstance("RSA")
          .generatePrivate(new PKCS8EncodedKeySpec(Files.readAllBytes(privFile.toPath())));
      return new KeyPair(pubKey, privKey);
    } else {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);
      KeyPair keyPair = keyGen.generateKeyPair();
      Files.createDirectories(Paths.get(KEY_DIR));
      Files.write(pubFile.toPath(), keyPair.getPublic().getEncoded());
      Files.write(privFile.toPath(), keyPair.getPrivate().getEncoded());
      return keyPair;
    }
  }

  private void saveCertificateAsDER(String path) throws Exception {
    try (FileOutputStream fos = new FileOutputStream(path)) {
      fos.write(signedCert.getEncoded());
      System.out.println("🔐 Certificate saved to DER: " + path);
    }
  }

  public String generateReadablePDF() throws Exception {
    PDDocument doc = new PDDocument();
    PDPage page = new PDPage();
    doc.addPage(page);

    PDPageContentStream content = new PDPageContentStream(doc, page);

    // Title
    content.beginText();
    PDType0Font robotoBoldFont = PDType0Font.load(doc, new File("../Fonts/Roboto-Bold.ttf"));
    content.setFont(robotoBoldFont, 24);
    content.newLineAtOffset(70, 700);
    content.showText("Certificate of Achievement");
    content.endText();

    // Body
    content.beginText();
    PDType0Font robotoRegularFont = PDType0Font.load(doc, new File("../Fonts/Roboto-Regular.ttf"));
    content.setFont(robotoRegularFont, 14);
    content.newLineAtOffset(70, 660);
    content.showText("This is to certify that " + who);
    content.newLineAtOffset(0, -20);
    content.showText("has achieved: " + achievement + " in " + inWhat);
    content.newLineAtOffset(0, -20);
    content.showText("Organized by: " + fromWhere);
    content.newLineAtOffset(0, -20);
    content.showText("Held at: " + heldAt);
    content.newLineAtOffset(0, -20);
    content.showText("Issued by: " + issuer);
    content.endText();

    // Embed cert + public key into QR
    String certB64 = Base64.getEncoder().encodeToString(signedCert.getEncoded());
    String pubKeyB64 = Base64.getEncoder().encodeToString(signedCert.getPublicKey().getEncoded());

    String qrJson = "{ \"certificate\": \"" + certB64 + "\", \"publicKey\": \"" + pubKeyB64 + "\" }";

    BufferedImage qrImage = generateQRCodeImage(qrJson, 500, 500);
    File qrFile = new File("certificates/temp_qr.png");
    ImageIO.write(qrImage, "png", qrFile);

    PDImageXObject qr = PDImageXObject.createFromFile("certificates/temp_qr.png", doc);
    content.drawImage(qr, 0, 0);

    // output path is certificates/currentEpoch.pdf
    String outputPath = "certificates/" + System.currentTimeMillis() + ".pdf";
    content.close();
    doc.save(outputPath);
    doc.close();

    qrFile.delete();
    System.out.println("📄 Human-readable certificate saved to: " + outputPath);
    return outputPath;
  }

  private BufferedImage generateQRCodeImage(String text, int width, int height) throws WriterException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
    return MatrixToImageWriter.toBufferedImage(bitMatrix);
  }

  public X509Certificate getSignedCertificate() {
    return signedCert;
  }
}
