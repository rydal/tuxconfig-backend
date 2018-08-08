

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
 * Servlet implementation class PasswordReset
 */
@WebServlet("/reset_password")
public class PasswordReset extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PasswordReset() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");
		if (request.getParameter("email")== null) { 
			// Get the printwriter object from response to write the required json object to the output stream      
			response.setContentType("application/json");
			JSONObject json = new JSONObject();
			 json.put("form", "Email address not sent");
			// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
			out.print(json);
			out.flush();
			return;}
		
		
		if (request.getParameter("code")== null) { // Get the printwriter object from response to write the required json object to the output stream      
			response.setContentType("application/json");
			JSONObject json = new JSONObject();
			 json.put("form", "Verification code not sent");
			// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
			out.print(json);
			out.flush();
			return;}
			
		try {
		
		    String email = request.getParameter("email").toLowerCase();
		    email = email.trim();
		    String code = request.getParameter("code");
		    String password_hash = Password.createHash(request.getParameter("password"));
		    
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost/linuxconf","arwen","imleaving");  
			//here sonoo is database name, root is username and password  
			PreparedStatement stmt=con.prepareStatement("select * from user where email = ?");  
			stmt.setObject(1,email);
			ResultSet rs=stmt.executeQuery();  
			if(!rs.next()) 
  			{
				session.setAttribute("flag", "couldn't find email address");
				response.setContentType("application/json");
				// Get the printwriter object from response to write the required json object to the output stream      
				 JSONObject json = new JSONObject();
				 json.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/Register.jsp");
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				out.print(json);
				out.flush();
				return;

  			} else {
  				if(rs.getInt("verified") == 1) {
  					PreparedStatement stmt2=con.prepareStatement("update user set password = ? where email = ? and verify_code = ?");
  					stmt2.setObject(1, password_hash);
  					stmt2.setObject(2, email);
  					stmt2.setObject(3,  code);
  					  					
  					int updated = stmt2.executeUpdate();
  					stmt2.close();
  					con.close();
  					if(updated == 1) {
  						response.setContentType("text/html");
  						session.setAttribute("flag", "Password updated successfully");
  						response.setContentType("application/json");
  						// Get the printwriter object from response to write the required json object to the output stream      
  						 JSONObject json = new JSONObject();
  						 json.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/login.jsp");
  						// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
  						out.print(json);
  						out.flush();
  						return;
  					} else { 
  						out.write("Error updating password");	
  						
  					} 
  				} else {
  					session.setAttribute("flag", "account not verified");
  					https://linuxconf.feedthepenguin.org/hehe/Register.jsp
  						response.setContentType("application/json");
  					// Get the printwriter object from response to write the required json object to the output stream      
  					 JSONObject json = new JSONObject();
  					 json.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/resend_email.jsp");
  					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
  					out.print(json);
  					out.flush();
  					return;
  					}
  				 
  			}	
  			
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			e.printStackTrace(out);
		}

		
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}
}
