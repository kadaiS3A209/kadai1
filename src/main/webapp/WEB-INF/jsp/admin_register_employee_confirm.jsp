<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLのCoreライブラリを使用するための宣言を追加 --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.EmployeeBean" %> <%-- response.sendRedirectのためだけに残すことも可能 --%>

<%-- セッションからtempEmployeeを取得し、もし存在しない場合は入力画面にリダイレクト --%>
<c:if test="${empty sessionScope.tempEmployee}">
    <% response.sendRedirect("AdminRegisterEmployeeServlet"); %>
</c:if>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員登録 - 確認</title>
<style>
    /* ... (CSSスタイルは変更なし) ... */
    body { font-family: sans-serif; }
    .confirm-container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .confirm-item { margin-bottom: 10px; }
    .label { font-weight: bold; display: inline-block; width: 150px; }
    .value { display: inline-block; }
    .button-group { margin-top: 20px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px;}
    .back-button { background-color: #6c757d; }
</style>
</head>
<body>
    <div class="confirm-container">
        <h1>従業員登録 - 確認</h1>

        <%-- セッションから取得した従業員情報をページ内で使えるように変数にセット --%>
        <c:set var="employee" value="${sessionScope.tempEmployee}" />

        <p>以下の内容で登録します。よろしいですか？</p>

        <div class="confirm-item">
            <span class="label">従業員ID:</span>
            <span class="value"><c:out value="${employee.empid}" /></span>
        </div>
        <div class="confirm-item">
            <span class="label">姓:</span>
            <span class="value"><c:out value="${employee.emplname}" /></span>
        </div>
        <div class="confirm-item">
            <span class="label">名:</span>
            <span class="value"><c:out value="${employee.empfname}" /></span>
        </div>
        <div class="confirm-item">
            <span class="label">ロール:</span>
            <span class="value">
                <%-- JSTLのchooseタグでロールIDに応じた表示名を出し分ける --%>
                <c:choose>
                    <c:when test="${employee.role == 1}">受付</c:when>
                    <c:when test="${employee.role == 2}">医師</c:when>
                    <c:when test="${employee.role == 3}">管理者</c:when>
                    <c:otherwise>不明なロール</c:otherwise>
                </c:choose>
                (<c:out value="${employee.role}" />)
            </span>
        </div>
        <div class="confirm-item">
            <span class="label">パスワード:</span>
            <span class="value">********</span> <%-- パスワードは表示しない --%>
        </div>

        <div class="button-group">
            <%-- 登録実行用フォーム --%>
            <form action="AdminRegisterEmployeeServlet" method="post" style="display: inline;">
                <%-- CSRF対策トークンも引き継ぐ --%>
                <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                <input type="hidden" name="action" value="register">
                <button type="submit" class="button">登録する</button>
            </form>

            <%-- 修正用フォーム (入力画面に戻る) --%>
            <form action="AdminRegisterEmployeeServlet" method="get" style="display: inline;">
                <button type="submit" class="button back-button">修正する</button>
            </form>
        </div>
    </div>
</body>
</html>