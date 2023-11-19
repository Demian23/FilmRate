<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>FilmRate/${sessionScope.get("currentUserName")}</title>
    <link href="css/admin_page.css" rel="stylesheet" />
</head>
<header>
    <%@include file="admin_header.jsp"%>
</header>
<body>
</body>
<%@include file="films_table.jsp"%>
</html>
