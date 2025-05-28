<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>処置履歴確認 - 患者選択</title>
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
        <h1>処置履歴確認 - 患者選択</h1>

        <c:if test="${not empty errorMessage_selectPatient}">
            <p class="error-message"><c:out value="${errorMessage_selectPatient}" /></p>
        </c:if>

        <form action="DoctorViewTreatmentHistoryServlet" method="post">
            <input type="hidden" name="action" value="viewHistory">
            <div class="form-group">
                <label for="patientIdForHistory">患者ID:</label>
                <input type="text" id="patientIdForHistory" name="patientIdForHistory" value="<c:out value='${param.patientIdForHistory}'/>" required maxlength="8">
            </div>
            <button type="submit" class="button">処置履歴を表示</button>
        </form>
        <a href="ReturnToMenuServlet" class="back-link">医師メニューへ戻る</a>
    </div>
</body>
</html>