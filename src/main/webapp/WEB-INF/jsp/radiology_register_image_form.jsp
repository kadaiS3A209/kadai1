<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>レントゲン写真 登録</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .info-bar { background-color: #e7f3fe; border-left: 6px solid #2196F3; margin-bottom: 20px; padding: 10px 15px; }
    .form-group { margin-bottom: 10px; }
    label { display: inline-block; width: 80px; }
    input[type="text"] { width: 400px; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .button { padding: 10px 20px; background-color: #198754; color: white; border: none; border-radius: 4px; font-size: 1.1em; cursor: pointer; }
    .error-message { color: red; font-weight: bold; margin-bottom: 15px; }
</style>
</head>
<body>
    <div class="container">
        <h1>レントゲン写真 登録</h1>

        <c:if test="${not empty orderDetails}">
            <div class="info-bar">
                <strong>指示ID:</strong> <c:out value="${orderDetails.xray_order_id}"/><br>
                <strong>患者名:</strong> <c:out value="${orderDetails.patient_name}"/> (ID: <c:out value="${orderDetails.patient_id}"/>)
            </div>
        </c:if>

        <c:if test="${not empty formError}">
            <p class="error-message"><c:out value="${formError}"/></p>
        </c:if>

        <p>撮影した画像のファイル名を10個まで入力できます。</p>

        <form action="RadiologyRegisterImageServlet" method="post" enctype="multipart/form-data">
            <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
            <input type="hidden" name="xrayOrderId" value="<c:out value='${orderDetails.xray_order_id}'/>">
            
            <%-- ▼▼▼ inputのtypeを "text" から "file" に変更 ▼▼▼ --%>
            <%-- multiple属性で複数ファイル選択を許可（ブラウザによる） --%>
            <c:forEach var="i" begin="1" end="10">
                <div class="form-group">
                    <label for="fileName${i}">ファイル${i}:</label>
                    <input type="file" id="fileName${i}" name="fileUpload" accept="image/*">
                </div>
            </c:forEach>

            <div style="text-align:center; margin-top:30px;">
                <button type="submit" class="button">登録して指示を完了する</button>
            </div>
        </form>
        <p style="margin-top:20px;"><a href="RadiologyOrderListServlet">指示一覧へ戻る</a></p>
    </div>
</body>
</html>
