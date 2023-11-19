<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table class="films_table">
    <tr>
        <td>
            Name
        </td>
        <td>
           Duration
        </td>
        <td>
            Launch date
        </td>
        <td>
            Age rate
        </td>
        <td>
            Average mark
        </td>
    </tr>
    <c:forEach items="${sessionScope.get(\"films\")}" var="film">
        <tr>
            <td> <a href="${pageContext.request.contextPath}/controller?film_id=${film.filmID}&command=FilmDetails">
                    ${film.localisedText.localisedText} (<i>${film.text.textEntity}</i>)
            </a></td>
            <td> ${film.duration}</td>
            <td> ${film.launchDate}</td>
            <td> ${film.ageRating}</td>
            <td>${film.averageMark}</td>
        </tr>
    </c:forEach>
</table>
