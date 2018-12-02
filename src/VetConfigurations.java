
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
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.json.JSONObject;

/**
 * Servlet implementation class AdminConsole
 */
@WebServlet("/vetconfigurations")
public class VetConfigurations extends HttpServlet {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DataSource dataSource = CustomDataSource.getInstance();
		QueryRunner run = new QueryRunner(dataSource);
      ResultSetHandler<DBcontributor> contributor_results= new BeanHandler<DBcontributor>(DBcontributor.class);
      ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);
		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
	
		String git_url = null;
		String command = null;
		String commit_hash = null;
		
		

		git_url = request.getParameter("git_url");
		if (git_url == null) {
			JSONObject json2 = new JSONObject();
			json2.put("form", "device id not received");
			// Assuming your json object is **jsonObject**, perform the following, it will
			// return your json object
			out.print(json2);
			return;
		}

		

		command = request.getParameter("command");
		if (command == null) {
			JSONObject json2 = new JSONObject();
			json2.put("form", "command not received");
			// Assuming your json object is **jsonObject**, perform the following, it will
			// return your json object
			out.print(json2);
			return;
		}

		commit_hash = request.getParameter("hash");
		if (commit_hash == null) {
			JSONObject json2 = new JSONObject();
			json2.put("form", "commit hash  not received");
			// Assuming your json object is **jsonObject**, perform the following, it will
			// return your json object
			out.print(json2);
			return;
		}

		
		try {
			
			
				if (command.equals("authorise")) {
					int rows_updated  = run.update("update git_url set authorised = '1' where git_url = ? and commit_hash = ?",git_url,commit_hash);
					
					if (rows_updated != 0) {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Configuration authorised");
						// Assuming your json object is **jsonObject**, perform the following, it will
						// return your json object
						out.print(json2);
					} else {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Configuration not found");
						// Assuming your json object is **jsonObject**, perform the following, it will
						// return your json object
					}
				}
				else if (command.equals("unauthorise")) {
					int rows_updated  = run.update("update git_url set authorised = '0' where git_url = ? and commit_hash = ? ",git_url,commit_hash);
					if (rows_updated != 0) {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Configuration unauthorised");
						// Assuming your json object is **jsonObject**, perform the following, it will
						// return your json object
						out.print(json2);
					} else {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Device not found");
						// Assuming your json object is **jsonObject**, perform the following, it will
						// return your json object
					}
				}

				else if (command.equals("delete")) {
					int rows_updated  = run.update("delete from git_url where git_url = ?  and commit_hash = ? ",git_url,commit_hash);
					if (rows_updated != 0) {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Configuration unauthorised");
						// Assuming your json object is **jsonObject**, perform the following, it will
						// return your json object
						out.print(json2);
					} else {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Configuration not found");
						// Assuming your json object is **jsonObject**, perform the following, it will
						// return your json object
					}
				} else {
					JSONObject json2 = new JSONObject();
					json2.put("form", "Error in command sent");
					// Assuming your json object is **jsonObject**, perform the following, it will
					// return your json object
					out.print(json2);
				}

		

		} catch (Exception ex) {
			ex.printStackTrace(out);
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
