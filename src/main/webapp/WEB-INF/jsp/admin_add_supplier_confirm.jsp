<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLのCoreライブラリとFormattingライブラリを使用するための宣言を追加 --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先登録 - 確認</title>
<style>
    body { font-family: sans-serif; }
    .confirm-container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .confirm-item { margin-bottom: 10px; }
    .label { font-weight: bold; display: inline-block; width: 150px; }
    .value { display: inline-block; }
    .button-group { margin-top: 20px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px;}
    .button:hover { background-color: #0056b3; }
    .back-button { background-color: #6c757d; }
    .back-button:hover { background-color: #5a6268; }
</style>
</head>
<body>
    <div class="confirm-container">
        <h1>仕入先登録 - 確認</h1>
        
        <%-- セッションからtempSupplierを取得し、もし存在しない場合は入力画面にリダイレクト --%>
        <c:if test="${empty sessionScope.tempSupplier}">
            <% response.sendRedirect("AdminAddSupplierServlet"); %>
        </c:if>

        <c:set var="supplier" value="${sessionScope.tempSupplier}" />

        <p>以下の内容で登録します。よろしいですか？</p>

        <div class="confirm-item">
            <span class="label">仕入先ID:</span>
            <span class="value"><c:out value="${supplier.shiireId}" /></span>
        </div>
        <div class="confirm-item">
            <span class="label">仕入先名:</span>
            <span class="value"><c:out value="${supplier.shiireMei}" /></span>
        </div>
        <div class="confirm-item">
            <span class="label">住所:</span>
            <span class="value"><c:out value="${supplier.shiireAddress}" /></span>
        </div>
        <div class="confirm-item">
            <span class="label">電話番号:</span>
            <span class="value"><c:out value="${supplier.shiireTel}" /></span>
        </div>
        <div class="confirm-item">
            <span class="label">資本金:</span>
            <span class="value">
                <%-- fmt:formatNumberで数値をカンマ区切りで表示 --%>
                <fmt:formatNumber value="${supplier.shihonkin}" type="number" groupingUsed="true"/> 円
            </span>
        </div>
        <div class="confirm-item">
            <span class="label">納期:</span>
            <span class="value"><c:out value="${supplier.nouki}" /> 日</span>
        </div>

        <div class="button-group">
            <form action="AdminAddSupplierServlet" method="post" style="display: inline;">
                <%-- CSRF対策トークンも引き継ぐ --%>
                <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                <input type="hidden" name="action" value="register">
                <button type="submit" class="button">登録する</button>
            </form>
            <form action="AdminAddSupplierServlet" method="get" style="display: inline;">
                <%-- GETで入力画面に戻る。セッションのtempSupplierが入力フォームで使われる --%>
                <button type="submit" class="button back-button">修正する</button>
            </form>
        </div>
    </div>
</body>
</html>