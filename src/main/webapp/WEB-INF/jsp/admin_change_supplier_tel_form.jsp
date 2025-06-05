<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先電話番号変更</title>
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
        <h1>仕入先電話番号変更</h1>

        <c:if test="${not empty supplierToChange}">
            <div class="info-bar">
                <strong>仕入先ID:</strong> <c:out value="${supplierToChange.shiireId}"/><br>
                <strong>仕入先名:</strong> <c:out value="${supplierToChange.shiireMei}"/><br>
                <strong>現在の電話番号:</strong> <c:out value="${supplierToChange.shiireTel}"/>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage_telChange}"><div class="message error-message"><c:out value="${errorMessage_telChange}"/></div></c:if>
        <%-- 成功メッセージは一覧画面に表示するため、ここでは通常不要 --%>

        <c:if test="${not empty supplierToChange}">
            <form action="AdminListSuppliersServlet" method="post"> <%-- 送信先を一覧サーブレットに --%>
                <input type="hidden" name="action" value="updateTel">
                <input type="hidden" name="shiireIdToChange" value="<c:out value='${supplierToChange.shiireId}'/>">
                <input type="hidden" name="sourceList" value="<c:out value='${sourceList}'/>"> <%-- 遷移元情報を引き継ぐ --%>

                <div class="form-group">
                    <label for="newTel">新しい電話番号:</label>
                    <input type="tel" id="newTel" name="newTel" value="<c:out value='${param.newTel != null ? param.newTel : supplierToChange.shiireTel}'/>" required pattern="^[0-9\\-()]{10,15}$" title="例: 000-0000-0000">
                </div>
                <button type="submit" class="button">電話番号を変更する</button>
                <%-- 戻り先を動的にするためのc:chooseブロック --%>
                <c:choose>
                    <c:when test="${not empty sourceList}">
                         <%-- 具体的なURLをsourceListの値によって変えるのはサーブレットの役割。
                              ここでは、単純にAdminListSuppliersServletに戻り、
                              sourceListの値（や他のセッションに保存した検索条件）を元に
                              サーブレットが元の表示を復元することを期待する。
                              または、単純に全件表示に戻るリンクでも良い。
                         --%>
                        <a href="AdminListSuppliersServlet" class="button" style="background-color:#6c757d;">一覧へ戻る</a>
                    </c:when>
                    <c:otherwise>
                        <a href="AdminListSuppliersServlet" class="button" style="background-color:#6c757d;">仕入先一覧へ戻る</a>
                    </c:otherwise>
                </c:choose>
            </form>
        </c:if>
        <c:if test="${empty supplierToChange && empty errorMessage_telChange}">
             <p>変更対象の仕入先が選択されていません。</p>
             <p><a href="AdminListSuppliersServlet">仕入先一覧へ戻る</a></p>
        </c:if>
    </div>
</body>
</html>