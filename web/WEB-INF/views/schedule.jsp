<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map, java.util.List, edu.domain.model.Movie, edu.domain.model.Session" %>

<!DOCTYPE html>
<html>
<head>
    <title>Расписание сеансов</title>
</head>
<body>
<h1>Расписание сеансов</h1>

<form action="schedule" method="get">
    <label for="date">Выберите дату:</label>
    <input type="date" id="date" name="date" value="<%= request.getAttribute("selectedDate") %>">
    <button type="submit">Показать</button>
</form>

<%
    Map<Movie, List<Session>> movieSessionsMap = (Map<Movie, List<Session>>) request.getAttribute("movieSessionsMap");
    if (movieSessionsMap == null || movieSessionsMap.isEmpty()) {
%>
<p>Нет фильмов на выбранную дату.</p>
<%
} else {
    for (Map.Entry<Movie, List<Session>> entry : movieSessionsMap.entrySet()) {
        Movie movie = entry.getKey();
        List<Session> sessions = entry.getValue();
%>
<h2><%= movie.getName() %></h2>
<h3><%= '(' + movie.getGenre().getRussianName() + ')' %></h3>
<%
    if (sessions == null || sessions.isEmpty()) {
%>
<p>Нет сеансов на выбранную дату.</p>
<%
} else {
%>
<ul>
    <%
        for (Session currentSession : sessions) {
    %>
    <li>
        <a href="<%= request.getContextPath() %>/tickets-selection?session_id=<%= currentSession.getId() %>">
            <%= currentSession.getStartTime().toLocalTime() %>
        </a>
    </li>
    <%
        }
    %>
</ul>
<%
            }
        }
    }
%>

</body>
</html>
