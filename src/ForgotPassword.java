
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class ForgotPassword
 */
@WebServlet("/forgotpassword")
public class ForgotPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ForgotPassword() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String email = request.getParameter("email").toLowerCase();
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		

		if (email == null) {
			response.setContentType("application/json");
			// Get the printwriter object from response to write the required json object to the output stream      
			 JSONObject json2 = new JSONObject();
			 json2.put("form", "email not entered");
			// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
			out.print(json2);
			return;
		}

		try {
			String code = randomString(32);

			java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");
			// here sonoo is database name, root is username and password
			PreparedStatement stmt = con.prepareStatement("select email,verified from user where email = ?");
			stmt.setObject(1, email);
			ResultSet rs = stmt.executeQuery();

			if (!rs.next()) {
				rs.close();
				stmt.close();
				con.close();
				
				session.setAttribute("flag", "email not found");
				response.setContentType("application/json");
				// Get the printwriter object from response to write the required json object to the output stream      
				 JSONObject json2 = new JSONObject();
				 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/Register.jsp");
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				out.print(json2);
				return;
			} else if (rs.getByte("verified") == 1) {
				
				new GenEmail(email,  out,"Reset Password", "<A HREF=\"https://linuxconf.feedthepenguin.org/hehe/PasswordReset.jsp?code="  + code +  "&email=" + email + "\"><BR><img src=\"https://linuxconf.feedthepenguin.org/hehe/img/reset.jpg\" ></A><BR><A HREF=\"https://linuxconf.feedthepenguin.org/hehe/PasswordReset.jsp?code=" + code + "&email=" + email +   "\">Reset Password</A>\""); 
				
			PreparedStatement insert_statement = con.prepareStatement("update user set  verify_code=? where email = ? ");
				insert_statement.setObject(1, code);
				insert_statement.setObject(2, rs.getInt("id"));
				insert_statement.executeUpdate();
				insert_statement.close();
				rs.close();
				stmt.close();
				con.close();
				if (session.getAttribute("email_fb") != null) {

					session.setAttribute("flag", "User password sent, check your email including spam");
				}else {

				session.setAttribute("flag", "Password Reset, check your email including spam");
					}
				response.setContentType("application/json");
				// Get the printwriter object from response to write the required json object to the output stream      
				 JSONObject json2 = new JSONObject();
				 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/index.jsp");
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				out.print(json2);
				return;
			} else {
				rs.close();
				stmt.close();
				con.close();
				session.setAttribute("flag", "Email not verified");
				response.setContentType("application/json");
				// Get the printwriter object from response to write the required json object to the output stream      
				 JSONObject json2 = new JSONObject();
				 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/resend_email.jsp");
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				out.print(json2);
				return;
			}

		}

		catch (Exception e) {
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

}
