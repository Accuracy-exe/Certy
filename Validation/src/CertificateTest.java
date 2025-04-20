import org.bouncycastle.x509.X509V3CertificateGenerator;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.jce.X509Principal;
import java.security.Security;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Base64;

import java.io.FileInputStream;
import java.security.cert.CertificateFactory;


public class CertificateTest {
    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());          // Bouncy... Hehe
        
        // Need some of deez to test ma shit
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        
        // Time to make some Bouncy certificates >.<
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X509Principal dnName = new X509Principal("CN=Test Certificate");                         // Apparantly this is where u mention who/what the certificate's subject is

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));                 // Ah ofc... the usual primary key of things
        certGen.setSubjectDN(dnName);
        certGen.setIssuerDN(dnName);
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60));                 // Who counts in miliseconds man ._.
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365));
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");                                // DA LEGEND HAS SHOWN ITSELF

        X509Certificate cert = certGen.generate(pair.getPrivate(), "BC");
        
        // OK I am not gonna do this again, so off u go into ma SSD
        saveCertificateAsDER(cert, "cert/selfsigned.crt");
        saveCertificateAsPEM(cert, "cert/selfsigned.pem");


        // Following Singleton as you can see *Chef's Kiss*
        ValidationEngine validator = ValidationEngine.getInstance();
        validator.validateCertificate(cert, pair.getPublic());          // I had to see if the thing that I saved was right or not


        X509Certificate certFromFile = loadCertificateFromFile("cert/selfsigned.crt");
        validator.validateCertificate(certFromFile, pair.getPublic());  // And ofc... this
    }

    public static void saveCertificateAsDER(X509Certificate cert, String path) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(cert.getEncoded());
            System.out.println("🔐 Certificate saved to DER: " + path); // Hey I love emojis in my terminal, don't judge
        }
    }

    public static void saveCertificateAsPEM(X509Certificate cert, String path) throws Exception {
        Base64.Encoder encoder = Base64.getMimeEncoder(64, System.lineSeparator().getBytes());
        String encoded = encoder.encodeToString(cert.getEncoded());
    
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("-----BEGIN CERTIFICATE-----\n");
            writer.write(encoded);
            writer.write("\n-----END CERTIFICATE-----\n");
            System.out.println("🔐 Certificate saved to PEM: " + path);
        }
    }



    // Why are u reading this... just read funtion name no
    public static X509Certificate loadCertificateFromFile(String path) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(path)) {
            return (X509Certificate) cf.generateCertificate(fis);
        }
    }
}
