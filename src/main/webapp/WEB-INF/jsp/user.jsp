<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>FilmRate</title>
</head>
<%@include file="header.jsp"%>
<body>
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
