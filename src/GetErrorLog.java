

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.OAuthService;
import com.mashape.unirest.http.Unirest;

/**
 * Servlet implementation class GetErrorLog
 */
@WebServlet("/geterrorlog")
public class GetErrorLog extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetErrorLog() {
        super();
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String device_id = request.getParameter("device_id");
		String owner_git_id = request.getParameter("owner_git_id");
		String git_url = null;
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost/linuxconf","arwen","imleaving");
		
					PreparedStatement get_git_url = con.prepareStatement("select from devices git_url where device_id = ? and owner_git_id = ? ");
					get_git_url.setObject(1 , device_id);
					get_git_url.setObject(2 , owner_git_id);
					
					ResultSet get_url = get_git_url.executeQuery();
					if(! get_url.next()) {
						JSONObject json2 = new JSONObject();
						json2.put("Error", "device not available" );
						out.println(json2);
						return;
					} else {
						git_url = (String) get_url.getObject("git_url");
					}
				
		
		
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Configure a repository (to ensure a secure temp location is used)
		ServletContext servletContext = this.getServletConfig().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(repository);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the requeste
		List<FileItem> items = upload.parseRequest(request);
		if (items.size() > 1) {
			JSONObject json2 = new JSONObject();
			json2.put("Error", "multiple file uploads" );
			out.println(json2);
			return;
		}
		FileItem uploaded_log = items.get(1);
		byte[] data = uploaded_log.get();
		
		String issue_url = git_url + "/issues";
		String oauth_token = "562ddba46a97992e54a5efa60616c1a6dfe5c7cc";
	
		// The fluent API relieves the user from having to deal with manual deallocation of system
		// resources at the cost of having to buffer response content in memory in some cases.

		Unirest.post(git_url)
		  .queryString("access_token", oauth_token)
		  .field("labels", )
		  .asJson()
		
		} catch (Exception ex) { ex.printStackTrace(out);
	
	}

}
