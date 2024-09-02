<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, edu.domain.model.Movie, edu.domain.model.Auditorium, edu.domain.model.Ticket, java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html>
<head>
    <title>Подтверждение покупки</title>
</head>
<body>
<%
    Movie movie = (Movie) request.getAttribute("movie");
    Auditorium auditorium = (Auditorium) request.getAttribute("auditorium");
    List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
    java.time.LocalDateTime startTime = (java.time.LocalDateTime) request.getAttribute("startTime");

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>

<h1>Билеты куплены успешно!</h1>
<p>Фильм: <%= movie.getName() %></p>
<p>Начало сеанса: <%= startTime.format(dateTimeFormatter) %></p>
<p>Зал: <%= auditorium.getId() %></p>

<h2>Информация о билетах</h2>
<ul>
    <%
        for (Ticket ticket : tickets) {
    %>
    <li>Ряд: <%= ticket.getRow() %>, Место: <%= ticket.getPlace() %></li>
    <%
        }
    %>
</ul>

</body>
</html>
