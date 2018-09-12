

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.json.JSONObject;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class GitAuthCallback
 */
@WebServlet("/gitauthcallback")
public class GitAuthCallback extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GitAuthCallback() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
		try {
		
			OAuth20Service service = (OAuth20Service) session.getAttribute("auth_manager");
        // Trade the Request Token and Verfier for the Access Token
	        final OAuth2AccessToken accessToken = service.getAccessToken(request.getParameter("code"));
	        
	        // Now let's go and ask for a protected resource!
	        final OAuthRequest github_request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
	        service.signRequest(accessToken, github_request);
        final Response github_response = service.execute(github_request);
        
               
        String data = github_response.getBody();
        JSONObject obj = new JSONObject(data);
        int git_id = obj.getInt("id");
        String repos_url = obj.getString("repos_url");
        String git_name = obj.getString("name");
        String location = obj.getString("location");
        String email = obj.getString("email");
        String bio = obj.getString("bio");
        String website = (String) session.getAttribute("website");
    	DataSource dataSource = CustomDataSource.getInstance();
		QueryRunner run = new QueryRunner(dataSource);

		
		out.write(repos_url);
		 //get access token
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httppost = new HttpGet(repos_url + "?access_token=" + accessToken.getAccessToken());

     //Execute and get the response.
     HttpResponse git_auth_response = httpclient.execute(httppost);
     HttpEntity entity = git_auth_response.getEntity();
     if (entity == null) {
    	 out.write("Error, could not find repository url");
     }
    	    InputStream instream = entity.getContent();
    	    StringWriter writer = new StringWriter();
    	    IOUtils.copy(instream, writer);
    	    String theString = writer.toString();    	        // do something useful
     		instream.close();
		ResultSetHandler<DBcontributor> contributor_results= new BeanHandler<DBcontributor>(DBcontributor.class);
      ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);
      int contributor_result = run.update("replace into contributor (email, website, name,location,git_id,git_token ) values (?,?,?,?,?,?)",email,website,git_name, location,git_id,accessToken.getAccessToken());
      if (contributor_result != 1) {
    	  out.write("Error");
    	  return;
      }
      JSONObject repos_obj = new JSONObject(theString);
      JsonArray jsonObject = new JsonParser()
    	        .parse(repos_url)
    	        .getAsJsonArray();

    	List<String> names = new ArrayList<>();
    	for (JsonElement jsonElement : jsonObject) {
    	    names.add(jsonElement.getAsJsonObject().get("html_url").getAsString());
    	}
    	session.setAttribute("repo_names", names);
    	response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/CreateUser.jsp");
    	  
        
        
        
		}

		  
		  

	catch (Exception ex) { ex.printStackTrace(out); }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
