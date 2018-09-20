
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
private float min_kernel_verison;
private float max_kernel_version;
private int upvotes;


private String owner_git_id;
private String name;
private String email;
private String distribution;
public String getDistribution() {
	return distribution;
}
public void setDistribution(String distribution) {
	this.distribution = distribution;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getOwner_git_id() {
	return owner_git_id;
}
public void setOwner_git_id(String owner_git_id) {
	this.owner_git_id = owner_git_id;
}
public int getUpvotes() {
	return upvotes;
}
public void setUpvotes(int upvotes) {
	this.upvotes = upvotes;
}
public int getDownvotes() {
	return downvotes;
}
public void setDownvotes(int downvotes) {
	this.downvotes = downvotes;
}
int downvotes;
}
