
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.sun.mail.imap.protocol.ID;

import java.sql.*;
import java.util.*;
import java.math.BigInteger;
import java.util.Random;

import java.util.Date;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Servlet implementation class register
 */
@WebServlet("/doregister")
public class register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public register() {
		super();
		// TODO Auto-generated constructor stub

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		HttpSession session = request.getSession();

		String email = request.getParameter("email").toLowerCase();

		try {
			String password_hash = Password.createHash(request.getParameter("password"));
			String code = randomString(32);

			long timestamp = System.currentTimeMillis();

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf ? allowMultiQueries=true&rewriteBatchedStatements=true", "arwen", "imleaving");
			// here sonoo is database name, root is username and password
			PreparedStatement stmt = con.prepareStatement("select email,verified from user where email = ?");
			stmt.setObject(1, request.getParameter("email") );
			ResultSet rs = stmt.executeQuery();

			if (!rs.next()) {
				PreparedStatement add_to_database = con.prepareStatement("insert into user (timestamp, email, password, verified, verify_code) values (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
				add_to_database.setObject(1,timestamp);
				add_to_database.setObject(2,email);
				add_to_database.setObject(3,password_hash);
				add_to_database.setObject(4,0);
				add_to_database.setObject(5,code);
				add_to_database.executeUpdate();
				
				new GenEmail(email,  out,"Welcome to linuxconf", "<A HREF=\"https://linuxconf.feedthepenguin.org/hehe/verify?code\""  + code + "&email=" + email +  "> <img border=\"0\" alt=\"Verify\" src=\"https://linuxconf.feedthepenguin.org/hehe/img/verify_email.jpeg\" width=\"150\" height=\"150\"></A><A HREF=\"https://linuxconf.feedthepenguin.org/hehe/verify?code="  + code + "&email=" + email +  ">Register</A>"); 
						
					
				rs.close();
				stmt.close();
				con.close();
				session.setAttribute("flag", "Check your email for a verification code");
				response.setContentType("application/json");
				// Get the printwriter object from response to write the required json object to the output stream      
				 JSONObject json2 = new JSONObject();
				 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/login.jsp");
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				out.print(json2);
				return;
			} else {
				if (rs.getByte("verified") == 1) {
					session.setAttribute("flag", "already registered");
					response.setContentType("application/json");
					// Get the printwriter object from response to write the required json object to the output stream      
					 JSONObject json2 = new JSONObject();
					 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/login.jsp");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					return;
					
				}
				if (rs.getByte("verified") == 0) {
					new GenEmail(email,  out, "linuxconf Registration", "Welcome to linuxconf, please verify your account on here: <A HREF=\"https://linuxconf.feedthepenguin.org/hehe/verify?code=" + code + "&email="  + email + "> <img border=\"0\" alt=\"Verify account\" src=\"https://linuxconf.feedthepenguin.org/hehe/img/verify_email.jpeg\" width=\"150\" height=\"150\"><br>Register link</a>");

					PreparedStatement insert_statement = con.prepareStatement("update user set password = ?, verified= ?, verify_code = ?, timestamp = ? where email = ? ");
					insert_statement.setObject(1, password_hash);
					insert_statement.setObject(2, 0);
					insert_statement.setObject(3, code);
					insert_statement.setObject(4, timestamp);
					insert_statement.setObject(5, email);
					
					insert_statement.executeUpdate();
					insert_statement.close();
					con.close();
					session.setAttribute("flag", "Account created, check your inbox incl. spam");
					
					response.setContentType("application/json");
					// Get the printwriter object from response to write the required json object to the output stream      
					 JSONObject json2 = new JSONObject();
					 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/index.jsp");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					return;
					

				}
			}
			con.close();
		} catch (java.sql.SQLIntegrityConstraintViolationException e) { e.printStackTrace(out);
		} catch (Exception e) {
			e.printStackTrace(out);
		}
	}

	

	public static String randomString(int len) {
		String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random RANDOM = new Random();

		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
		}

		return sb.toString();
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);

}
}