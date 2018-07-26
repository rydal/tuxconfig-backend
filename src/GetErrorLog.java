
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.OAuthService;

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String git_url = null;
		String device_id = null;
		String owner_git_id = null;
		
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");

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
			if (items.size() != 1) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "multiple file uploads");
				out.println(json2);
				return;
			}
			FileItem uploaded_log = items.get(0);

			String issue_url;
			String clientId = "af9623acb5be449d2aa7";
			String clientSecret = "37260e1b85ec32a26530486562879071765c5b24";
			// The fluent API relieves the user from having to deal with manual deallocation
			// of system
			// resources at the cost of having to buffer response content in memory in some
			// cases.
			String output = "";
			InputStream is = null;
			BufferedReader bfReader = null;

			is = uploaded_log.getInputStream();
			bfReader = new BufferedReader(new InputStreamReader(is));
			String temp = null;
			while ((temp = bfReader.readLine()) != null) {
				if (temp.contains("device_id")) {
					temp = temp.replace("device_id", "").trim();
					device_id = temp;
				} else if (temp.contains("owner_git_id")) {
					temp = temp.replace("owner_git_id", "").trim();
					owner_git_id = temp;
				} else {
					output += temp;

				}
			}


			PreparedStatement get_git_url = con
					.prepareStatement("select git_url  from devices where device_id = ? and owner_git_id = ? ");
			get_git_url.setObject(1, device_id);
			get_git_url.setObject(2, owner_git_id);

			ResultSet get_url = get_git_url.executeQuery();
			if (!get_url.next()) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "device not available");
				out.println(json2);
				return;
			} else {
				git_url = (String) get_url.getObject("git_url");
			}
			git_url = git_url.replaceAll("\\.git", "");
			git_url = git_url.replaceAll("https://", "https://api.");
			git_url = git_url.replaceAll("github.com", "github.com/repos");
			
			issue_url = git_url + "/issues";
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(issue_url + "?client_id=" + clientId + "&client_secret=" + clientSecret);
			httpGet.setHeader("Accept", "application/vnd.github.v3+json");
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			
			out.println(response1.getStatusLine());
			   String inputLine ;
			BufferedReader br = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
			
			boolean seen = false;
			while ((inputLine = br.readLine()) != null) {
	              if( inputLine.contains("^Configure me " + uploaded_log.hashCode())) {
	            	  seen  = true;
	            	  
	              }
	              if (inputLine.contains(device_id)) {
	            	 inputLine = inputLine.replace(":", "\\:");
	              }
	       }
			if (!seen) {
				byte[] body = uploaded_log.get();
				String body_string = new String(body);
				
				
				CloseableHttpClient send_issue_client = HttpClients.createDefault();
				HttpPost httpPost = new HttpPost((issue_url + "?client_id=" + clientId + "&client_secret=" + clientSecret));
				httpPost.setHeader("Accept", "application/vnd.github.v3+json");
				httpPost.setHeader("Authorization", "token 199f04bde200233cb1fb0852eb70939145eea564");
				 
				 StringEntity json_parameters = new StringEntity ( "{\"title\":Configure me " + uploaded_log.hashCode() + ", body : " + body_string + ", labels : bug }");
				
				 out.write(EntityUtils.toString(json_parameters));
				    
				    httpPost.setEntity(json_parameters);
				    CloseableHttpResponse post_response = send_issue_client.execute(httpPost);
				    out.println(post_response.getStatusLine());
			
				    BufferedReader br2 = new BufferedReader(new InputStreamReader(post_response.getEntity().getContent()));
					

					while ((inputLine = br2.readLine()) != null) {
						out.write(inputLine);
					}
				    
			}
			
	       br.close();

		} catch (Exception ex) {
			ex.printStackTrace(out);
		}

	}
}
