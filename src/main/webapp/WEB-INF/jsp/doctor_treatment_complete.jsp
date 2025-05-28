<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>処置完了</title>
<style>
    body { font-family: sans-serif; text-align: center; padding-top: 50px; }
    .message { font-size: 1.2em; margin-bottom: 20px; }
    .link { margin: 0 10px; text-decoration: none; color: #007bff; }
</style>
</head>
<body>
    <c:if test="${not empty successMessage}">
        <h1 class="message"><c:out value="${successMessage}"/></h1>
    </c:if>
    <c:if test="${empty successMessage}">
        <h1 class="message">処置が完了しました。</h1>
    </c:if>

    <p>
        <a href="DoctorListAllPatientsServlet" class="link">患者一覧へ戻る</a>
        <a href="ReturnToMenuServlet" class="link">医師メニューへ戻る</a>
    </p>
</body>
</html>