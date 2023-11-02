<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>FilmRate</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/controller" method="post">
        <input type="hidden" name="command" value="ChangeLanguage">
        <label>
            Language
            <select name="language">
                <c:forEach items="${sessionScope.get(\"languages\")}" var="lang">
                    <option value="${lang.id}"
                            <c:if test="${lang.id == sessionScope.get(\"currentLangID\")}">selected</c:if>>
                            ${lang.name}</option>
                </c:forEach>
            </select>
        </label>
        <button type="submit">Change</button>
    </form>
    <table>
        <c:forEach items="${sessionScope.get(\"films\")}" var="film">
            <tr style="border: 1px solid black">
                <td style="border: 1px solid black"><a href="${pageContext.request.contextPath}/controller?film_id=${film.filmID}&command=FilmDetails">
                        ${film.localisedText.localisedText} (<i>${film.text.textEntity}</i>)
                </a></td>
                <td style="border: 1px solid black">${film.duration}</td>
                <td style="border: 1px solid black">${film.ageRating}</td>
                <td style="border: 1px solid black">${film.wholeMarksSum / film.wholeMarksAmount}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
