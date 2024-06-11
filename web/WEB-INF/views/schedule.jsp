<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Расписание сеансов</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
<h1>Расписание сеансов</h1>

<!-- Форма для выбора даты -->
<form action="schedule" method="get">
    <label for="date">Выберите дату:</label>
    <input type="date" id="date" name="date" value="${selectedDate}">
    <button type="submit">Показать</button>
</form>

<c:if test="${not empty movieSessionsMap}">
    <c:forEach var="entry" items="${movieSessionsMap}">
        <c:set var="movie" value="${entry.key}" />
        <c:set var="sessions" value="${entry.value}" />

        <h2>${movie.name} (${movie.year})</h2>
        <c:if test="${not empty sessions}">
            <ul>
                <c:forEach var="session" items="${sessions}">
                    <li>
                        <fmt:formatDate value="${session}" pattern="HH:mm" />
                    </li>
                </c:forEach>
            </ul>
        </c:if>
        <c:if test="${empty sessions}">
            <p>Нет сеансов на выбранную дату.</p>
        </c:if>
    </c:forEach>
</c:if>
<c:if test="${empty movieSessionsMap}">
    <p>Нет фильмов на выбранную дату.</p>
</c:if>

</body>
</html>
