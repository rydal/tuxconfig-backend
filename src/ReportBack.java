

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
		String commit_hash = request.getParameter("commit_hash");
		
		JSONObject json3 = new JSONObject();
		
		if (git_url == null) json3.put("Error", "Git url not recieved correctly");
		if (success == null) json3.put("Error", "Success status not recieved correctly");
		if (device_id == null) json3.put("Error", "Device id not received correctly");
		if (code  == null) json3.put("Error", "Code not recieved correctly");
		if (commit_hash == null) json3.put("Error", "Commit hash not recieved correctly");
		
		if (json3.length() != 0) {
			out.print(json3);
			return;
		}
		 String[]  each_side = device_id.split(":");
		  if (each_side.length != 2) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "Device id not of correct format");
				out.println(json2);
				return;
		  }
		  while (each_side[0].length() < 4) {
		 	  each_side[0] = "0" + each_side[0];
		  }
		  
		  while (each_side[1].length() < 4) {
		 	  each_side[1] = each_side[1] + "0";
		  }
	device_id = each_side[0] + ":" + each_side[1];
		
		try { 
				
				
				if (success.equals("true")) {
						int increment_upvote = run.update("update git_url set upvotes = upvotes + 1 where git_url = ? and commit_hash = ? ",git_url,commit_hash);
						if (increment_upvote == 1 ) {	
						JSONObject json2 = new JSONObject();
							json2.put("Success", "Votes Updated" );
							out.println(json2);
							return;	
						} else {
							JSONObject json2 = new JSONObject();
							json2.put("Failed", "Repository and commit hash not found" );
							out.println(json2);
							return;	
						
						}
					
					
				}
				
				else if (success.equals("false")) {
						int increment_downvote = run.update("update git_url set downvotes = downvotes + 1 where  git_url = ? and commit_hash = ?",git_url,commit_hash);
						if (increment_downvote == 1 ) {	
							
						JSONObject json2 = new JSONObject();
							json2.put("Success", "Votes Updated" );
							out.println(json2);
							return;	
				} else {
					JSONObject json2 = new JSONObject();
					json2.put("Failed", "Repository and commit hash not found" );
					out.println(json2);
					return;	
				
				}
					
					
				}else {
				
				JSONObject json2 = new JSONObject();
				json2.put("Error", "Could not determine success type");
				out.println(json2);
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
