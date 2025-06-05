<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>他病院 電話番号変更</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;}
    .form-group { margin-bottom: 15px; } label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="tel"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right:10px;}
    .message { margin:10px 0; padding:10px; border-radius:4px; }
    .error-message { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb;}
    .info-bar { padding: 10px; background-color: #f0f0f0; margin-bottom:15px; border-radius:4px; }
</style>
</head>
<body>
    <div class="container">
        <h1>他病院 電話番号変更</h1>

        <c:if test="${not empty tabyouinToChange}">
            <div class="info-bar">
                <strong>他病院ID:</strong> <c:out value="${tabyouinToChange.tabyouinId}"/><br>
                <strong>他病院名:</strong> <c:out value="${tabyouinToChange.tabyouinMei}"/><br>
                <strong>現在の電話番号:</strong> <c:out value="${tabyouinToChange.tabyouinTel}"/>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage_telChange}"><div class="message error-message"><c:out value="${errorMessage_telChange}"/></div></c:if>

        <c:if test="${not empty tabyouinToChange}">
            <form action="AdminManageTabyouinServlet" method="post">
                <input type="hidden" name="action" value="updateTel">
                <input type="hidden" name="tabyouinIdToChange" value="<c:out value='${tabyouinToChange.tabyouinId}'/>">
                <%-- <input type="hidden" name="sourceList" value="<c:out value='${sourceList}'/>"> --%> <%-- 必要なら遷移元情報を引き継ぐ --%>

                <div class="form-group">
                    <label for="newTel">新しい電話番号:</label>
                    <input type="tel" id="newTel" name="newTel" value="<c:out value='${param.newTel != null ? param.newTel : tabyouinToChange.tabyouinTel}'/>" required pattern="^[0-9\\-()]{10,15}$" title="例: 000-0000-0000 (ハイフンと括弧は任意)">
                </div>
                <button type="submit" class="button">電話番号を変更する</button>
                <a href="AdminManageTabyouinServlet" class="button" style="background-color:#6c757d;">一覧へ戻る</a>
            </form>
        </c:if>
        <c:if test="${empty tabyouinToChange && empty errorMessage_telChange}">
             <p>変更対象の他病院が選択されていません。</p>
             <p><a href="AdminManageTabyouinServlet">他病院一覧へ戻る</a></p>
        </c:if>
    </div>
</body>
</html>