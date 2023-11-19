<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="user_header">
    <p class="user_header_welcome_phrase">FilmRate - rate your favorite films, <b>${sessionScope.get("currentUserName")}</b>!</p>
    <button class="user_header_logout"><a href="controller?command=LogOut">LogOut</a></button>
    <button class="user_header_main_page"><a href="controller?command=FillFilmsInUserPage">To main page</a></button>
    <form action="${pageContext.request.contextPath}/controller" method="post">
        <input type="hidden" name="command" value="ChangeLanguage">
        <div class="user_header_chose_lang">
            <label class="user_header_languages">
                Language
                <select name="language">
                    <c:forEach items="${sessionScope.get(\"languages\")}" var="lang">
                        <option value="${lang.id}"
                                <c:if test="${lang.id == sessionScope.get(\"currentLangID\")}">selected</c:if>>
                                ${lang.name}</option>
                    </c:forEach>
                </select>
            </label>
            <button class="user_header_change_lang" type="submit">Change</button>
        </div>
    </form>
</div>