<%--
  Created by IntelliJ IDEA.
  User: 17691
  Date: 2019-08-14
  Time: 10:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <link href="/css/bootstrap.css" rel="stylesheet">
  <title>登陆</title>
</head>
<body class="container text-center">
<form action="login" method="post" style="margin-top: 3%">
  <label>手机号
    <input name="phone" type="tel" class="form-control">
  </label>
  <p style="color: #d43f3a">${errorMsg}</p>
  <input class="btn btn-primary" type="submit" value="OK">
</form>
</body>
</html>
