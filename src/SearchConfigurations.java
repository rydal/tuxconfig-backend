

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class SearchConfigurations
 */
@WebServlet("/searchconfigurations")
public class SearchConfigurations extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchConfigurations() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

		if (email == null) {
			JSONObject json2 = new JSONObject();
			json2.put("form", "Email address not received from cookie");
			// Assuming your json object is **jsonObject**, perform the following, it will
			// return your json object
			out.print(json2);
			return;
		}
		if (hash == null) {
			JSONObject json2 = new JSONObject();
			json2.put("form", "Password not received from cookie");
			// Assuming your json object is **jsonObject**, perform the following, it will
			// return your json object
			out.print(json2);
			return;
		}

		String device_id = null;
		String command = null;
		String commit_hash = null;
		String owner_git_id = null;
		
		

		device_id = request.getParameter("device_id");
		if (device_id == null) {
			JSONObject json2 = new JSONObject();
			json2.put("form", "device id not received");
			// Assuming your json object is **jsonObject**, perform the following, it will
			// return your json object
			out.print(json2);
			return;
		}


		owner_git_id = request.getParameter("owner_git_id");
		if (owner_git_id == null) {
			JSONObject json2 = new JSONObject();
			json2.put("form", "git id not received");
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
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");

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
				// Assuming your json object is **jsonObject**, perform the following, it will
				// return your json object
				out.print(json2);
				return;
			} else {
				PreparedStatement stmt2 = con
						.prepareStatement("SELECT email, password, authorised  FROM user where email = ?");
				stmt2.setObject(1, email);
				ResultSet rs2 = stmt2.executeQuery();

				if (!rs2.next()) {
					JSONObject json2 = new JSONObject();
					json2.put("form", "Could not find your email address");
					// Assuming your json object is **jsonObject**, perform the following, it will
					// return your json object
					out.print(json2);
					return;
				}
				if (rs2.getInt("authorised") == 0) {
					session.setAttribute("flag", "User not authorised, re-request authorization");
					JSONObject json2 = new JSONObject();
					json2.put("redirect", "https://linuxconf.feedthepenguin.org/hehe/RequestAuthorization.jsp");
					// Assuming your json object is **jsonObject**, perform the following, it will
					// return your json object
					out.print(json2);
				}
			}
			if (!hash.equals(rs.getString("password"))) {
				session.setAttribute("flag", "invalid password");
				response.setStatus(HttpServletResponse.SC_FOUND); // SC_FOUND = 302
				out.write("https://linuxconf.feedthepenguin.org/hehe/login.jsp");
				return;
			} else if (hash.equals(rs.getString("password"))) {
				if (command.equals("authorise")) {
					PreparedStatement aurthorise_device = con
							.prepareStatement("update devices set authorised = '1' where device_id = ? and commit_hash = ? and owner_git_id = ? ");
					aurthorise_device.setObject(1, device_id);
					aurthorise_device.setObject(2, commit_hash);
					aurthorise_device.setObject(3, owner_git_id);
					int rows_updated = aurthorise_device.executeUpdate();
					if (rows_updated == 1) {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Device authorised");
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
				else if (command.equals("unauthorise")) {
					PreparedStatement aurthorise_device = con
							.prepareStatement("update devices set authorised = '0' where device_id = ? and commit_hash = ? and owner_git_id = ? ");
					aurthorise_device.setObject(1, device_id);
					aurthorise_device.setObject(2, commit_hash);
					aurthorise_device.setObject(3, owner_git_id);
					int rows_updated = aurthorise_device.executeUpdate();
					if (rows_updated == 1) {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Device unauthorised");
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
					PreparedStatement aurthorise_device = con
							.prepareStatement("delete from devices where device_id = ? and commit_hash = ? and owner_git_id = ? ");
					aurthorise_device.setObject(1, device_id);
					aurthorise_device.setObject(2, commit_hash);
					aurthorise_device.setObject(3, owner_git_id);
					int rows_updated = aurthorise_device.executeUpdate();
					if (rows_updated == 1) {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Device unauthorised");
						// Assuming your json object is **jsonObject**, perform the following, it will
						// return your json object
						out.print(json2);
					} else {
						JSONObject json2 = new JSONObject();
						json2.put("form", "Device not found");
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

			}
			}
		
		
		catch (Exception ex) {
			ex.printStackTrace(out);
		}

	

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
