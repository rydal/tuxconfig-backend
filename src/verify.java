

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;


/**
 * Servlet implementation class verify
 */
@WebServlet("/verify")
public class verify extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public verify() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();

		response.setContentType("text/html");
		String code = request.getParameter("code");
		String email = request.getParameter("email");
		if(code == null || email == null) {
			out.write("Something has gone wrong");
			return;
	}   
		try{  
  			Class.forName("com.mysql.jdbc.Driver");  
  			Connection con=DriverManager.getConnection(  
  			"jdbc:mysql://localhost/linuxconf","arwen","imleaving");  
  			PreparedStatement stmt=con.prepareStatement("select email, password from user where email = ? ");  
  			stmt.setObject(1, email);
  			ResultSet rs=stmt.executeQuery();  

  			if(! rs.next())  
  			{
  				session.setAttribute("flag", "User not found");
					response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/resend_email.jsp");
  			}
  			
  				PreparedStatement stmt2=con.prepareStatement("select timestamp from user where email = ? and verify_code = ?  ");  
  	  			stmt2.setObject(1, email);
  	  			stmt2.setObject(2, code);
	  			ResultSet rs2=stmt2.executeQuery();  
  				if(!rs2.next()) {
  					session.setAttribute("flag", "Incorrect code");
  					response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/resend_email.jsp");

  				}
	  			
	  			long yourJUDae = Long.valueOf(rs2.getString("timestamp"));
	  			long dayAgo = System.currentTimeMillis() - (24 * 60 * 60 *  1000);
	  			
  				if (yourJUDae < dayAgo) {
  					rs.close();
  					stmt.close();
  					con.close();
  					session.setAttribute("flag", "Code has expired");
  					response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/resend_email.jsp");
  					
  				}
  					
  				PreparedStatement insert_statement=con.prepareStatement("update user set verified = ? where email = ?");
  				insert_statement.setObject(1, 1);
  				insert_statement.setObject(2, email);
  				
  				insert_statement.executeUpdate(); 
  				
  				rs.close();
					stmt.close();
					con.close();
					insert_statement.close();
					con.close();
					
					
				session.setAttribute("flag","Email address sucsessfully registered");
  				
				Cookie cookie3 = Servlets.getCookie(request, "password");

				if (cookie3 != null) {
					cookie3.setValue(rs.getString("password"));
					cookie3.setMaxAge(60 * 60 * 24 * 28);

					response.addCookie(cookie3);
				} else {
					Cookie password_cookie = new Cookie("password", rs.getString("password"));
					password_cookie.setMaxAge(60 * 60 * 24 * 28);
					response.addCookie(password_cookie);

				}
				Cookie cookie2 = Servlets.getCookie(request, "email");

				if (cookie2 != null) {
					cookie2.setValue(email);
					cookie2.setMaxAge(60 * 60 * 24 * 28);

					response.addCookie(cookie2);
				} else {
					Cookie email_cookie = new Cookie("email", rs.getString("email"));
					email_cookie.setMaxAge(60 * 60 * 24 * 28);
					response.addCookie(email_cookie);

				}
				
				response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/AdminConsole.jsp");
  				
			    	
  				con.close(); 
  			
  		} catch(java.sql.SQLIntegrityConstraintViolationException e) {}
  			catch(Exception e){ e.printStackTrace(out);}  
  			}  
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
}
}
