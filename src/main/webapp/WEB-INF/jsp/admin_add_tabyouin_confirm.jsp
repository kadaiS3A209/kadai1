<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.TabyouinBean" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>他病院 新規登録 - 確認</title>
<style> /* 仕入先登録確認画面のスタイルを参考に調整 */
    body { font-family: sans-serif; }
    .confirm-container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .confirm-item { margin-bottom: 10px; } .label { font-weight: bold; display: inline-block; width: 150px; }
    .value { display: inline-block; } .button-group { margin-top: 20px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px;}
</style>
</head>
<body>
    <div class="confirm-container">
        <h1>他病院 新規登録 - 確認</h1>
        <c:set var="tabyouin" value="${sessionScope.tempTabyouin}" />

        <c:if test="${empty tabyouin}">
            <p>確認情報がありません。入力画面からやり直してください。</p>
            <p><a href="AdminAddTabyouinServlet">入力画面へ戻る</a></p>
            <%-- return; --%>
        </c:if>

        <p>以下の内容で登録します。よろしいですか？</p>
        <div class="confirm-item"><span class="label">他病院ID:</span><span class="value"><c:out value="${tabyouin.tabyouinId}" /></span></div>
        <div class="confirm-item"><span class="label">他病院名:</span><span class="value"><c:out value="${tabyouin.tabyouinMei}" /></span></div>
        <div class="confirm-item"><span class="label">住所:</span><span class="value"><c:out value="${tabyouin.tabyouinAddrss}" /></span></div>
        <div class="confirm-item"><span class="label">電話番号:</span><span class="value"><c:out value="${tabyouin.tabyouinTel}" /></span></div>
        <div class="confirm-item"><span class="label">資本金:</span><span class="value"><fmt:formatNumber value="${tabyouin.tabyouinShihonkin}" type="number" groupingUsed="true"/> 円</span></div>
        <div class="confirm-item"><span class="label">救急対応:</span><span class="value"><c:out value="${tabyouin.kyukyu == 1 ? 'あり' : 'なし'}" /></span></div>

        <div class="button-group">
            <form action="AdminAddTabyouinServlet" method="post" style="display: inline;">
                <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                <input type="hidden" name="action" value="register">
                <button type="submit" class="button">登録する</button>
            </form>
            <form action="AdminAddTabyouinServlet" method="get" style="display: inline;">
                <button type="submit" class="button" style="background-color:#6c757d;">修正する</button>
            </form>
        </div>
    </div>
</body>
</html>