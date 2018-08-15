

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class AdminConsole
 */
@WebServlet("/vetconfigurations")
public class VetConfigurations extends HttpServlet  {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
     public VetConfigurations() {
		// TODO Auto-generated constructor stub
	    super();
        // TODO Auto-generated constructor stub
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
		Cookie cookie = null;
		Cookie[] cookies = null;

		String email = null; 
		String hash = null; 
		
		
		// Get an array of Cookies associated with this domain
		cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			cookie = cookies[i];
			if (cookie.getName().equals("email")) {
				email = cookie.getValue();
			}
			if (cookie.getName().equals("password")) {
				hash = cookie.getValue();
		} 
		}
		
		if (email == null ) {
			 JSONObject json2 = new JSONObject();
			 json2.put("form", "Email address not received from cookie");
			// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
			out.print(json2);
			return;
		}
		if (hash == null) {
			 JSONObject json2 = new JSONObject();
			 json2.put("form", "Password not received from cookie");
			// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
			out.print(json2);
			return;
		}
	
		try { 
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost/mycode","arwen","imleaving");  
			
		     
		    PreparedStatement stmt = con.prepareStatement("SELECT email, password FROM user where email = ? ");
			stmt.setObject(1, email);
		 	ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				rs.close();
				stmt.close();
				con.close();
				session.setAttribute("flag", "user not found");
				response.setStatus(HttpServletResponse.SC_FOUND); // SC_FOUND = 302
				 JSONObject json2 = new JSONObject();
				 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/Register.jsp");
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				out.print(json2);
				return;
			} else {
				 PreparedStatement stmt2 = con.prepareStatement("SELECT email, password, authorized  FROM user where email = ?");
					stmt.setObject(1, email);
				 	ResultSet rs2 = stmt.executeQuery();
				
				 	if (rs2.getInt("authorized") == 0) {
						session.setAttribute("flag", "User not authroized, re-request authorization");
				 		 JSONObject json2 = new JSONObject();
						 json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/Request_authorization.jsp");
						// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
						out.print(json2);
				 	}
			}
				if (! hash.equals(rs.getString("password"))) {
					session.setAttribute("flag", "invalid password");
					response.setStatus(HttpServletResponse.SC_FOUND); // SC_FOUND = 302
					out.write("https://linuxconf.feedthepenguin.org/hehe/login.jsp");
					return;
						
				} else if (hash.equals(rs.getString("password"))) {
					
				}
						
			
			
			} catch (Exception ex) { ex.printStackTrace(out); }
				
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}


}
