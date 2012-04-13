<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Admin Console</title>
<meta name="menu" content="session" />
<link rel="stylesheet" type="text/css" href="<c:url value='/styles/tablesorter/style.css'/>" />
<script type="text/javascript" src="<c:url value='/scripts/jquery.tablesorter.js'/>"></script>
</head>

<body>

<h1>Sessions</h1>

<table id="tableList" class="tablesorter" cellspacing="1">
	<thead>
		<tr>
			<%--
			<th width="30%">Username</th>
			<th width="10%">Resource</th>
			<th width="10%">Status</th>
			<th width="10%">Presence</th>
			<th width="15%">Client IP</th>
			<th width="25%">Created</th>
			--%>
			<th>Username</th>
			<th>Resource</th>
			<th>Status</th>
			<th>Presence</th>
			<th>Client IP</th>
			<th>Created</th>			
		</tr>
	</thead>
	<tbody>
		<c:forEach var="sess" items="${sessionList}">
			<tr>
				<td><c:out value="${sess.username}" /></td>
				<td><c:out value="${sess.resource}" /></td>
				<td align="center"><c:out value="${sess.status}" /></td>
				<td>
					<c:choose>
					<c:when test="${sess.presence eq 'Online'}">
						<img src="images/user-online.png" />
					</c:when>
					<c:when test="${sess.presence eq 'Offline'}">
						<img src="images/user-offline.png" />
					</c:when>
					<c:otherwise>
						<img src="images/user-away.png" />
					</c:otherwise>
					</c:choose>
					<c:out value="${sess.presence}" />
				</td>
				<td><c:out value="${sess.clientIP}" /></td>
				<td align="center"><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${sess.createdDate}" /></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<script type="text/javascript">
//<![CDATA[
$(function() {
	$('#tableList').tablesorter();
	//$('#tableList').tablesorter( {sortList: [[0,0], [1,0]]} );
	//$('table tr:nth-child(odd)').addClass('odd');
	$('table tr:nth-child(even)').addClass('even');	 
});
//]]>
</script>

</body>
</html>
