<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title><decorator:title/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link rel="stylesheet" type="text/css" href="<c:url value='/styles/console.css'/>" />
	<script type="text/javascript" src="<c:url value='/scripts/jquery.js'/>"></script>
	<script type="text/javascript"> 
		function checkOK(){
			$('#content').show();
			$('#loginform').hide();
		}
		function checkFailed(){
			$('#content').hide();
			$('#loginform').show();
		}
		function logout(){
			//session.invalidate();
			//response.sendRedirect("index.jsp");
		}
	</script>
	<decorator:head/>
</head>
<c:set var="topMenu" scope="request"><decorator:getProperty property="meta.menu"/></c:set>			
<c:set var="subMenu" scope="request"><decorator:getProperty property="meta.submenu"/></c:set>			
<body<decorator:getProperty property="body.id" writeEntireProperty="true"/><decorator:getProperty property="body.onload" writeEntireProperty="true"/>>
     
<div id="page">
    <div id="header">
        <jsp:include page="/includes/header.jsp"/>
    </div>
        

        
<div style="text-align:center">
<form id="loginform" action="adminlogin.do?action=login" method="post" style="text-align:center">
<table width="500" cellpadding="4" cellspacing="0" border="0">
<tr id="adminName">
	<td>用户名:</td>
	<td><input type="text" id="adminname" name="adminname" value="" style="width:380px;" /></td>
</tr>
<tr>
	<td>密码:</td>
	<td><input type="password" id="adminpwd" name="adminpwd" value="" style="width:380px;" /></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td><input type="submit" value="登陆" /></td>
</tr>
</table> 
</form>
</div>
<%
String Login = (String)session.getAttribute("Login");
if (Login != null && Login.equals("OK")) { 
//out.println("欢迎回来,Administrator");
%>
<script  type="text/javascript">
 checkOK();
 </script>
 <%
} 
else { 
out.println("请先登录，谢谢") ;
%>
<script  type="text/javascript">
checkFailed();
</script>
<%
}
%>

        <div id="content">
			<ul id="tabmenu">
				<li><a href="index.do"
					class="<c:if test="${topMenu eq 'home'}"><c:out value="current" /></c:if>">首页</a></li>
				<li><a href="user.do"
					class="<c:if test="${topMenu eq 'user'}"><c:out value="current" /></c:if>">所有用户</a></li>
				<li><a href="session.do"
					class="<c:if test="${topMenu eq 'session'}"><c:out value="current" /></c:if>">在线用户</a></li>
				<li><a href="notification.do"
					class="<c:if test="${topMenu eq 'notification'}"><c:out value="current" /></c:if>">推送消息</a></li>
				<li><a href="pushstatics.do"
					class="<c:if test="${topMenu eq 'pushstatics'}"><c:out value="current" /></c:if>">推送情况统计</a></li>
				<li><a href="subscriptions.do"
					class="<c:if test="${topManu eq 'subscriptions'}"><c:out value="current" /></c:if>">订阅情况</a></li>
				<li><a href="reply.do"
					class="<c:if test="${topManu eq 'reply'}"><c:out value="current" /></c:if>">用户留言区</a></li>
				<li><a href="upload.do"
					class="<c:if test="${topManu eq 'upload'}"><c:out value="current" /></c:if>">用户上传</a></li>
			</ul>
			<div id="tabcontent">
				<decorator:body/>
			</div>
        </div>
        <div id="footer">
            <jsp:include page="/includes/footer.jsp"/>
        </div>
    </div>
</body>
</html>
