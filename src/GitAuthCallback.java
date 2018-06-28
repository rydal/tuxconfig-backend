

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
		OAuth2AccessToken accessToken = service.getAccessToken(verifier);

        final OAuthRequest git_request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, git_request);
        final Response git_response = service.execute(git_request);
        
        String git_result = git_response.getBody();
        JSONObject json = new JSONObject(git_result);
		String repos_url = (String) json.get("repos_url").toString();
		
		List clone_urls = new ArrayList<String>(); 
			
		URL url = new URL(repos_url);
		    InputStream is = null;
		    DataInputStream dis;
		    String s;
		    StringBuffer sb = new StringBuffer();

		      is = url.openStream();
		      dis = new DataInputStream(new BufferedInputStream(is));
		
		
		while ((s = dis.readLine()) != null) {
			final Pattern p = Pattern.compile("^\\s+\"url\".*");
			Matcher m = p.matcher(s);
			if(m.find()) {
				out.write(s);
				out.flush();
			}
	
		}
		is.close();

		for (int i = 0 ; i < clone_urls.size() ; i++) {
			out.write("item" + i + clone_urls.get(i));
		}
		} catch (Exception ex) { ex.printStackTrace(); }

	}

}
