<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Успешная авторизация</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .message {
            background-color: #dff0d8;
            border: 1px solid #ddd;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 20px;
            font-size: 18px;
            color: #333;
        }
        .link-container {
            margin-top: 20px;
        }
        .link-container a {
            text-decoration: none;
            font-size: 16px;
            color: #4CAF50;
            border-bottom: 1px solid #4CAF50;
        }
        .link-container a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="message">
    Вы успешно авторизованы и теперь можете покупать билеты :)
</div>
<div class="link-container">
    <p>
        <a href="<%= request.getContextPath() %>/schedule">К расписанию фильмов</a>
    </p>
</div>
</body>
</html>
