

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
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.json.JSONArray;
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			JSONArray json_array = new JSONArray();

			while( request.getParameter("git_url" + i) != null) {
				
				
				String is_valid = getCommits(request.getParameter("git_url" + i), git_id, out);
				if(! is_valid.startsWith("ok")) {
					
					json_array.put("Error at device " + Integer.toString(i) + " " + is_valid);
					// Assuming your json object is **jsonObject**, perform the following, it will
					// return your json object
				} else {
					json_array.put("Commit description :" + is_valid.replaceAll("ok", ""));
				}
				PreparedStatement stmt = con.prepareStatement("replace into devices (device_id,name,owner_git_id, contributor_email,git_url) values (?,?,?,?,?)");
			    stmt.setObject(1, request.getParameter("device_id" + i));
			    stmt.setObject(2, request.getParameter("device_name" + i));
			    stmt.setObject(3, git_id);
			    stmt.setObject(4, git_email);
			    stmt.setObject(5, request.getParameter("git_url" + i));
			    
			    stmt.executeUpdate();
				i++;
			}
			out.println(json_array);
			
		    
		}catch (Exception ex) { ex.printStackTrace(out);}
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub 
		doGet(request, response);
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
			   
			   Repository repository = cloned_git.getRepository();
			   
			   RevWalk revWalk = new RevWalk( repository );  
				   revWalk.sort( RevSort.COMMIT_TIME_DESC );
				   Map<String, Ref> allRefs = repository.getRefDatabase().getRefs( RefDatabase.ALL );
				   for( Ref ref : allRefs.values() ) {
				     RevCommit commit = revWalk.parseCommit( ref.getLeaf().getObjectId() );
				     revWalk.markStart( commit );
				   }
				   RevCommit newestCommit = revWalk.next();
				 
			   
			   
			   String commit_definition = newestCommit.getShortMessage();
			   
			   long commit_time = newestCommit.getCommitTime();
			   long now = System.currentTimeMillis();
			   
			   if (commit_time > (now - (1000 * 60 * 60 * 24 * 7))) {
				   return ("latest commit too young");
			   }
			   return "ok " + commit_definition;
			   			   
		 } catch (Exception ex) { ex.printStackTrace(out); }
		  return null;
		 
	 }
}
