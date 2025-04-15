import java.sql.*;

public class SQLiteCertificateAdapter implements DatabaseAdapter {
    private static final String DB_URL = "jdbc:sqlite:certificates.db";
    private Connection connection;

    public SQLiteCertificateAdapter() {
        try {
            Class.forName("org.sqlite.JDBC"); // Important: Load the SQLite driver
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS certificates (" +
                     "id TEXT PRIMARY KEY," +
                     "pem TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Failed to initialize DB: " + e.getMessage());
        }
    }

    @Override
    public boolean insertCertificate(String id, String certPem) {
        String sql = "INSERT INTO certificates(id, pem) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, certPem);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getCertificate(String id) {
        String sql = "SELECT pem FROM certificates WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("pem");
            }
        } catch (SQLException e) {
            System.err.println("Fetch failed: " + e.getMessage());
        }
        return null;
    }
}
