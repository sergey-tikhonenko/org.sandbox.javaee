<%@ page isErrorPage="true"%>
<%@ page import="java.util.Date"%>
<%
    final String requestURL = request.getRequestURL().toString();
    final String requestURI = request.getRequestURI();
    final int index = requestURL.indexOf(requestURI);
    final String retryURL = requestURL.substring(0, index) +
    request.getAttribute("javax.servlet.error.request_uri");
%>
<html>
<body>
	<H1>Error Page</H1>
	A program error occurred on
	<%=new Date()%>
	at
	<%=request.getServerName()%>.
	<p />
	<strong>javax.servlet.error.request_uri: </strong>
	<%=request.getAttribute("javax.servlet.error.request_uri")%>
	<br>
	<strong>javax.servlet.error.exception: </strong>
	<%=request.getAttribute("javax.servlet.error.exception")%>
	<br>
	<strong>javax.servlet.error.status_code: </strong>
	<%=request.getAttribute("javax.servlet.error.status_code")%>
	<br>
	<strong>javax.servlet.error.servlet_name: </strong>
	<%=request.getAttribute("javax.servlet.error.servlet_name")%>
	<br>

	<!-- Error info:
-->
  <%exception.printStackTrace();%>

	<a href="<%=retryURL%>">Click here to retry this operation.</a>
</BODY>

</HTML>