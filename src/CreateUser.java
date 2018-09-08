
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
		
		response.setContentType("application/json");
		try {
		float min_kernel_version = 0;
		float max_kernel_version = 0;
		
			String git_id = request.getParameter("git_id");
			String git_email =  request.getParameter("git_email");
			String webpage = request.getParameter("url");
			String description = request.getParameter("desctiption");
			String git_token = request.getParameter("git_token");
			min_kernel_version = Float.parseFloat(request.getParameter("min_kernel_version"));
			max_kernel_version = Float.parseFloat(request.getParameter("max_kernel_version"));

			String device_id = request.getParameter("device_id");
			String git_url = request.getParameter("git_url");
			String commit_hash = request.getParameter("commit_hash");
			
			

			if (git_id == null || git_email == null || webpage == null || description == null || git_token == null || 
					min_kernel_version == 0 || max_kernel_version == 0 || device_id == null || git_url == null || commit_hash == null) {
				JSONObject json2 = new JSONObject();
				json2.put("Error", "Values not recieved");
				// Assuming your json object is **jsonObject**, perform the following, it will
				// return your json object
				out.print(json2);
			}
			run.update("replace into contributor (url,description,owner_git_id, email,git_token) values (?,?,?,?)",webpage,description,git_id,git_email,git_token);
					java.util.Date dt = new java.util.Date();

			java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String currentTime = sdf.format(dt);
					
			run.update("replace into devices (device_id,git_url) values (?,?,?)",device_id,git_url);
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
		doGet(request, response);
	}

	public String[] getCommits(String url, String git_id, String name, String device_id, PrintWriter out) {
		try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");

		) {

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

			File config_file = new File(cloned_directory + "/penguin.sh");
			if (!config_file.isFile()) {
				return new String[] { "Error", "penguin.sh not included in git repository" };
			}

			Set<String> remote_names = repo.getRemoteNames();

			repo.close();
			cloned_git.close();

			// get commit message

			try (BufferedReader br = new BufferedReader(new FileReader(config_file));) {
				String line;
				String tuxconfig_device_id = null;
				String owner_git_id = null;
				String tuxconfig_device_name = null;
				String distribution = null;
				String tuxconfig_reboot = null;
				String tuxconfig_test_packages = null;
				String tuxconfig_test_command = null;
				String tuxconfig_test_message = null;
				
				

				while ((line = br.readLine()) != null) {
					if (line.contains("device_id")) {
						line = line.replace("device_id", "").trim();
						tuxconfig_device_id = line.toLowerCase();
					} else if (line.contains("owner_git_id")) {
						line = line.replace("owner_git_id", "").trim();
						owner_git_id = line;
					} else if (line.contains("tuxconfig_name")) {
						line = line.replace("tuxconfig_name", "").trim();
						tuxconfig_device_name = line;
					}
					else if (line.contains("tuxconfig_reboot")) {
						line = line.replace("tuxconfig_reboot", "").trim();
						tuxconfig_reboot = line;
					}
					else if (line.contains("tuxconfig_test_packages")) {
						line = line.replace("tuxconfig_test_packages", "").trim();
						tuxconfig_test_packages = line;
					}
					else if (line.contains("tuxconfig_test_command")) {
						line = line.replace("tuxconfig_test_command", "").trim();
						tuxconfig_test_command = line;
					}
					else if (line.contains("tuxconfig_test_message")) {
						line = line.replace("tuxconfig_test_message", "").trim();
						tuxconfig_test_message = line;
					}
				}

				if (tuxconfig_device_id == null) {
					return new String[] { "Error", "device_id not set in configuration file penguin.sh" };

				}
				if (owner_git_id == null) {
					return new String[] { "Error", "owner_git_id not set in configuration file penguin.sh" };

				}
				if (tuxconfig_device_name == null) {
					return new String[] { "Error", "tuxconfig_name not set in configuration file penguin.sh" };

				}
				if (tuxconfig_reboot == null) {
					return new String[] { "Error", "tuxconfig_reboot not set in configuration file penguin.sh" };

				}
								
				
				if (tuxconfig_test_packages == null) {
					return new String[] { "Error", "tuxconfig_test_packages not set in configuration file penguin.sh" };

				}
				if (tuxconfig_test_command== null) {
					return new String[] { "Error", "tuxconfig_test_command not set in configuration file penguin.sh" };

				}
				if (tuxconfig_test_message == null) {
					return new String[] { "Error", "tuxconfig_test_message not set in configuration file penguin.sh" };

				}
								

				if (!tuxconfig_device_id.equalsIgnoreCase(device_id.toLowerCase())) {
					return new String[] { "Error", "Submitted device id does not equal device id on penguin.sh file" };
				}
				
				if (!name.equalsIgnoreCase(tuxconfig_device_name)) {
					return new String[] { "Error", "Submitted name does not equal name on penguin.sh file" };
				}

				return new String[] { "Success", commit_hash, "Repository names: " + String.join(" ", remote_names) };
			}

			// Assume failed

		} catch (RefNotFoundException ex) {
			return new String[] { "Error", "Cannot find commit" };
		} catch (Exception ex) {
			ex.printStackTrace(out);
			return new String[] { "Error", "unspecified error" };
		}

	}

}
