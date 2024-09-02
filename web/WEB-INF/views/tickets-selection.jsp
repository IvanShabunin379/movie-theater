<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, edu.domain.model.Movie, edu.domain.model.Auditorium, edu.domain.model.Ticket, java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html>
<head>
    <title>Выбор билетов</title>
    <style>
        .vip {
            font-weight: bold;
        }
        .three-d {
            font-style: italic;
        }
        .seat {
            display: inline-block;
            width: 30px;
            height: 30px;
            text-align: center;
            line-height: 30px;
            margin: 2px;
            border: 1px solid black;
        }
        .seat.available {
            background-color: green;
        }
        .seat.occupied {
            background-color: red;
        }
        .seat.selected {
            background-color: blue;
        }
    </style>
</head>
<body>
<%
    Movie movie = (Movie) request.getAttribute("movie");
    Auditorium auditorium = (Auditorium) request.getAttribute("auditorium");
    String countryName = (String) request.getAttribute("countryName");
    String directorName = (String) request.getAttribute("directorName");
    List<Ticket> tickets = (List<Ticket>) request.getAttribute("tickets");
    java.time.LocalDateTime startTime = (java.time.LocalDateTime) request.getAttribute("startTime");

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
%>

<h1><%= movie.getName() %></h1>
<p><%= movie.getYear() %></p>
<p><%= countryName %></p>
<p><%= movie.getGenre().getRussianName() %></p>
<p><%= movie.getDuration() %> мин</p>
<p><%= movie.getDescription() %></p>
<p>Режиссер: <%= directorName %></p>
<p>Начало сеанса: <%= startTime.format(dateTimeFormatter) %></p>
<p>Зал: <%= auditorium.getId() %></p>
<p class="<%= auditorium.getIsVip() ? "vip" : "" %>"><%= auditorium.getIsVip() ? "VIP" : "" %></p>
<p class="<%= auditorium.getIs3d() ? "three-d" : "" %>"><%= auditorium.getIs3d() ? "3D" : "" %></p>

<h2>Места в зале</h2>
<form action="tickets-selection" method="post">
    <input type="hidden" name="session_id" value="<%= request.getParameter("session_id") %>">
    <%
        int rows = auditorium.getNumberOfRows();
        int seatsInRow = auditorium.getNumberOfSeatsInRow();

        boolean[][] seatsOccupied = new boolean[rows][seatsInRow];

        for (Ticket ticket : tickets) {
            seatsOccupied[ticket.getRow() - 1][ticket.getPlace() - 1] = ticket.getIsPurchased();
        }

        for (int row = 0; row < rows; row++) {
            for (int seat = 0; seat < seatsInRow; seat++) {
                boolean isOccupied = seatsOccupied[row][seat];
    %>
    <div class="seat <%= isOccupied ? "occupied" : "available" %>">
        <label>
            <input type="checkbox" name="selected_seats" value="<%= row + 1 %>-<%= seat + 1 %>" <%= isOccupied ? "disabled" : "" %>>
            <%= row + 1 %>/<%= seat + 1 %>
        </label>
    </div>
    <%
        }
    %>
    <br/>
    <%
        }
    %>
    <button type="submit">Купить выбранные билеты</button>
</form>

</body>
</html>
