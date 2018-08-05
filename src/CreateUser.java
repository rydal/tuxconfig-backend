

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.internal.storage.dfs.DfsPacksChangedEvent;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Config;
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
		
		try ( 
			Connection con=DriverManager.getConnection(  
			"jdbc:mysql://localhost/linuxconf","arwen","imleaving");
			    PreparedStatement contributor_details = con.prepareStatement("replace into contributor ( url , description , owner_git_id , email) values (?,?,?,?) ");
) {  
			
		    String git_id = (String) session.getAttribute("git_id");
		    String git_email = (String) session.getAttribute("git_email");
		    
		    String webpage = request.getParameter("url");
		    String description = request.getParameter("desctiption");
		    
		    
		    
		    
		    contributor_details.setObject(1, webpage);
		    contributor_details.setObject(2, description);
		    contributor_details.setObject(3, git_id);
		    contributor_details.setObject(4, git_email);
		    
		    contributor_details.executeUpdate();
		    
		    
		    int i = 0;
			JSONArray json_array = new JSONArray();

			
			
			
			while( request.getParameter("git_url" + i) != null) {
				String commit_id = request.getParameter("commit_id" + i);
				String url = request.getParameter("git_url");
				
				if (commit_id == null) {
					JSONObject json2 = new JSONObject();
					json2.put("Error", "Commit id for project" + i + " not entered");
					out.println(json2);
					return;
				
				}
				
				String[] got_commits = getCommits(url,git_id, commit_id, out);
				
				if(! got_commits[0].equals("Success")) {
					
					json_array.put("Error importing repository: " + url + " Reason: " + got_commits[1]);
					// Assuming your json object is **jsonObject**, perform the following, it will
					// return your json object
				} else {
					
					//Assume version number starts at 0
				try( PreparedStatement get_version_number = con.prepareStatement("select max(version) as version from devices where device_id = ? and owner_git_id = ?");
				ResultSet got_version_number = get_version_number.executeQuery();
				PreparedStatement stmt = con.prepareStatement("replace into devices (device_id,owner_git_id,  version, name, commit_id, contributor_email,git_url) values (?,?,?,?,?,?,?)");) {
		
				int version_number = got_version_number.getInt("version") + 1;  
					
				stmt.setObject(1, got_commits[0]);
			    stmt.setObject(2, got_commits[1]);
			    stmt.setObject(3, got_commits[2]);
			    stmt.setObject(4, got_commits[3]);
			    stmt.setObject(5, got_commits[4]);
			    stmt.setObject(6, git_email);
			    stmt.setObject(7, request.getParameter("git_url" + i));
			    
			    stmt.executeUpdate();
				i++;
			}
			}
			out.println(json_array);
			
		    
			} }catch (Exception ex) { ex.printStackTrace(out);}
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse resp	onse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	 public String[] getCommits(String url,String git_id, String commit_id, PrintWriter out) {
		 try (Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost/linuxconf","arwen","imleaving");  
				PreparedStatement get_version_number = con.prepareStatement("select max(version) as version from devices where device_id = ? and owner_git_id = ?");
				ResultSet got_version_number = get_version_number.executeQuery(); ) { 
			 	int version = got_version_number.getInt("version");
		
			 String cloned_directory = "/tmp/linuxconf/" + url + ":" + git_id; 
			 FileUtils.deleteDirectory(new File(cloned_directory));

			   Git cloned_git = Git.cloneRepository()
			  .setURI(url)
			  .setDirectory(new File(cloned_directory))
			  .setBranchesToClone( Arrays.asList( "refs/heads/master" ) )
			  .setBranch( "refs/heads/master" )
			  .call();
			  
			   cloned_git.checkout().setName( commit_id ).call();
 
			   
			   Repository repo = cloned_git.getRepository();
			   
		       Set<String> remote_names = repo.getRemoteNames();
		       
		       //get commit message
			   
			   File config_file = new File(cloned_directory + "/penguin.sh"); 
			   if(! config_file.isFile() ) {
				   return new String[] {"Error" , "penguin.sh not included in git repository"};
			   }
	           
			  
			       
			   try (    BufferedReader br = new BufferedReader(new FileReader(config_file));) {
				   String line;
				   String device_id = null;
				   String owner_git_id = null;
				   String device_name = null;
				   String distribution = null;
				   String linux_version = null;
				   int code_version = 0;
				   
				   while ((line = br.readLine()) != null)
					   if (line.contains("device_id")) {
							line = line.replace("device_id", "").trim();
							device_id = line;
						} else if (line.contains("owner_git_id")) {
							line = line.replace("owner_git_id", "").trim();
							owner_git_id = line;
						}  else if (line.contains("comfigureme_name")) {
							line = line.replace("configureme_name", "").trim();
							device_name = line;
						} else if (line.contains("comfigureme_distributon")) {
							line = line.replace("configureme_distribution", "").trim();
							distribution = line;
						}else if (line.contains("comfigureme_linux_version")) {
							line = line.replace("configureme_linux_version", "").trim();
							linux_version = line;
						}
			   
			   
					  
				   if (device_id == null) {
					   return new String[] {"Error", "device_id not set in configuration file penguin.sh"};
					   
				   }
				   if (owner_git_id == null) {
					   return new String[] {"Error", "owner_git_id not set in configuration file penguin.sh"};
					   
				   }
				   if (device_name == null) {
					   return new String[] {"Error", "configureme_name not set in configuration file penguin.sh"};
					   
				   }
				   if (distribution == null) {
					   return new String[] {"Error", "configureme_distribution not set in configuration file penguin.sh"};
					   
				   }if (linux_version == null) {
					   return new String[] {"Error", "configureme_linux_version not set in configuration file penguin.sh"};
					   
				   }
				  
			       
			       return new String[] {"Success", device_id,  git_id, Integer.toString(version), device_name, commit_id, "Repository names: " + String.join(" ", remote_names) + " Commit message: "  };
			   }
			   
			   //Assume failed
			   
				 }catch (RefNotFoundException ex) { 
			   return new String[] {"Error", "Cannot find commit"}; 
			}
		 catch (Exception ex) {ex.printStackTrace(out); return new String[] {"Error", "unspecified error"}; }
			   
		 }
}
