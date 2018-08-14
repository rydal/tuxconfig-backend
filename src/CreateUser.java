

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
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
import java.util.jar.Attributes.Name;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
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
				String url = request.getParameter("git_url" + i) ;
				String name = request.getParameter("device_name" + i) ;
				String device_id = request.getParameter("device_id" + i) ;
				
				
				String[] is_valid = getCommits(url, git_id, name, device_id, out);
				
				if( is_valid[0].startsWith("Error")) {
					JSONObject json2 = new JSONObject();
					json2.put("Error ",  url + " Reason: " + is_valid[1]);
					out.println(json2);
					
				} else if (is_valid[0].equals("Success")){
				PreparedStatement check_commit = con.prepareStatement("select * from devices where device_id = ?  and owner_git_id = ? and commit_hash = ?");
				check_commit.setObject(1, request.getParameter("device_id" + i));
				check_commit.setObject(2, request.getParameter(git_id));
				check_commit.setObject(3, is_valid[1]);
				ResultSet checked_commit = check_commit.executeQuery();
				if (! checked_commit.next()) {
				PreparedStatement stmt = con.prepareStatement("replace into devices (device_id,name,owner_git_id, contributor_email,git_url, version, commit_hash) values (?,?,?,?,?,?,?)");
			    stmt.setObject(1, request.getParameter("device_id" + i));
			    stmt.setObject(2, request.getParameter("device_name" + i));
			    stmt.setObject(3, git_id);
			    stmt.setObject(4, git_email);
			    stmt.setObject(5, request.getParameter("git_url" + i));
			    stmt.setObject(6, is_valid[1]);
			    stmt.setObject(7, is_valid[2]);
			    
			    stmt.executeUpdate();
			    
				JSONObject json2 = new JSONObject();
				json2.put("Success ",  url + " " + is_valid[2]);
				out.println(json2);
				}
			    
				
				}
				i++;
			}
			response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/CreateUser.jsp");
		    
		}catch (Exception ex) { ex.printStackTrace(out);}
		

	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse resp	onse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	 public String[] getCommits(String url,String git_id, String name, String device_id, PrintWriter out) {
		 try (Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost/linuxconf","arwen","imleaving");  

			){
			 
			 String generatedString = RandomStringUtils.random( 20, true, true);
				String escaped_url = url.replaceAll("/", "%2F");

				 String cloned_directory;
				 Path linuxconf_path ;
				 int version = 0;
				 do {
					 cloned_directory = "/tmp/linuxconf/" + escaped_url + ":" + git_id + ":" + version;
					  linuxconf_path = FileSystems.getDefault().getPath(cloned_directory);
					  version++;
					     
				 } while (Files.exists(linuxconf_path) );

				  cloned_directory = "/tmp/linuxconf/" + escaped_url + ":" + git_id + ":" + generatedString;
				 
				 FileUtils.deleteDirectory(new File(cloned_directory));
				    Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwx---");
				  
			       FileAttribute<Set<PosixFilePermission>> fileAttributes
	                = PosixFilePermissions.asFileAttribute(permissions);
				 Files.createDirectory(linuxconf_path, fileAttributes ) ;
				 File directory = new File(cloned_directory);
				 
				 new File(cloned_directory).mkdir();
				   Git cloned_git = Git.cloneRepository()
				  .setURI(url)
				  .setDirectory(directory)
				  .call();
				   
				  
				   String commit_hash = null;
				   
				   Repository repo = cloned_git.getRepository();
				   
				   ObjectId id = repo.resolve(Constants.HEAD);
				    commit_hash = id.getName();
			
				    
				    
				   File config_file = new File(cloned_directory + "/penguin.sh"); 
				   if(! config_file.isFile() ) {
					   return new String[] {"Error" , "penguin.sh not included in git repository"};
				   }
			  
			   
		       Set<String> remote_names = repo.getRemoteNames();
		       
		       repo.close();
		       cloned_git.close();
				
		       
		       //get commit message
			   
			   
	           
			  
			       
			   try (    BufferedReader br = new BufferedReader(new FileReader(config_file));) {
				   String line;
				   String read_device_id = null;
				   String owner_git_id = null;
				   String read_device_name = null;
				   String distribution = null;
				   String linux_version = null;
				   
				   while ((line = br.readLine()) != null)
					   if (line.contains("device_id")) {
							line = line.replace("device_id", "").trim();
							read_device_id = line;
						} else if (line.contains("owner_git_id")) {
							line = line.replace("owner_git_id", "").trim();
							owner_git_id = line;
						}  else if (line.contains("configureme_name")) {
							line = line.replace("configureme_name", "").trim();
							read_device_name = line;
						} 
			   
			   
				   
					  
				   if (read_device_id == null) {
					   return new String[] {"Error", "device_id not set in configuration file penguin.sh"};
					   
				   }
				   if (owner_git_id == null) {
					   return new String[] {"Error", "owner_git_id not set in configuration file penguin.sh"};
					   
				   }
				   if (read_device_name == null) {
					   return new String[] {"Error", "configureme_name not set in configuration file penguin.sh"};
					   
				   }
				
				   if (! read_device_id.equalsIgnoreCase(device_id.toLowerCase())) {
					   return new String[] {"Error", "Submitted device id does not equal device id on penguin.sh file"};
				   }
				   if (! name.equalsIgnoreCase(read_device_name)) {
					   return new String[] {"Error", "Submitted name does not equal name on penguin.sh file"};
				   }
				  
			       
			       return new String[] {"Success",  commit_hash, "Repository names: " + String.join(" ", remote_names) };
			   }
			   
			   //Assume failed
			   
			   
				 }catch (RefNotFoundException ex) { 
			   return new String[] {"Error", "Cannot find commit"}; 
			}
		 catch (Exception ex) {ex.printStackTrace(out);  return new String[] {"Error", "unspecified error"}; }
			  
		 }
	 
}
