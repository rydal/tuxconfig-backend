
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
import java.util.HashSet;
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
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
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
	private HashSet<String> h;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateUser() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DataSource dataSource = CustomDataSource.getInstance();
		QueryRunner run = new QueryRunner(dataSource);
      ResultSetHandler<DBcontributor> contributor_results= new BeanHandler<DBcontributor>(DBcontributor.class);
      ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);
      
		
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		
		response.setContentType("application/json");
		try {
		float min_kernel_version = 0;
		float max_kernel_version = 0;
		float min_distribution_version  = 0;
		float max_distribution_version  = 0;
		
			String git_id = (String) session.getAttribute("git_id");
			String git_email = (String) session.getAttribute("git_email");
			String description = (String) session.getAttribute("description");
			
			String webpage = request.getParameter("url");
			String git_token = request.getParameter("git_token");
			String distribution = request.getParameter("distribution");
			
			min_kernel_version = Float.parseFloat(request.getParameter("min_kernel_version"));
			max_kernel_version = Float.parseFloat(request.getParameter("max_kernel_version"));
			min_distribution_version = Float.parseFloat(request.getParameter("min_distribution_version"));
			max_distribution_version = Float.parseFloat(request.getParameter("max_distribution_version"));

			
			String git_url = request.getParameter("git_url");
			String commit_hash = request.getParameter("commit_hash");
			
			String error_message = null;
			if(git_id == null ) error_message = "git id not entered";
			if(git_id == null ) error_message = "git id not entered";
			

			
			
			
			run.update("replace into contributor (url,description,owner_git_id, email,git_token) values (?,?,?,?)",webpage,description,git_id,git_email,git_token);
					java.util.Date dt = new java.util.Date();

			java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(dt);
					
			run.update("replace into devices (device_id,git_url) values (?,?,?)",git_url);
			run.update("replace into git_url (owner_git_id,git_url,commit_hash, commit_date,min_kernel_version,max_kernel_version)"
					+ " values (?,?,?,?,?,?)",git_id,git_url,commit_hash,currentTime,min_kernel_version, max_kernel_version);
			JSONObject json2 = new JSONObject();
			json2.put("Form", "Data accepted");
			// Assuming your json object is **jsonObject**, perform the following, it will
			// return your json object
			out.print(json2);
		} catch (Exception ex) { ex.printStackTrace(out); }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse resp
	 *      onse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public String getCommits(String url, String git_id) {
		try  {

			String generatedString = RandomStringUtils.random(20, true, true);
			String escaped_url = url.replaceAll("/", "%2F");

			String cloned_directory;
			Path linuxconf_path;
			int version = 0;
			do {
				cloned_directory = "/tmp/linuxconf/" + escaped_url + ":" + git_id + ":" + version;
				linuxconf_path = FileSystems.getDefault().getPath(cloned_directory);
				version++;

			} while (Files.exists(linuxconf_path));

			cloned_directory = "/tmp/linuxconf/" + escaped_url + ":" + git_id + ":" + generatedString;

			FileUtils.deleteDirectory(new File(cloned_directory));
			Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwx---");

			FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);
			Files.createDirectory(linuxconf_path, fileAttributes);
			File directory = new File(cloned_directory);

			new File(cloned_directory).mkdir();
			Git cloned_git = Git.cloneRepository().setURI(url).setDirectory(directory).call();

			String commit_hash = null;

			Repository repo = cloned_git.getRepository();

			ObjectId id = repo.resolve(Constants.HEAD);
			commit_hash = id.getName();

			String message;
			File config_file = new File(cloned_directory + "/tuxconfig");
			if (!config_file.isFile()) {
				 message = "Error:  tuxconfig not included in git repository" ;
			}

			Set<String> remote_names = repo.getRemoteNames();

			repo.close();
			cloned_git.close();

			// get commit message

			BufferedReader br = new BufferedReader(new FileReader(config_file));
				String line;
				String tuxconfig_device_ids = null;
				String tuxconfig_module = null;
				String tuxconfig_depenedencies = null;
				String test_program = null;
				String test_message = null;
				
				

				while ((line = br.readLine()) != null) {
					if (line.contains("device_id")) {
						line = line.replace("device_id", "").trim();
						tuxconfig_device_ids = line.toLowerCase();
					} else if (line.contains("tuxconfig_module")) {
						line = line.replace("tuxconfig_module", "").trim();
						tuxconfig_module = line.toLowerCase();
					}
					else if (line.contains("tuxconfig_depenedencies")) {
						line = line.replace("tuxconfig_depenedencies", "").trim();
						tuxconfig_depenedencies = line.toLowerCase();
					}
					else if (line.contains("test_program")) {
						line = line.replace("test_program", "").trim();
						test_program = line.toLowerCase();
					}
					else if (line.contains("test_message")) {
						line = line.replace("test_message", "").trim();
						test_message = line.toLowerCase();
					}
				}
					
					
				if (tuxconfig_device_ids == null) {
					message = "tuxconfig_device_ids not set in configuration file";

				}
				if (tuxconfig_module == null) {
					message = "tuxconfig_module not set in configuration file";

				}
				if (tuxconfig_depenedencies == null) {
					message = "tuxconfig_dependencies not set in configuration file";

				}
				if (test_program == null) {
					message = "test_program not set in configuration file";
				}
				if (test_message == null) {
					message = "test_message not set in configuration file";

				}
				
				String[] devices_array = tuxconfig_device_ids.split(" ");
		        HashSet<String> h = new HashSet<String>(); 

		        boolean correct = true;
				for(int i = 0; i < devices_array.length; i++) {
					  String[]  each_side = devices_array[i].split(":");
					  if (each_side.length != 2) {
						correct = false;
						
						break;
					  }
					  if (each_side[0].length() < 4) {
						  each_side[0] = "0" + each_side[0];
					  }
					  if (each_side[1].length() < 4) {
						  each_side[1] = each_side[0] + "0";
					  }
					  h.add(each_side[0] + ":" + each_side[1]);
					  
					
				
				
			}

			// Assume failed

		} catch (RefNotFoundException ex) {
			ex.printStackTrace();
			return  "Error cannot find commit" ;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Error unspecified error" ;
		}
		return null;
	}
}


