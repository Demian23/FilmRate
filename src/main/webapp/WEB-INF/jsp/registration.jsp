<html lang="en">
<head>
    <title>FilmRate/registration</title>
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
        <label>
            Email:
            <input type="email" name="user-email" value=""/>
        </label>
        <button type="submit">Submit</button>
    </form>
</body>
</html>
