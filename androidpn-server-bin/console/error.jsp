<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>Internal Server Error</title>
</head>
<body id="error">
	<div>
	<h1>An error occurred</h1>
	<% if (exception != null) { %>
	<pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre>
	<% } else if ((Exception)request.getAttribute("javax.servlet.error.exception") != null) { %>
	<pre><% ((Exception)request.getAttribute("javax.servlet.error.exception")).printStackTrace(new java.io.PrintWriter(out)); %></pre>
	<% } %>
	</div>
</body>
</html>
