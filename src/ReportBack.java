

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
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
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
		DataSource dataSource = CustomDataSource.getInstance();
		QueryRunner run = new QueryRunner(dataSource);
      ResultSetHandler<DBcontributor> contributor_results= new BeanHandler<DBcontributor>(DBcontributor.class);
      ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);
      ResultSetHandler<DBSuccess> success_results = new BeanHandler<DBSuccess>(DBSuccess.class);
		String success = request.getParameter("success");
		PrintWriter out = response.getWriter();
		String code = request.getParameter("code");
		String git_url = request.getParameter("git_url");
		String device_id = request.getParameter("device_id");
		try { 
				
				
				if (success.equals("true")) {
					DBSuccess log_success = run.query("select * from success_code where success_code = ? ",success_results,code);
					if (log_success == null) {
						JSONObject json2 = new JSONObject();
						json2.put("Error", "code not found" );
						out.println(json2);
						return;
					} else {
						int increment_upvote = run.update("update devices set upvotes = upvotes + 1 where devices_id = ? and owner_git_url = ? ",device_id,git_url);
						if (increment_upvote != 1) {
							JSONObject json2 = new JSONObject();
							json2.put("Success", "Votes Updated" );
							out.println(json2);
							return;	
						}
					}
					
				}
				
				if (success.equals("false")) {
					DBSuccess log_success = run.query("select * from success_code where success_code = ? ",success_results,code);
					if (log_success == null) {
						JSONObject json2 = new JSONObject();
						json2.put("Error", "Code not found" );
						out.println(json2);
						return;
					} else {
						int increment_upvote = run.update("update devices set downvotes = downvotes + 1 where devices_id = ? and owner_git_url = ? ",device_id,git_url);
						if (increment_upvote != 1) {
							JSONObject json2 = new JSONObject();
							json2.put("Success", "Votes Updated" );
							out.println(json2);
							return;	
						}
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
