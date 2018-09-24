

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

/**
 * Servlet implementation class SendVoteEmail
 */
@WebServlet("/sendvoteemail")
public class SendVoteEmail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendVoteEmail() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		DataSource dataSource = CustomDataSource.getInstance();
		QueryRunner run = new QueryRunner(dataSource);
		ResultSetHandler<DBcontributor> contributor_results = new BeanHandler<DBcontributor>(DBcontributor.class);
		ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);
		PrintWriter out = response.getWriter();
		if (! request.getRemoteAddr().equals(request.getLocalAddr())) {
			
			out.write("access to this servlet only allowed on localhost");
			return;
		}
		String email_address = request.getParameter("emailaddress");
		String vote_direction = request.getParameter("votedirection");
		
		if (vote_direction == null) {
			out.write("invalid vote direction");
			return;
		}
		if (email_address == null) {
			out.write("invalid submitted email address");
			return;
		}
		if (vote_direction.equals("up")) {
			new GenEmail(email_address, out, "Tuxconfig contribution going well","Your Tuxconfig contribution has a vote difference of more than 10. It's been recieved well"
					+" Check it's status at https://linuxconf.feedthepenguin.org");
		}
		if (vote_direction.equals("down")) {
			new GenEmail(email_address, out, "Tuxconfig contribution going badly","Your Tuxconfig contribution has a vote difference of less than 10. It's been recieved badly"
					+" Check it's status at https://linuxconf.feedthepenguin.org");
		}
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
