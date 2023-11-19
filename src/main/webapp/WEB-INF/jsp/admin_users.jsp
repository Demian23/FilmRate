<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>FilmRate/${sessionScope.get("currentUserName")}/Users</title>
    <link href="css/admin_page.css" rel="stylesheet" />
</head>
<header>
    <%@include file="admin_header.jsp"%>
</header>
<body>
<%@include file="admin_users_table.jsp"%>
</body>
</html>
