<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- 日付フォーマット用 --%>
<%@ page import="model.PatientBean" %> <%-- パスを合わせる --%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>患者登録 - 確認</title>
<style>
    /* 既存の確認画面スタイルを再利用または調整 */
    body { font-family: sans-serif; }
    .confirm-container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .confirm-item { margin-bottom: 10px; }
    .label { font-weight: bold; display: inline-block; width: 150px; }
    .value { display: inline-block; }
    .button-group { margin-top: 20px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right: 10px;}
</style>
</head>
<body>
    <div class="confirm-container">
        <h1>患者登録 - 確認</h1>
        <%
            PatientBean patient = (PatientBean) session.getAttribute("tempPatient");
            if (patient == null) {
                response.sendRedirect("ReceptionRegisterPatientServlet"); // エラーまたはセッション切れ
                return;
            }
        %>
        <p>以下の内容で登録します。よろしいですか？</p>

        <div class="confirm-item"><span class="label">患者ID:</span><span class="value"><c:out value="${tempPatient.patId}" /></span></div>
        <div class="confirm-item"><span class="label">患者姓:</span><span class="value"><c:out value="${tempPatient.patLname}" /></span></div>
        <div class="confirm-item"><span class="label">患者名:</span><span class="value"><c:out value="${tempPatient.patFname}" /></span></div>
        <div class="confirm-item"><span class="label">保険証記号番号:</span><span class="value"><c:out value="${tempPatient.hokenmei}" /></span></div>
        <div class="confirm-item">
            <span class="label">有効期限:</span>
            <span class="value">
                <c:if test="${not empty tempPatient.hokenexp}">
                    <fmt:formatDate value="${tempPatient.hokenexp}" pattern="yyyy年MM月dd日" />
                </c:if>
            </span>
        </div>

        <div class="button-group">
            <form action="ReceptionRegisterPatientServlet" method="post" style="display: inline;">
                <input type="hidden" name="action" value="register">
                <button type="submit" class="button">登録する</button>
            </form>
            <form action="ReceptionRegisterPatientServlet" method="get" style="display: inline;">
                <button type="submit" class="button" style="background-color:#6c757d;">修正する</button>
            </form>
        </div>
    </div>
</body>
</html>