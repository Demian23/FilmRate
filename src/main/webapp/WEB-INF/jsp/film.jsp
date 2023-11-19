<%@ page import="by.wtj.filmrate.bean.UserMark" %>
<%@ page import="by.wtj.filmrate.bean.CompleteFilmInfo" %>
<%@ page import="by.wtj.filmrate.controller.RequestParameterName" %>
<%@ page import="by.wtj.filmrate.bean.UserComment" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>FilmRate/${sessionScope.get("filmInfo").film.text.textEntity}</title>
    <link href="css/user_page.css" rel="stylesheet" />
</head>
<header>
    <%@include file="user_header.jsp"%>
</header>
<body>
<h1>${sessionScope.get("filmInfo").film.localisedText.localisedText}</h1>
<h2>${sessionScope.get("filmInfo").film.text.textEntity}</h2>
<p>Launch date: ${sessionScope.get("filmInfo").film.launchDate}</p>
<p>Duration: ${sessionScope.get("filmInfo").film.duration}</p>
<p>Age rating: ${sessionScope.get("filmInfo").film.ageRating}</p>
<p>Average mark: ${sessionScope.get("filmInfo").film.wholeMarksSum / sessionScope.get("filmInfo").film.wholeMarksAmount}</p>
<form action="${pageContext.request.contextPath}/controller" method="post">
    <input type="hidden" name="command" value="SetUserMarkAndComment">
    <label>
        Mark:
        <input type="number" min="0" name="<%=RequestParameterName.USER_MARK%>" value='<%=((CompleteFilmInfo)request.getSession().getAttribute("filmInfo")).getMark().getMark()!= UserMark.NO_MARK ? String.format("%d", ((CompleteFilmInfo)request.getSession().getAttribute("filmInfo")).getMark().getMark()) : "" %>'>
    </label>
    <label>
        Comment:
        <input type="text" name="<%=RequestParameterName.USER_COMMENT%>" value='
<%=((CompleteFilmInfo)request.getSession().getAttribute("filmInfo")).getComment().getCommentId() != UserComment.NO_COMMENT_ID? ((CompleteFilmInfo)request.getSession().getAttribute("filmInfo")).getComment().getText(): "" %>'>
    </label>
    <button type="submit">Submit</button>
</form>
<!--TODO show other comments and marks (if user want)-->
<%@include file="film_comments.jsp"%>
<%@include file="film_marks.jsp"%>
</body>
</html>
