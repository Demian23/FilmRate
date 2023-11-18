<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>FilmRate</title>
</head>
<body>
<h1 style="color: red">You have ban!</h1>
<h2>From ${requestScope.get("banned").startPeriod} to ${requestScope.get("banned").endPeriod}</h2>
<h3>by <i>${requestScope.get("banned").adminBannedName}</i></h3>
</body>
</html>
