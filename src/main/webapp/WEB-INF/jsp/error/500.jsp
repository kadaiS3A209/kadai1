<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>システムエラー</title>
</head>
<body>
    <h1>システムエラーが発生しました</h1>
    <p>申し訳ございませんが、処理を続行できませんでした。時間をおいて再度お試しください。</p>
    <a href="${pageContext.request.contextPath}/">トップページに戻る</a>
    <%-- isErrorPage="true" にすると、例外オブジェクト exception が使えるが、ユーザーには表示しない --%>
    <%-- サーバーのログには、exception.printStackTrace() などで詳細なエラーを出力する --%>
</body>
</html>