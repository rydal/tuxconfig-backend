
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class DoLogin
 */
@WebServlet("/dologin")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	// TODO Auto-generHttpServletRequestated method stub
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		HttpSession session = request.getSession();

		String email = null;
		String password = null;
		String hash = "";

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

			if(request.getParameter("email") != null) {
				  email = request.getParameter("email");
			}

			if(request.getParameter("password") != null) {
				  password = request.getParameter("password");
			}			
			
			
			
				PreparedStatement stmt3 = conn
						.prepareStatement("SELECT email, verified, password FROM user where email = ?");
				stmt3.setString(1, email);
				ResultSet rs3 = stmt3.executeQuery();
				if (!rs3.next()) {
					rs3.close();
					stmt3.close();
					conn.close();
					session.setAttribute("flag", "invalid email: " + email);
					response.setContentType("application/json");
					// Get the printwriter object from response to write the required json object to the output stream      
					 JSONObject json2 = new JSONObject();
					 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/Register.jsp");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					return;
				}
				
		

				PreparedStatement stmt = conn.prepareStatement(
						"SELECT verified,email,password FROM user where email= ?  and verified = '0'");
				stmt.setString(1, email);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					rs3.close();
					stmt3.close();
					rs.close();
					stmt.close();
					conn.close();
					session.setAttribute("email_resend", email);
					session.setAttribute("flag", "not verified");
					response.setContentType("application/json");
					// Get the printwriter object from response to write the required json object to the output stream      
					 JSONObject json2 = new JSONObject();
					 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/resend_email.jsp?email=" + email);
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					return;

				} else {
			
				

				// Extract data from result set
				// hack if cookies exist

				// use normal login:
				ChkPassword chkpassword = new ChkPassword();
				if (hash.equals(rs3.getString("password")) || Password.verifyPassword(password, rs3.getString("password")) )	
						 {
					PreparedStatement stmt2 = conn.prepareStatement("SELECT email, password, verified FROM user where email = ? and verified = '1'");
					stmt2.setObject(1, email);
					ResultSet rs2 = stmt2.executeQuery();
					if (!rs2.next()) {
						rs2.close();
						stmt2.close();
						rs3.close();
						stmt3.close();
						rs.close();
						stmt.close();
						conn.close();
						session.setAttribute("flag", "invalid email");
						response.setContentType("application/json");
						// Get the printwriter object from response to write the required json object to the output stream      
						 JSONObject json2 = new JSONObject();
						 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/Register.jsp");
						// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
						out.print(json2);
						return;
					}
					
					Cookie cookie3 = Servlets.getCookie(request, "password");

					if (cookie3 != null) {
						cookie3.setValue(rs3.getString("password"));
						cookie3.setMaxAge(60 * 60 * 24 * 28);

						response.addCookie(cookie3);
					} else {
						Cookie password_cookie = new Cookie("password", rs3.getString("password"));
						password_cookie.setMaxAge(60 * 60 * 24 * 28);
						response.addCookie(password_cookie);

					}
					Cookie cookie2 = Servlets.getCookie(request, "email");

					if (cookie2 != null) {
						cookie2.setValue(email);
						cookie2.setMaxAge(60 * 60 * 24 * 28);

						response.addCookie(cookie2);
					} else {
						Cookie email_cookie = new Cookie("email", rs3.getString("email"));
						email_cookie.setMaxAge(60 * 60 * 24 * 28);
						response.addCookie(email_cookie);

					}
					
					rs2.close();
					stmt2.close();
					rs3.close();
					stmt3.close();
					rs.close();
					stmt.close();
					conn.close();
					response.setContentType("application/json");
					// Get the printwriter object from response to write the required json object to the output stream      
					 JSONObject json2 = new JSONObject();
					 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/AdminConsole.jsp");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					return;
						 

				} else {
				
					rs3.close();
					stmt3.close();
					rs.close();
					stmt.close();
					conn.close();
					response.setContentType("application/json");
					// Get the printwriter object from response to write the required json object to the output stream      
					 JSONObject json2 = new JSONObject();
					 json2.put("form", "invalid password");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					out.flush();
					return;
				}
				
				}
				
	
			// Clean-up environment

			} catch (

		SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace(out);
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace(out);
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
