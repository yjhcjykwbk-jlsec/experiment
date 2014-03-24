<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>android推送管理</title>
<meta name="menu" content="pushstatics" />
<link rel="stylesheet" type="text/css" href="<c:url value='/styles/tablesorter/style.css'/>" />
<script type="text/javascript" src="<c:url value='/scripts/jquery.tablesorter.js'/>"></script>

<% 
String Login = (String)session.getAttribute("Login");

if (Login != null && Login.equals("OK")) { 
} 
else { 
out.println("请先登录，谢谢") ;
response.sendRedirect("index.jsp"); 
}
%>

</head>

<body>

<h1>推送详细情况</h1>

<table id="tableList" class="tablesorter" cellspacing="1">
	<thead>
		<tr>
			<th align="center">id</th>
			<th align="center">message id</th>
			<th align="center">消息日期</th>
			<th align="center">推送消息</th>
			<th align="center">推送目标/人</th>
			<th align="center">未发送/人</th>
			<th align="center">已发送/人</th>
			<th align="center">已接收/人</th>
			<th align="center">已查看/人</th>			
		</tr>
	</thead>
	<tbody>
		<c:forEach var="pushstatics" items="${pushstaticsList}">
			<tr>
				<td align="center">
					<c:out value="${pushstatics.staticsId}" />
				</td>
				<td align="center">
					<c:out value="${pushstatics.messageId}" />
				</td>
				<td align="center">
					<c:out value="${pushstatics.messageCreateDate}" />
				</td>
				<td align="center">
					<c:out value="${pushstatics.messageTitle}" />
				</td>
				<td align="center">
					<c:out value="${pushstatics.count_target}" />
				</td>
				<td align="center">
					<c:out value="${pushstatics.count_notsend}" />
				</td>
				<td align="center">
					<c:out value="${pushstatics.count_send}" />
				</td>
				<td align="center">
					<c:out value="${pushstatics.count_receive}" />
				</td>
				<td align="center">
					<c:out value="${pushstatics.count_view}" />
				</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>  
        <tr align="center">  
            <td colspan="5">  
                <jsp:include page="paging_footer.jsp"></jsp:include>  
            </td>  
        </tr>  
    </tfoot>
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