package jsplink;

public class DBSuccess {
	public String getSuccess_code() {
		return success_code;
	}
	public void setSuccess_code(String success_code) {
		this.success_code = success_code;
	}
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
	private String success_code;
	private String device_id;
	private String git_url;
	
}
