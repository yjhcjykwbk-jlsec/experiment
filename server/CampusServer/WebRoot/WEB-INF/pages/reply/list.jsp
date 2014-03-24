<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>android推送管理</title>
<meta name="menu" content="reply" />
<link rel="stylesheet" type="text/css" href="<c:url value='/styles/tablesorter/style.css'/>" />
<script type="text/javascript" src="<c:url value='/scripts/jquery.tablesorter.js'/>"></script>
</head>

<body>

<% 
String Login = (String)session.getAttribute("Login");

if (Login != null && Login.equals("OK")) { 
} 
else { 
out.println("请先登录，谢谢") ;
response.sendRedirect("index.jsp"); 
}
%>

<h1>用户留言</h1>

<table id="tableList" class="tablesorter" cellspacing="1">
	<thead>
		<tr>
			<th>用户名</th>
			<th>留言</th>			
		</tr>
	</thead>
	<tbody>
		<c:forEach var="reply" items="${replyList}">
			<tr>
				<td><c:out value="${reply.userName}" /></td>
				<td><c:out value="${reply.replyMessage}" /></td>
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
