
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
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Servlet implementation class ReportSuccess
 */
@WebServlet("/success")
public class ReportSuccess extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReportSuccess() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String token = request.getParameter("token");
		String success = request.getParameter("success");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");

			if (success.equals("yes")) {
				PreparedStatement get_device = con.prepareStatement(
						"select devices_device_id, devices_owner_git_id from success_code where success_code = ?");
				get_device.setObject(1, token);
				ResultSet got_device = get_device.executeQuery();
				if (!got_device.next()) {

					JSONObject json2 = new JSONObject();
					json2.put("success", "fail");
					// Assuming your json object is **jsonObject**, perform the following, it will
					// return your json object
					out.print(json2);
					return;
				} else {
					PreparedStatement add_to_votes = con.prepareStatement("update devices set upvotes=upvotes+1 where device_id=? and owner_git_id=?" );
					add_to_votes.setObject(1, got_device.getString("devices_device_id"));
					add_to_votes.setObject(2, got_device.getString("devices_owner_git_id"));
			     	add_to_votes.executeUpdate();
			     	out.println(add_to_votes);
					
				}
			} else if (success.equals("no")) {

			}
		} catch (Exception ex) {
			ex.printStackTrace(out);
		}
		;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
