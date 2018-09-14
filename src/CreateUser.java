
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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
    private HashSet<String> devices_hashset = new HashSet<String>(); 


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
		
			String  git_id = (String) session.getAttribute("git_id");
			
			
			String website = request.getParameter("website");

			String  git_token = (String) session.getAttribute("git_token");
			
			String git_url = request.getParameter("git_url");

			min_kernel_version = Float.parseFloat(request.getParameter("min_kernel_version"));
			max_kernel_version = Float.parseFloat(request.getParameter("max_kernel_version"));
			min_distribution_version = Float.parseFloat(request.getParameter("min_distribution_version"));
			max_distribution_version = Float.parseFloat(request.getParameter("max_distribution_version"));
			String distribution = request.getParameter("distribution");
						
			JSONObject json3 = new JSONObject();
			
			if (git_id == null) json3.put("Error", "Git owner id not stored in session");
			
			
			
			if (website == null) json3.put("Error", "Webpage not stored correctly");
			if (git_token == null) json3.put("Error", "Git Token not stored correctly");
			
			if (distribution == null) json3.put("Error", "Distribution not stored correctly");
			if (git_url == null) json3.put("Error", "Git urlnot stored correctly");
			if (min_kernel_version == 0) json3.put("Error", "min kernel version not stored correctly");
			if (max_kernel_version == 0) json3.put("Error", "max kernel version not stored correctly");
			if (min_distribution_version == 0) json3.put("Error", "min distribution version not stored correctly");
			if (max_distribution_version == 0) json3.put("Error", "max distribution version not stored correctly");
			if (distribution == null) json3.put("Error", "git token not stored correctly");
			
			if (json3.length() != 0) {
				out.print(json3);
				return;
			}

			
			run.update("update contributor set website = ? where git_id = ?",website,git_id);
			
			
			String message = getCommits(git_url, git_id,out);
			if (message.startsWith("Error")) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "Could not import " + git_url  + " message:\n " + message);
				// Assuming your json object is **jsonObject**, perform the following, it will
				// return your json object
				out.print(json2);
				return;
			} else {
			
					java.util.Date dt = new java.util.Date();

			java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			
			String currentTime = sdf.format(dt);

			

		
			String commit_hash = message;
		
			
			run.update("replace into git_url (owner_git_id,git_url,commit_hash, commit_date,min_kernel_version,max_kernel_version,min_distribution_version,max_distribution_version,distribution)"
					+ " values (?,?,?,?,?,?,?,?,?)",git_id,git_url,commit_hash,currentTime,min_kernel_version, max_kernel_version,min_distribution_version,max_distribution_version,distribution);
			}
			JSONObject json2 = new JSONObject();
			json2.put("Form", "Data Accepted");
				// Assuming your json object is **jsonObject**, perform the following, it will
			// return your json object
			out.print(json2);
			return;
		
			
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

	public String getCommits(String url, String git_id,PrintWriter out) {
		DataSource dataSource = CustomDataSource.getInstance();
		QueryRunner run = new QueryRunner(dataSource);
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

			
			Repository repo = cloned_git.getRepository();

			ObjectId id = repo.resolve(Constants.HEAD);
			String message = null;

			String commit_hash = id.getName();

			File config_file = new File(cloned_directory + "/tuxconfig");
			if (!config_file.isFile()) {
				 message += "Error:  tuxconfig not included in git repository\n" ;
			}

			Set<String> remote_names = repo.getRemoteNames();

			repo.close();
			cloned_git.close();

			// get commit message

				String line;
				String tuxconfig_device_ids = null;
				String tuxconfig_module = null;
				String tuxconfig_depenedencies = null;
				String test_program = null;
				String test_message = null;
				BufferedReader br = new BufferedReader(new FileReader(config_file));

				

				while ((line = br.readLine()) != null) {
					if (line.contains("device_id")) {
						line = line.replace("device_ids=", "").trim();
						tuxconfig_device_ids = line.toLowerCase();
						tuxconfig_device_ids = tuxconfig_device_ids.replaceAll("\"", "");
						
						
					} else if (line.contains("tuxconfig_module")) {
						line = line.replace("tuxconfig_module\\s*=", "").trim();
						tuxconfig_module = line.toLowerCase();
						tuxconfig_module = tuxconfig_module.replaceAll("\"", "");

					}
					else if (line.contains("tuxconfig_depenedencies")) {
						line = line.replace("tuxconfig_depenedencies\\s=", "").trim();
						tuxconfig_depenedencies = line.toLowerCase();
					}
					else if (line.contains("test_program")) {
						line = line.replace("test_program\\s=", "").trim();
						test_program = line.toLowerCase();
					}
					else if (line.contains("test_message")) {
						line = line.replace("test_message\\s=", "").trim();
						test_message = line.toLowerCase();
					}
				}
					
					
				if (tuxconfig_device_ids == null) {
					message += "Error: tuxconfig_device_ids not set in configuration file\n";

				}
				if (tuxconfig_module == null) {
					message += "Error: tuxconfig_module not set in configuration file\n";

				}
				if (tuxconfig_depenedencies == null) {
					message += "Error: tuxconfig_dependencies not set in configuration file\n";

				}
				if (test_program == null) {
					message += "Error: test_program not set in configuration file\n";
				}
				if (test_message == null) {

					message += "Error: test_message not set in configuration file\n";
				}
				
				out.write(tuxconfig_device_ids);
				HashSet<String> myHashSet = new HashSet();  // Or a more realistic size

				StringTokenizer st = new StringTokenizer(tuxconfig_device_ids, " ");
				if (st.hasMoreTokens() == false) {
					myHashSet.add(tuxconfig_device_ids);
				} else {
					while(st.hasMoreTokens())
				   myHashSet.add(st.nextToken());
				
					
				}

				Iterator<String> it = myHashSet.iterator();
			     while(it.hasNext()){
			        String[]  each_side = it.next().split(":");
					  if (each_side.length != 2) {
						  message += "Error parsing device id: " + it.toString() + "\n";
						break;
					  }
					  if (each_side[0].length() < 4) {
						  each_side[0] = "0" + each_side[0];
					  }
					  if (each_side[1].length() < 4) {
						  each_side[1] = each_side[0] + "0";
					  }
						run.update("replace into devices (device_id,git_url) values (?,?)",each_side[0] + ":" + each_side[1],url);
						run.update("update git_url set module = ? where git_url = ?", tuxconfig_module, url);
								  }
		br.close();
		
		if (message.startsWith("Error")) {
			return message;
		} else {
			return commit_hash;
		}
			
			// Assume failed

		} catch (RefNotFoundException ex) {
			ex.printStackTrace(out);
			return  "Error: cannot find commit" ;
		} catch (Exception ex) {
			ex.printStackTrace(out);
			return "Error: unspecified error" ;
		}
	}
}


