import java.security.PublicKey;
import java.security.cert.X509Certificate;


// Clean af... ngl
public class ValidationEngine {
    private static ValidationEngine instance;
    private ValidationEngine() {}
    public static ValidationEngine getInstance() {    // Just to reinforce the Singleness of my life
        if (instance == null) {
            instance = new ValidationEngine();
        }
        return instance;
    }
    public boolean validateCertificate(X509Certificate cert, PublicKey caPublicKey) {      // The golden function
        try {
            cert.checkValidity();                     // It's literally just that, java had built-in shit for this
            cert.verify(caPublicKey);                 // And that...
            System.out.println("✅ Certy is valid.");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Validation failed: " + e.getMessage());
            return false;
        }
    }
}
