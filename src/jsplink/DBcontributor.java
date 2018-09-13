package jsplink;

public class DBcontributor {
private String  email;
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public String getOwner_git_id() {
	return owner_git_id;
}
public void setOwner_git_id(String owner_git_id) {
	this.owner_git_id = owner_git_id;
}
public String getCookie() {
	return cookie;
}
public void setCookie(String cookie) {
	this.cookie = cookie;
}
public String getGit_token() {
	return git_token;
}
public void setGit_token(String git_token) {
	this.git_token = git_token;
}
private String  url;
private String description;
private String owner_git_id;
private String cookie;
private String  git_token;
private String location;
private String name;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getLocation() {
	return location;
}
public void setLocation(String location) {
	this.location = location;
}
}
