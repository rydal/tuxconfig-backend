

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * Servlet implementation class GitAuthCallback
 */
@WebServlet("/gitauthcallback")
public class GitAuthCallback extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(); 
		PrintWriter out = response.getWriter();
		String verifier = request.getParameter("code");
	    final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

	    if (verifier == null) {
	    	out.write("Oauth not completed successfully");
	    	return;
	    }
		
		try {
		OAuth20Service service = (OAuth20Service) session.getAttribute("git_login"); 
		
		if (service == null) {
			out.write("Github login hasn't worked, please retry");
			return;
		}
		 
		OAuth2AccessToken accessToken = service.getAccessToken(verifier);

        final OAuthRequest git_request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, git_request);
        final Response git_response = service.execute(git_request);
        
        String git_result = git_response.getBody();
        JSONObject json = new JSONObject(git_result);
		String repos_url = (String) json.get("repos_url").toString();
		String git_id = (String) json.get("id").toString();
		String git_email = (String) json.get("email").toString();

		List<String> clone_urls = new ArrayList<String>(); 
			
		URL url = new URL(repos_url);
		
		      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		      // write the output to stdout
		      String line = reader.readLine();
		      String[] lines = line.split(",");
		      for (String current_line : lines)
		      {
		          Pattern r = Pattern.compile("\"clone_url\"");
		    	  Matcher m = r.matcher(current_line);
		          if (m.find( )) {
		             String[] clone_url = current_line.split(":");
		             String final_url = clone_url[1] + clone_url[2];
		             final_url = final_url.replace("https//", "https://");
		             clone_urls.add(final_url.replaceAll("\"", ""));
		             
		      }
		      }
		session.setAttribute("clone_urls", clone_urls);
		session.setAttribute("git_id", git_id);
		session.setAttribute("git_email", git_email);
		response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/CreateUser.jsp");
		} catch (Exception ex) { ex.printStackTrace(); }

	}

}
