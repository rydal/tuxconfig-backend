

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.jgit.api.Git;
import org.json.JSONObject;

/**
 * Servlet implementation class GetDevice
 */
@WebServlet("/getdevice")
public class GetDevice extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetDevice() {
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

		String device_id = request.getParameter("deviceid");
		String attempt_number  = request.getParameter("attempt");
		String description = request.getParameter("description");
		PrintWriter out = response.getWriter();
		
		if(device_id == null ) {
			JSONObject json2 = new JSONObject();
			json2.put("Error", "deviceid not sent");
			out.println(json2);
			return;
		}
		if(attempt_number == null ) {
			JSONObject json2 = new JSONObject();
			json2.put("Error", "attempt number not sent");
			out.println(json2);
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
				run.update("update devices set description = ? where device_id = ? ",description,device_id);
			ResultSetHandler<List<DBDevice>> rsh = new BeanListHandler<DBDevice>(DBDevice.class);
			List<DBDevice> rows = (List<DBDevice>) run.query("select * from devices inner join git_url on devices.devices_constraint = git_url. where devices.device_id = ? and git_url.authorised = '1' order by (git_url.upvotes - git_url.downvotes) desc  limit ?",device_results,device_id,Integer.parseInt(attempt_number));
			 if (Integer.parseInt(attempt_number) > rows.size()) {
					JSONObject json2 = new JSONObject();
					json2.put("Error", "Device not found");
					// Assuming your json object is **jsonObject**, perform the following, it will
					// return your json object
					out.print(json2);
					return;
			 }
			DBDevice db_device = rows.get(Integer.parseInt(attempt_number));
				
			if (db_device == null) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "Device not found");
				// Assuming your json object is **jsonObject**, perform the following, it will
				// return your json object
				out.print(json2);
			}  else {
				String randomString = randomString(64);
				run.update("insert into success_code ( success_code, device_id, git_url, timestamp ) values ( ?,?,?,?)",randomString, device_id,db_device.getGit_url(),new java.sql.Timestamp(new java.util.Date().getTime()));
				
				
				JSONObject json2 = new JSONObject();
				json2.put("success_code", randomString);
				json2.put("git_url", db_device.getGit_url());
				json2.put("commit_hash", db_device.getCommit_hash());
				int vote_difference = db_device.getUpvotes() - db_device.getDownvotes();
				json2.put("vote_difference", Integer.toString(vote_difference));
				json2.put("owner_git_id", db_device.getOwner_git_id());
				json2.put("module", db_device.getModule());
				
				
				
				// Assuming your json object is **jsonObject**, perform the following, it will
				// return your json object
				out.print(json2);
				
			}

			
		} catch (Exception ex) { ex.printStackTrace(out);}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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
