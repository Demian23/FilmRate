<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>FilmRate</title>
</head>
<body>
    <table>
        <c:forEach items="${films}" var="film">
            <tr style="border: 1px solid black">
                <td style="border: 1px solid black"><a href="${pageContext.request.contextPath}/controller?film_id=${film.filmId}&command=FilmDetails">${film.originalTitle}</a></td>
                <td style="border: 1px solid black">${film.duration}</td>
                <td style="border: 1px solid black">${film.ageRating}</td>
                <td style="border: 1px solid black">${film.averageMark}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
