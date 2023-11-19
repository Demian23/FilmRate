<%@ page contentType="text/html;charset=UTF-8"%>
<c:if test="${requestScope.get(\"marksToCurrentFilm\") != null}">
    <c:forEach items="${requestScope.get(\"marksToCurrentFilm\")}" var="mark">
        <ul>
            <li>
                <p>${mark.mark}</p>
                <p>${mark.userName}</p>
            </li>
        </ul>
    </c:forEach>
</c:if>
<c:if test="${requestScope.get(\"marksToCurrentFilm\") == null}">
    <button><a href="controller?command=AddMarksToCurrentFilmInSession&film_id=${sessionScope.get("filmInfo").film.filmID}">Show marks</a></button>
</c:if>
