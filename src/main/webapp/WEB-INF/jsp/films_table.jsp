<table class="films_table">
    <c:forEach items="${sessionScope.get(\"films\")}" var="film">
        <tr>
            <td> <a href="${pageContext.request.contextPath}/controller?film_id=${film.filmID}&command=FilmDetails">
                    ${film.localisedText.localisedText} (<i>${film.text.textEntity}</i>)
            </a></td>
            <td> ${film.duration}</td>
            <td> ${film.ageRating}</td>
            <td> ${film.wholeMarksSum / film.wholeMarksAmount}</td>
        </tr>
    </c:forEach>
</table>
