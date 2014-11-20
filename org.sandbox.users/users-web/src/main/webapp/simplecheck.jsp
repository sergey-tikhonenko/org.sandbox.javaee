<%@ page errorPage="ShowError.jsp" %>

<html>
<head>
   <title>Simple check</title>
</head>
<body>
<table>
<tr><td>User:</td><td><%= request.getUserPrincipal().getName() %></td></tr>
<tr><td>Is Secure:</td><td><%= request.isSecure() %></td></tr>
<tr><td>RemoteUser:</td><td><%= request.getRemoteUser() %></td></tr>
<tr><td>AuthType:</td><td><%= request.getAuthType() %></td></tr>
<tr><td>Is UserViewer:</td><td><%= request.isUserInRole("UserViewer") %></td></tr>
<tr><td>Is UserEditor:</td><td><%= request.isUserInRole("UserEditor") %></td></tr>
<tr><td>Is ssouser:</td><td><%= request.isUserInRole("ssouser") %></td></tr>
</table>
</body>
</html>