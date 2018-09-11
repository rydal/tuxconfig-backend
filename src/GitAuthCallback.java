

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	        System.out.println("Now we're going to access a protected resource...");
	        final OAuthRequest github_request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
	        service.signRequest(accessToken, github_request);
        final Response github_response = service.execute(github_request);
        String data = github_response.getBody();
        JSONObject obj = new JSONObject(data);

        
        
        
        
        
        
		}

		  
		  

	catch (Exception ex) { ex.printStackTrace(); }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
