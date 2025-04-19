public class IssuerClient {
    private final DatabaseAdapter db;

    public IssuerClient(DatabaseAdapter db) {
        this.db = db;
    }

    public void issueCertificate(String id, String certPem) {
        boolean result = db.insertCertificate(id, certPem);
        System.out.println(result ? "Certificate issued successfully." : "Failed to issue certificate.");
    }
}
