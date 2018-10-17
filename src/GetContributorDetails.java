

import java.io.IOException;
import java.io.PrintWriter;

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
 * Servlet implementation class GetContributorDetails
 */
@WebServlet("/getcontributor")
public class GetContributorDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetContributorDetails() {
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

		String owner_git_id = request.getParameter("owner_git_id");
		
		PrintWriter out = response.getWriter();
		
		if(owner_git_id == null ) {
			JSONObject json2 = new JSONObject();
			json2.put("Error", "owner git id not sent");
			out.println(json2);
			return;
		}
		try { 
			DBcontributor db_contrib = run.query("select * from contributor where git_id = ?",contributor_results,owner_git_id);				
			if (db_contrib == null) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "Contributor not found");
				// Assuming your json object is **jsonObject**, perform the following, it will
				// return your json object
				out.print(json2);
		}  else {
			JSONObject json2 = new JSONObject();
			json2.put("website", db_contrib.getWebsite());
			json2.put("bio", db_contrib.getBio());
			json2.put("name", db_contrib.getName());
			json2.put("email", db_contrib.getEmail());
			json2.put("avatar_url", db_contrib.getAvatar_url());
			out.print(json2);
		} 
		}catch (Exception ex) { ex.printStackTrace(out);}
		
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
