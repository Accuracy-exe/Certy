public class Main {
    public static void main(String[] args) {
        DatabaseAdapter db = new SQLiteCertificateAdapter();

        IssuerClient issuer = new IssuerClient(db);
        VerifierClient verifier = new VerifierClient(db);

        // Simulate issuing a cert
        issuer.issueCertificate("cert534", "-----BEGIN CERTIFICATE-----\n...");

        // Simulate verifying the cert
        verifier.verifyCertificate("cert789");
    }
}
