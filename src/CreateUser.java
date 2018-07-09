

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

/**
 * Servlet implementation class SetRepositries
 */
@WebServlet("/createuser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateUser() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		ArrayList<String> clone_urls = new ArrayList<String>();
		
		try { 
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost/linuxconf","arwen","imleaving");  
			
		    String git_id = (String) session.getAttribute("git_id");
		    String git_email = (String) session.getAttribute("git_email");
		    
		    String webpage = request.getParameter("url");
		    String description = request.getParameter("desctiption");
		    
		    
		    
		    PreparedStatement contributor_details = con.prepareStatement("replace into contributor (url,description,owner_git_id, email) values (?,?,?,?) ");
		    contributor_details.setObject(1, webpage);
		    contributor_details.setObject(2, description);
		    contributor_details.setObject(3, git_id);
		    contributor_details.setObject(4, git_email);
		    contributor_details.executeUpdate();
		    
		    
		    int i = 0;
			while( request.getParameter("git_url" + i) != null) {
				
				
				getCommits(request.getParameter("git_url" + i), out);
				PreparedStatement stmt = con.prepareStatement("replace into devices (device_id,name,owner_git_id, contributor_email,git_url) values (?,?,?,?,?)");
			    stmt.setObject(1, request.getParameter("device_id" + i));
			    stmt.setObject(2, request.getParameter("device_name" + i));
			    stmt.setObject(3, git_id);
			    stmt.setObject(4, git_email);
			    stmt.setObject(5, request.getParameter("git_url" + i));
			    stmt.executeUpdate();
				i++;
			}
			
		    
		}catch (Exception ex) { ex.printStackTrace(out);}
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	 public boolean getCommits(String url, PrintWriter out) {
		 try {
		 
			   
			   Git git = Git.cloneRepository()
			  .setURI(url)
			  .setDirectory(new File("/tmp/linuxconf/" + url))
			  .setBranchesToClone( Arrays.asList( "refs/heads/master" ) )
			  .setBranch( "refs/heads/master" )
			  .call();
		 
			   git.checkout().setName( "<id-to-commit>" ).call();



		 } catch (Exception ex) { ex.printStackTrace(out); }
		 return true; 
		 
	 }
}
