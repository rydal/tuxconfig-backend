
public class DBDevice {
private String device_id;
public String getDevice_id() {
	return device_id;
}
public void setDevice_id(String device_id) {
	this.device_id = device_id;
}
public String getGit_url() {
	return git_url;
}
public void setGit_url(String git_url) {
	this.git_url = git_url;
}
public String getCommit_hash() {
	return commit_hash;
}
public void setCommit_hash(String commit_hash) {
	this.commit_hash = commit_hash;
}
public int getAuthorised() {
	return authorised;
}
public void setAuthorised(int authorised) {
	this.authorised = authorised;
}
public float getMin_kernel_verison() {
	return min_kernel_verison;
}
public void setMin_kernel_verison(float min_kernel_verison) {
	this.min_kernel_verison = min_kernel_verison;
}
public float getMax_kernel_version() {
	return max_kernel_version;
}
public void setMax_kernel_version(float max_kernel_version) {
	this.max_kernel_version = max_kernel_version;
}
private String git_url;
private String commit_hash;
private int authorised;
float min_kernel_verison;
float max_kernel_version;
}
