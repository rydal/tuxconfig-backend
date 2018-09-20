<!DOCTYPE html>
<html>
<head><meta charset="utf-8">
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="./css/styles.css"><meta name="viewport" content="width=device-width, initial-scale=1">
<script src="./js/resendemail.js"></script>
<title>linuxconf</title>
</head>
<body>
<br>
<div style="text-align:center" id="portrait" >
<h2>
<%

if ((String) session.getAttribute("flag") != null) {
	out.write((String) session.getAttribute("flag")); session.setAttribute("flag",null);
	out.write("<br>");
}
%>

Email:
</h2>
<br>
<input type=text id="email"  size="35">
<br>
<img  src="./img/submit.jpg" class="center" onclick="send_post()">

<div id="server_response"> </div>
</div>
</body>
</html>