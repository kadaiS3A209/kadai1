<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>患者登録完了</title>
<style> /* 既存の完了画面スタイルを再利用 */ </style>
</head>
<body>
    <h1>患者登録完了</h1>
    <c:if test="${not empty message}">
        <p><c:out value="${message}"/></p>
    </c:if>
    <p><a href="ReceptionRegisterPatientServlet">続けて患者を登録する</a></p>
    <p><a href="ReturnToMenuServlet">受付メニューへ戻る</a></p>
</body>
</html>