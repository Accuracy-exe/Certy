public interface DatabaseAdapter {
    boolean insertCertificate(String id, String certPem);
    String getCertificate(String id);
}
