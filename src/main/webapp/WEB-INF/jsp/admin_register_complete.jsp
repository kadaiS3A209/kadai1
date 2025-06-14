<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLのCoreライブラリを使用するための宣言を追加 --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員登録完了</title>
<style>
    body { font-family: sans-serif; text-align: center; padding-top: 50px; }
    .message { font-size: 1.2em; margin-bottom: 20px; }
    .link { margin: 0 10px; text-decoration: none; color: #007bff; }
</style>
</head>
<body>
    <%--
        サーブレットから完了メッセージが渡された場合はそれを表示し、
        なければデフォルトのメッセージを表示します。
        これにより、この完了画面を他の機能と共通で使いやすくなります。
    --%>
    <c:set var="completionMessage" value="${not empty requestScope.message ? requestScope.message : '従業員の登録が完了しました。'}" />

    <h1>従業員登録完了</h1>
    <p class="message"><c:out value="${completionMessage}"/></p>
    
    <p>
        <a href="AdminRegisterEmployeeServlet" class="link">続けて従業員を登録する</a>
        <a href="ReturnToMenuServlet" class="link">管理者メニューへ戻る</a>
    </p>
</body>
</html>