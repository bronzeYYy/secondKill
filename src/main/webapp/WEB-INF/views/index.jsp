<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<link href="/css/bootstrap.css" rel="stylesheet">
<script src="/js/jquery-3.3.1.min.js"></script>
<body class="container text-center">

<p style="display: none; margin-top: 5%" id="msg"></p>
<table class="table table-hover">
  <tr>
    <td>商品id</td>
    <td>商品标题</td>
    <td>库存</td>
    <td>开始时间</td>
    <td>操作</td>
  </tr>
  <c:forEach items="${goods}" var="i">
    <tr>
      <td>${i.goodsId}</td>
      <td>${i.title}</td>
      <td>${i.number}</td>
      <td>${i.startTime}</td>
      <td>
        <c:choose>
          <c:when test="${i.number <= 0}">
            <button class="btn" disabled>库存不足</button>
          </c:when>
          <c:otherwise>
            <button class="btn btn-primary" onclick="kill('${i.goodsId}', this)">秒杀</button>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </c:forEach>
</table>
<script>
  var msg = $('#msg');
  var kill = function (id, button) {
      button.disabled = 'disabled';
      msg.css('display', 'none');
      $.ajax({
          url: '/kill',
          type: 'post',
          data: {'id': id},
          success: function (data) {
              msg.text(data);
          }
      });
      msg.css('display', 'block');
      button.removeAttribute('disabled');
  }
</script>
</body>
</html>
