import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class ChkPassword {

	public String correct_password(String email, String hash) {
		String output = "Error";
		try {
			Class.forName("com.mysql.jdbc.Driver");

			final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
			final String DB_URL = "jdbc:mysql://localhost/linuxconf";

			// Database credentials
			final String USER = "arwen";
			final String PASS = "imleaving";

			// Set response content type

			// Register JDBC driver
			// Open a connection

			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String sql;
			sql = "SELECT email,password, FROM user where email = ? ";
			// Execute SQL query
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setObject(1, email);

			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				output = "email not found";
			} else {
				
				if (hash.equals(rs.getString("password")) ) {
					output = "true";
				} else {
					output = "Invalid password";
				}
			}

			rs.close();
			stmt.close();

			conn.close();

		}

		catch (

		SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		
		return output;
	}
}
