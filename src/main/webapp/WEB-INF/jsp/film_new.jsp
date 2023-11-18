<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>FilmRate/Admin/NewFilm</title>
</head>
<header>
  <%@include file="admin_header.jsp"%>
</header>
<body>
  <h1>Film inserter</h1>
  <h2><i><b>Pay attention when insert film, that default language is your chosen language</b></i></h2>
  <form action="controller" method="post">
    <input type="hidden" name="command" value="AddNewFilm"/>
    <label>
      Film name
      <input type="text" name="film_name" value=""/>
    </label>
    <label>
      Launch date
      <input type="date" name="film_launch_date" value=""/>
    </label>
    <label>
      Duration
      <input type="time" step="1" name="film_duration" value=""/>
    </label>
    <label>
      Age rate
      <input type="text" name="film_age_rate" value="PG-13"/>
    </label>
    <button type="submit">Submit</button>
  </form>
</body>
</html>
