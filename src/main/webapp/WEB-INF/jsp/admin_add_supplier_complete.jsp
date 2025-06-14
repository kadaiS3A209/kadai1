<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLのCoreライブラリを使用するための宣言を追加 --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

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
    <%--
        セッションスコープから完了メッセージを取得します。
        もしメッセージが存在すれば、それをページ内で使う変数 'completionMessage' に格納し、
        セッションからはそのメッセージを削除します。
        もしメッセージが存在しなければ、デフォルトのメッセージを 'completionMessage' に設定します。
    --%>
    <c:choose>
        <c:when test="${not empty sessionScope.message}">
            <c:set var="completionMessage" value="${sessionScope.message}" />
            <c:remove var="message" scope="session" />
        </c:when>
        <c:otherwise>
            <c:set var="completionMessage" value="処理が完了しました。" />
        </c:otherwise>
    </c:choose>

    <%-- 準備したメッセージを表示します --%>
    <div class="message"><c:out value="${completionMessage}"/></div>

    <a href="AdminAddSupplierServlet" class="link">続けて仕入先を登録する</a>
    <a href="ReturnToMenuServlet" class="link">メニューに戻る</a>
</body>
</html>