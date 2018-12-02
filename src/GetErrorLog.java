
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
import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
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
import org.omg.PortableInterceptor.SUCCESSFUL;

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

		DataSource dataSource = CustomDataSource.getInstance();
		QueryRunner run = new QueryRunner(dataSource);
		ResultSetHandler<DBcontributor> contributor_results = new BeanHandler<DBcontributor>(DBcontributor.class);
		ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);
		ResultSetHandler<DBSuccess> success_results = new BeanHandler<DBSuccess>(DBSuccess.class);

		PrintWriter out = response.getWriter();
		String git_url = request.getParameter("git_url");
		String device_id = request.getParameter("device_id");
		String code  = request.getParameter("code");
		
		JSONObject json3 = new JSONObject();

		if (git_url == null) json3.put("Error", "Git url not sent correctly");
		if (device_id == null) json3.put("Error", "Device id not sent correctly");
		
		if (code  == null) json3.put("Error", "Success code not sent correctly");
		
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


						// Check status
			
			DBcontributor get_git_token = run.query(
					"select a.git_token  from contributor a, git_url b, success_code c where a.git_id = b.owner_git_id  and b.git_url = c.git_url and c.git_url = ?",
					contributor_results, git_url);
			if (get_git_token == null) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "Git oauth token not found");
				out.println(json2);
				return;
			}
			String git_token = get_git_token.getGit_token();
			git_url = git_url.replaceAll("\\.git", "");
			git_url = git_url.replaceAll("https://", "https://api.");
			git_url = git_url.replaceAll("github.com", "github.com/repos");

			issue_url = git_url + "/issues";

			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(issue_url + "?client_id=" + clientId + "&client_secret=" + clientSecret);
			httpGet.setHeader("Accept", "application/vnd.github.v3+json");
			CloseableHttpResponse response1 = httpclient.execute(httpGet);

			if ( response1.getStatusLine().getStatusCode() >= 400) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "Error pulling issues");
				out.println(json2);
				return;

			}

			String inputLine;
			BufferedReader br = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
			byte[] file_in_bytes = uploaded_log.get();

			int file_size = file_in_bytes.length;

			boolean seen = false;
			while ((inputLine = br.readLine()) != null) {
				if (inputLine.contains("Tuxconfig " + file_size)) {
					seen = true;

				}
			}
			if (!seen) {

				byte[] body = uploaded_log.get();
				String body_string = new String(body);
				body_string = body_string.replace("\n", "\\n");

				CloseableHttpClient send_issue_client = HttpClients.createDefault();
				HttpPost httpPost = new HttpPost(issue_url);
				httpPost.setHeader("Accept", "application/vnd.github.v3+json");
				httpPost.setHeader("Authorization", "token " + git_token );
				 
				 //StringEntity json_parameters = new StringEntity ( "{ \"title\" : \"Tuxconfig " + uploaded_log.hashCode() + " \" , \"body\" : \"" + body_string + "\" }");
				//StringEntity json_parameters = new StringEntity ( "{ \"title\" : \"Configure me " + md5hash + " \""
					//	+ " , \"body\" : \"" + body_string.trim() + "\" }");
				  
				StringEntity json_parameters = new StringEntity("{  \"title\": \"Tuxconfig " + file_size + " \", \"body\": \"" +  body_string + " \" }");
				httpPost.setEntity(json_parameters);
				    
				    CloseableHttpResponse post_response = send_issue_client.execute(httpPost);
				    out.println(post_response.getStatusLine());
				    
					if (post_response.getStatusLine().getStatusCode() >=  400 ) {
				    	JSONObject json2 = new JSONObject();
						json2.put("Error ", "Error uploading  issue");
						out.println(json2);
						return;
				    } else {
				    	JSONObject json2 = new JSONObject();
						json2.put("Ok", "Posted issue successfully");
						out.println(json2);
						return;
				    }

			} else {
				JSONObject json2 = new JSONObject();
				json2.put("Ok", "Issue already posted");
				out.println(json2);
				return;
			} 

		} catch (Exception ex) {
			ex.printStackTrace(out);
		}

	}
}
