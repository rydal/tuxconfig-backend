

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		String device_id = request.getParameter("deviceid");
		PrintWriter out = response.getWriter();
		
		try { 
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost/linuxconf","arwen","imleaving");  
			
			PreparedStatement get_device_request = con.prepareStatement("select * from devices where device_id = ?");
			get_device_request.setObject(1, device_id);
			
			ResultSet got_device_request = get_device_request.executeQuery();
			if (!got_device_request.next()) {
				JSONObject json2 = new JSONObject();
				json2.put("form", "Device not found");
				// Assuming your json object is **jsonObject**, perform the following, it will
				// return your json object
				out.print(json2);
			}  else {
				PreparedStatement insert_success_code = con.prepareStatement("insert into success_code ( success_code, devices_device_id, devices_owner_git_id, timestamp ) values ( ?,?,?,?)");
				insert_success_code.setObject(1, randomString(64)); 
				insert_success_code.setObject(2, device_id);
				insert_success_code.setObject(3, got_device_request.getInt("owner_git_id"));
				java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
				insert_success_code.setObject(4, date);
				insert_success_code.executeUpdate();
				JSONObject json2 = new JSONObject();
				
				json2.put("url", got_device_request.getString("git_url"));
				json2.put("commit", got_device_request.getString("git_commit"));
				
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
