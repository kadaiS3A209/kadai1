<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>他病院登録完了</title>
<style> /* 仕入先登録完了画面のスタイルを参考に調整 */
    body { font-family: sans-serif; text-align: center; padding-top: 50px; }
    .message { font-size: 1.2em; margin-bottom: 20px; }
    .link { margin: 0 10px; text-decoration: none; color: #007bff; }
</style>
</head>
<body>
    <c:set var="completionMessage" value="${sessionScope.message_tabyouin_management}" />
    <c:if test="${not empty completionMessage}">
        <h1 class="message"><c:out value="${completionMessage}"/></h1>
        <% session.removeAttribute("message_tabyouin_management"); %>
    </c:if>
    <c:if test="${empty completionMessage}">
        <h1 class="message">処理が完了しました。</h1>
    </c:if>

    <p>
        <a href="AdminAddTabyouinServlet" class="link">続けて他病院を登録する</a>
        <a href="ReturnToMenuServlet" class="link">管理者メニューへ戻る</a>
        <%-- <a href="AdminListTabyouinServlet" class="link">他病院一覧へ</a> --%> <%-- 一覧画面作成後に追加 --%>
    </p>
</body>
</html>