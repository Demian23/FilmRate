<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header>
    <button><a href="controller?command=LogOut">LogOut</a></button>
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
</header>