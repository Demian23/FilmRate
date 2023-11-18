<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table>
    <c:forEach items="${sessionScope.get(\"users\")}" var="userData">
        <tr style="border: 1px solid black">
            <td style="border: 1px solid black">${userData.user.name}</td>
            <td>Rate: ${userData.user.userRate}</td>
        <c:if test="${not empty userData.bannedInfo}">
            <td style="border: 1px solid black">${userData.bannedInfo.startPeriod}</td>
            <td style="border: 1px solid black">${userData.bannedInfo.endPeriod}</td>
            <td style="border: 1px solid black">by ${userData.bannedInfo.adminBannedName}</td>
            <td>
                <a href="controller?user_id=${userData.bannedInfo.userId}&command=TakeOffBan">
                    TakeOff
                </a>
            </td>
        </c:if>
        <c:if test="${empty userData.bannedInfo}">
            <td>
                <a href="controller?user_id=${userData.user.userId}&command=BanUser">
                    Ban
                </a>
            </td>
        </c:if>
        </tr>
    </c:forEach>
</table>
