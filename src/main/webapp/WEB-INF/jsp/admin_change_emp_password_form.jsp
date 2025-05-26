<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.EmployeeBean" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員パスワード変更</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;}
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="password"], input[type="text"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .button:hover { background-color: #0056b3; }
    .message { margin-bottom: 15px; padding:10px; }
    .error-message { color: red; border: 1px solid red; background-color: #ffebeb;}
    .success-message { color: green; border: 1px solid green; background-color: #e6ffe6;}
    .info-bar { padding: 10px; background-color: #f0f0f0; margin-bottom:15px; border-radius:4px; }
    .back-link { display:inline-block; margin-top: 20px; margin-right: 10px; }
</style>
</head>
<body>
    <div class="container">
        <h1>従業員パスワード変更</h1>

        <c:if test="${not empty employeeToChange}">
            <div class="info-bar">
                <strong>対象従業員:</strong> <c:out value="${employeeToChange.empLname} ${employeeToChange.empFname}"/>
                (ID: <c:out value="${employeeToChange.empId}"/>)
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="message error-message"><c:out value="${errorMessage}"/></div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="message success-message"><c:out value="${successMessage}"/></div>
        </c:if>

        <c:if test="${not empty employeeToChange}">
            <form action="AdminChangeEmpPasswordServlet" method="post">
                <input type="hidden" name="empIdToChange" value="<c:out value='${employeeToChange.empId}'/>">
                <div class="form-group">
                    <label for="newPassword">新しいパスワード:</label>
                    <input type="password" id="newPassword" name="newPassword" required>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">新しいパスワード (確認):</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>
                <button type="submit" class="button">パスワードを変更する</button>
            </form>
        </c:if>
        <c:if test="${empty employeeToChange && empty errorMessage && empty successMessage}">
             <p>パスワードを変更する従業員が選択されていません。</p>
        </c:if>

        <a href="AdminListEmployeesServlet" class="back-link">従業員一覧へ戻る</a>
        <a href="ReturnToMenuServlet" class="back-link">管理者メニューへ戻る</a>
    </div>
</body>
</html>