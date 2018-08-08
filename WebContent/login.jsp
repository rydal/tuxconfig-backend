		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta charset="utf-8">
<body>
<div style="text-align:center" id="portrait" >

<form action="https://linuxconf.feedthepenguin.org/hehe/dologin" method="post" id="form1" >

<a href="https://linuxconf.feedthepenguin.org/hehe/index.jsp"><img src="./img/linuxconf.png" class="center" ></a><h1>Login	</h1><div id="cookie_magic" style = "max-height: 1px;"></div><div id="server_response" style = "max-height: 1px;"></div>
<%
if ((String) session.getAttribute("flag") != null) {
	out.write((String) session.getAttribute("flag")); session.setAttribute("flag",null);
}
%><h2>Email:<br><input type=text id="email" name="email" onfocusout="ValidateEmail()" ><br>
Password:</h2><br>

<input type="password" id="password" name="password" /><br>
<img  src="./img/login.png" name="loginimage" id="login" onclick="send_post()"	class="center"><br>
Forgot Password:<br>
<img src="./img/forgot_password.png" onclick="location.href='https://linuxconf.feedthepenguin.org/hehe/forgot_password.html'"><br>
<input type="image" src="./img/register.png" name="register_image" class="center"  onclick="location.href='https://mycode.feedthepenguin.org/mycard/Register.jsp'">
</form>
</div>
</body>

</html>