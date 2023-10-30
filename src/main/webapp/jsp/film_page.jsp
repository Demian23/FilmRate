<%@ page contentType="text/html;charset=UTF-8" language="java" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>${sessionScope.get("filmInfo").film.localisedText}</title>
</head>
<body>
<h1>${sessionScope.get("filmInfo").film.localisedText.localisedText}</h1>
<h2>${sessionScope.get("filmInfo").film.text.textEntity}</h2>
<p>Launch date: ${sessionScope.get("filmInfo").film.launchDate}</p>
<p>Duration: ${sessionScope.get("filmInfo").film.duration}</p>
<p>Age rating: ${sessionScope.get("filmInfo").film.ageRating}</p>
<p>Average mark: ${sessionScope.get("filmInfo").film.averageMark}</p>
<form action="${pageContext.request.contextPath}/controller" method="post">
    <input type="hidden" name="command" value="">
    <label>
        Mark:
        <input type="number" name="user-mark">
    </label>
    <label>
        Comment:
        <input type="text" name="user-comment">
    </label>
    <button type="submit">Submit</button>
</form>
<!--TODO show other comments and marks (if user want)-->
</body>
</html>
