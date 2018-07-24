

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class ReportBack
 */
@WebServlet("/reportback")
public class ReportBack extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportBack() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String success = request.getParameter("success");
		PrintWriter out = response.getWriter();
		String token = request.getParameter("token");
		try { 
				Class.forName("com.mysql.jdbc.Driver");  
				Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost/linuxconf","arwen","imleaving");
				
				if (success.equals("true")) {
					PreparedStatement log_success = con.prepareStatement("select * from success_code where success_code = ? ");
					log_success.setObject(1, token);
					ResultSet result = log_success.executeQuery();
					if (! result.next()) {
						JSONObject json2 = new JSONObject();
						json2.put("Error", "token not found" );
						out.println(json2);
						return;
					} else {
						PreparedStatement increment_upvote = con.prepareStatement("update devices set upvotes = upvotes + 1 where devices_id = ? and owner_git_id = ? ");
						increment_upvote.executeQuery();
					}
					
				}
				
				if (success.equals("false")) {
					PreparedStatement log_success = con.prepareStatement("select * from success_code where success_code = ? ");
					log_success.setObject(1, token);
					ResultSet result = log_success.executeQuery();
					if (! result.next()) {
						JSONObject json2 = new JSONObject();
						json2.put("Error", "token not found" );
						out.println(json2);
						return;
					} else {
						PreparedStatement increment_downvote = con.prepareStatement("update devices set downvotes = downvotes + 1 where devices_id = ? and owner_git_id = ? ");
						increment_downvote.executeQuery();
					}
					
				}
				
				
			} catch (Exception ex) { ex.printStackTrace(out); } 		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
