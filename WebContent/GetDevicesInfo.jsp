<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="jsplink.*"%>


<%@ page import="org.apache.commons.dbutils.QueryRunner"%>
<%@ page import="org.apache.commons.dbutils.ResultSetHandler"%>
<%@ page import="org.apache.commons.dbutils.handlers.BeanHandler"%>
<%@ page import="org.apache.commons.dbutils.handlers.BeanListHandler"%>
<%@ page import="javax.sql.DataSource"%>
<%@ taglib prefix="f"  uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h"  uri="http://java.sun.com/jsf/html"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<b>Device details listing:</b>
<%
DataSource dataSource = CustomDataSource.getInstance();
QueryRunner run = new QueryRunner(dataSource);
ResultSetHandler<DBcontributor> contributor_results = new BeanHandler<DBcontributor>(DBcontributor.class);
ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);

String sql = "select device_id, descripion, git_url, module from devices d inner join git_url g on d.devices_condtraint = g.git_url_constraint";
ResultSetHandler<List<DBDevice>> rsh = new BeanListHandler<DBDevice>(DBDevice.class);
List<DBDevice> rows = run.query(sql, rsh);

Iterator<DBDevice> it = rows.iterator();

while (it.hasNext()) {
	out.println("<hr>");
	DBDevice bean = it.next();
	out.println("Device id: " + bean.getDevice_id() + "<br>");
	out.println("Descritpion: " + bean.getDescription() + "<br>");
	out.println("Git Url:" + bean.getGit_url() + "<br>");
	out.println("Module:" + bean.getModule() + "<br>");
}


%>
</body>
</html>