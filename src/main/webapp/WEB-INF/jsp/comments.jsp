<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${sessionScope.get(\"commentsToCurrentFilm\") != null}">
    <c:forEach items="${sessionScope.get(\"commentsToCurrentFilm\")}" var="comment">
        <ul>
            <li>
                <p>${comment.text}</p>
                <p>${comment.date}</p>
                <p>${comment.score}</p>
                <p>${comment.userName}</p>
                <button><a href="${comment.commentId}">Like</a></button>
                <button><a href="${comment.commentId}">Dislike</a></button>
            </li>
        </ul>
    </c:forEach>
</c:if>
<c:if test="${sessionScope.get(\"commentsToCurrentFilm\") == null}">
    <button><a href="controller?command=AddCommentsToCurrentFilmInSession&film_id=${sessionScope.get("filmInfo").film.filmID}">Show comments</a></button>
</c:if>
