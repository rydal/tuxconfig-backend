

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * Servlet implementation class GitAuthCallback
 */
@WebServlet("/gitauth")
public class GitAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GitAuth() {
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
		 final String clientId = "af9623acb5be449d2aa7";
	        final String clientSecret = "37260e1b85ec32a26530486562879071765c5b24";
	        final OAuth20Service service = new ServiceBuilder(clientId)
	                .apiSecret(clientSecret)
	                .scope("rpublic_repo")
	                .callback("https://linuxconf.feedthepenguin.org/hehe/gitauthcallback")
	                .build(GitHubApi.instance());
	        
	        String website = request.getParameter("website");
	        session.setAttribute("website", website);
	        // Obtain the Authorization URL
	        final String authorizationUrl = service.getAuthorizationUrl();
	        session.setAttribute("auth_manager", service);
	        response.sendRedirect(authorizationUrl);
	        
	        		
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
