<%--
  Created by IntelliJ IDEA.
  User: egor
  Date: 22.10.23
  Time: 20:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>FilmRate/Authorisation</title>
</head>
<body>

<form action="${pageContext.request.contextPath}/controller" method="post">
    <input type="hidden" name="command" value="Registration"/>
    <label>
        Username:
        <input type="text" name="user-name" value=""/>
    </label>
    <label>
        Password:
        <input type="password" name="user-password" value=""/>
    </label>
    <button type="submit">Submit</button>
</form>
</body>
</html>
