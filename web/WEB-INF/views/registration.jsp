<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Регистрация</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        h2 {
            color: dimgray;
            text-align: center;
        }
        .error-message {
            background-color: #f2f2f2;
            color: #333;
            border: 1px solid #ddd;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        form {
            max-width: 500px;
            margin: 0 auto;
            background-color: #f9f9f9;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }
        input[type="text"], input[type="password"], input[type="date"], select {
            width: 100%;
            padding: 8px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        button {
            background-color: dimgray;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover {
            background-color: dimgray;
        }
    </style>
</head>
<body>
<h2>Регистрация</h2>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null) {
%>
<p class="error-message"><%= errorMessage %></p>
<%
    }
%>

<form action="registration" method="post">
    <div>
        <label for="email">E-mail:</label>
        <input type="text" id="email" name="email" required>

        <label for="name">Имя:</label>
        <input type="text" id="name" name="name" required>

        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required>
    </div>

    <button type="submit">Зарегистрироваться</button>
</form>
</body>
</html>
