<%-- /WEB-INF/jsp/error/404.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>404 Not Found - ページが見つかりません</title>
<style>
    body { font-family: 'Segoe UI', Meiryo, sans-serif; text-align: center; padding-top: 50px; background-color: #f8f9fa; }
    .error-container { max-width: 600px; margin: auto; }
    .error-code { font-size: 6em; font-weight: bold; color: #6c757d; }
    .error-message { font-size: 1.5em; color: #343a40; margin-top: 0; }
    .error-description { color: #6c757d; margin-top: 20px; }
    .home-link { display: inline-block; margin-top: 30px; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
</style>
</head>
<body>
    <div class="error-container">
        <div class="error-code">404</div>
        <h1 class="error-message">ページが見つかりません</h1>
        <p class="error-description">
            お探しのページは、移動または削除された可能性があります。<br>
            URLをご確認の上、再度お試しください。
        </p>
        <a href="${pageContext.request.contextPath}/ReturnToMenuServlet" class="home-link">メニューへ戻る</a>
    </div>
</body>
</html>