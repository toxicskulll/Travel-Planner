import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:users.db";
    
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            createTable();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private static void createTable() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY," +
                        "password TEXT NOT NULL," +
                        "name TEXT," +
                        "gender TEXT," +
                        "age INTEGER," +
                        "email TEXT," +
                        "instagram TEXT," +
                        "linkedin TEXT)";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean addUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Username already exists
            return false;
        }
    }

    public static boolean validateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password").equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void updateProfile(String username, String name, String gender, 
            int age, String email, String instagram, String linkedin) {
        String sql = "UPDATE users SET name = ?, gender = ?, age = ?, " +
                    "email = ?, instagram = ?, linkedin = ? WHERE username = ?";
                    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setInt(3, age);
            pstmt.setString(4, email);
            pstmt.setString(5, instagram);
            pstmt.setString(6, linkedin);
            pstmt.setString(7, username);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Profile update affected " + rowsAffected + " rows");
            
            // Verify the update
            printUserData(username);
            
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Helper method to print user data
    private static void printUserData(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("User data for: " + username);
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Gender: " + rs.getString("gender"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Instagram: " + rs.getString("instagram"));
                System.out.println("LinkedIn: " + rs.getString("linkedin"));
            }
        } catch (SQLException e) {
            System.err.println("Error printing user data: " + e.getMessage());
        }
    }

    public static ProfileData getProfileData(String username) {
        String sql = "SELECT name, gender, age, email, instagram, linkedin " +
                    "FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ProfileData(
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getInt("age"),
                    rs.getString("email"),
                    rs.getString("instagram"),
                    rs.getString("linkedin")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ProfileData();
    }

    // Helper method to print database contents (for debugging)
    public static void printDatabaseContents() {
        String sql = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Gender: " + rs.getString("gender"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Instagram: " + rs.getString("instagram"));
                System.out.println("LinkedIn: " + rs.getString("linkedin"));
                System.out.println("--------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}