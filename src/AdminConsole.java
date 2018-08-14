

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
@WebServlet("/adminconsole")
public class AdminConsole extends HttpServlet  {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminConsole() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
	

		String email = null; 
		String action = null; 
	
		email = request.getParameter("email");
		action = request.getParameter("action");
		
		if (email == null ) {
			 JSONObject json2 = new JSONObject();
			 json2.put("form", "Email address not received from page");
			// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
			out.print(json2);
			return;
		}
		if (action == null) {
			 JSONObject json2 = new JSONObject();
			 json2.put("form", "Action not recieved from page");
			// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
			out.print(json2);
			return;
		}
	
		try { 
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost/linuxconf","arwen","imleaving");  
			
			if (action.equals("delete")) {
				PreparedStatement delete_user = con.prepareStatement("delete from user where email = ?");
				delete_user.setObject(1, email);
				int rows_updated =  delete_user.executeUpdate();
				if(rows_updated != 1 ) {
					JSONObject json2 = new JSONObject();
					 json2.put("form", "Email address could not be found to be deleted");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					return;
				}	else {
					JSONObject json2 = new JSONObject();
					 json2.put("form", "Email address deleted");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					
				}
			}

			if (action.equals("authrorize")) {
				PreparedStatement authroize_user = con.prepareStatement("update user set authroize = 1 where email = ?");
				authroize_user.setObject(1, email);
				int rows_updated =  authroize_user.executeUpdate();
				if(rows_updated != 1) {
					JSONObject json2 = new JSONObject();
					 json2.put("form", "Email address could not be found to be authorized");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
					return;
				} else {
					JSONObject json2 = new JSONObject();
					 json2.put("form", "Email address authorized");
					// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
					out.print(json2);
				}
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