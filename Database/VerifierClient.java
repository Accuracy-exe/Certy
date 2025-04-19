public class VerifierClient {
    private final DatabaseAdapter db;

    public VerifierClient(DatabaseAdapter db) {
        this.db = db;
    }

    public void verifyCertificate(String id) {
        String cert = db.getCertificate(id);
        if (cert != null) {
            System.out.println("Certificate found:\n" + cert);
        } else {
            System.out.println("Certificate not found.");
        }
    }
}
