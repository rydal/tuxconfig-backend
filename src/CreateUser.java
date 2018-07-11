

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

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.DescribeCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
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
import org.json.JSONObject;

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
				
				
				String is_valid = getCommits(request.getParameter("git_url" + i), git_id, out);
				if(! is_valid.equals("ok")) {
					JSONObject json2 = new JSONObject();
					
					json2.put("error", "Error at device " + Integer.toString(i) + is_valid);
					// Assuming your json object is **jsonObject**, perform the following, it will
					// return your json object
					out.print(json2);
				}
				PreparedStatement stmt = con.prepareStatement("replace into devices (device_id,name,owner_git_id, contributor_email,git_url,git_commit) values (?,?,?,?,?,?)");
			    stmt.setObject(1, request.getParameter("device_id" + i));
			    stmt.setObject(2, request.getParameter("device_name" + i));
			    stmt.setObject(3, git_id);
			    stmt.setObject(4, git_email);
			    stmt.setObject(5, request.getParameter("git_url" + i));
			    contributor_details.setObject(6, request.getParameter("git_commit" + i));
			    
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

	 public String getCommits(String url, String git_id, PrintWriter out) {
		 try {
			   
			 FileUtils.deleteDirectory(new File("/tmp/linuxconf/" + url + ":" + git_id));

			   Git cloned_git = Git.cloneRepository()
			  .setURI(url)
			  .setDirectory(new File("/tmp/linuxconf/" + url + ":" + git_id))
			  .setBranchesToClone( Arrays.asList( "refs/heads/master" ) )
			  .setBranch( "refs/heads/master" )
			  .call();
			   
			   File git_dir = new File("/tmp/linuxconf/" + url + "/.git");
			   
			   
			   Repository repository = cloned_git.getRepository();
			   
			   RevCommit youngestCommit = null;    
			   List<Ref> branches = new Git(repository).branchList().setListMode(ListMode.ALL).call();
			   RevWalk walk = new RevWalk(cloned_git.getRepository());
			       for(Ref branch : branches) {
			           RevCommit commit = walk.parseCommit(branch.getObjectId());
			           if(commit.getAuthorIdent().getWhen().compareTo(
			              youngestCommit.getAuthorIdent().getWhen()) > 0)
			              youngestCommit = commit;
			       }
			   
			   if (youngestCommit == null) {
				   return "not found";
			   }
			   String commit_definition = youngestCommit.getShortMessage();
			   
			   long commit_time = youngestCommit.getCommitTime();
			   long now = System.currentTimeMillis();
			   
			   if (commit_time < (now - (1000 * 60 * 60 * 24 * 7))) {
				   return ("commit too young");
			   }
			   return "ok";
			   			   
		 } catch (Exception ex) { ex.printStackTrace(out); }
		  return null;
		 
	 }
}
