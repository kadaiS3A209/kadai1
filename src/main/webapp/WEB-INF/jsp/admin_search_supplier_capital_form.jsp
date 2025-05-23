<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先検索 - 資本金</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;}
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="text"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .button:hover { background-color: #0056b3; }
    .error-message { color: red; font-size: 0.9em; margin-top: 3px; }
    .back-link { display:block; margin-top: 20px; }
</style>
</head>
<body>
    <div class="container">
        <h1>仕入先検索 - 資本金</h1>

        <c:if test="${not empty errorMessage}">
            <p class="error-message"><c:out value="${errorMessage}" /></p>
        </c:if>

        <form action="AdminSearchSupplierByCapitalServlet" method="post">
            <div class="form-group">
                <label for="minCapital">検索する資本金額 (以上入力):</label>
                <input type="text" id="minCapital" name="minCapital" value="<c:out value='${param.minCapital}'/>" required placeholder="例: 1000000 (半角数字)">
                <%-- テストケースには全角やカンマ、円記号の入力もあるが、ここではまず半角数字を基本とする --%>
            </div>
            <button type="submit" class="button">検索</button>
        </form>
        <a href="ReturnToMenuServlet" class="back-link">管理者メニューへ戻る</a>
    </div>
</body>
</html>