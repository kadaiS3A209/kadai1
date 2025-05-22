<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先登録完了</title>
<style>
    body { font-family: sans-serif; text-align: center; padding-top: 50px; }
    .message { font-size: 1.2em; margin-bottom: 20px; }
    .link { margin: 0 10px; text-decoration: none; color: #007bff; }
</style>
</head>
<body>
    <% String message = (String) session.getAttribute("message");
       if (message == null) message = "処理が完了しました。"; // デフォルトメッセージ
       session.removeAttribute("message"); // 一度表示したら消す
    %>
    <div class="message"><%= message %></div>
    <a href="AdminAddSupplierServlet" class="link">続けて仕入先を登録する</a>
    <a href="ReturnToMenuServlet" class="link">メニューに戻る</a>
</body>
</html>