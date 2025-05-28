<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.PatientBean" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>保険証情報変更</title>
<style> /* 既存のフォームスタイルを適用 */
    body { font-family: sans-serif; }
    .container { width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;}
    .info-bar { background-color: #e7f3fe; border-left: 6px solid #2196F3; margin-bottom: 15px; padding:10px; }
    .form-group { margin-bottom: 15px; } label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="date"] { width: calc(100% - 22px); padding: 10px; border: 1px solid #ccc; border-radius: 4px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .message { margin: 10px 0; padding:10px; border-radius:4px; }
    .error-message-server { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb;}
    .success-message { color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb;}
</style>
</head>
<body>
    <div class="container">
        <h1>保険証情報変更</h1>

        <c:if test="${not empty patientToChange}">
            <div class="info-bar">
                <strong>対象患者:</strong> <c:out value="${patientToChange.patLname} ${patientToChange.patFname}"/>
                (ID: <c:out value="${patientToChange.patId}"/>)<br>
                現在の保険証記号番号: <c:out value="${patientToChange.hokenmei}"/><br>
                現在の有効期限: <fmt:formatDate value="${patientToChange.hokenexp}" pattern="yyyy年MM月dd日" />
            </div>
        </c:if>

        <c:if test="${not empty formError}"><div class="message error-message-server"><c:out value="${formError}" escapeXml="false"/></div></c:if>
        <c:if test="${not empty successMessage}"><div class="message success-message"><c:out value="${successMessage}"/></div></c:if>

        <c:if test="${not empty patientToChange}">
            <form action="ReceptionChangeInsuranceServlet" method="post">
                <input type="hidden" name="action" value="confirmChange">
                <input type="hidden" name="patIdToChange" value="<c:out value='${patientToChange.patId}'/>">

                <div class="form-group">
                    <label for="newHokenmei">新しい保険証記号番号 (変更する場合のみ入力):</label>
                    <%-- エラー時に戻された値を表示 --%>
                    <c:set var="prevHokenmei" value="${not empty userInput.hokenmei ? userInput.hokenmei : ''}" />
                    <input type="text" id="newHokenmei" name="newHokenmei" value="<c:out value='${prevHokenmei}'/>" placeholder="現在の値: ${patientToChange.hokenmei}">
                </div>
                <div class="form-group">
                    <label for="newHokenexp">新しい有効期限 (変更する場合のみ入力):</label>
                    <%-- エラー時に戻された値を表示 --%>
                    <c:set var="prevHokenexpStr" value="" />
                    <c:if test="${not empty userInput.hokenexp}">
                        <fmt:formatDate value="${userInput.hokenexp}" pattern="yyyy-MM-dd" var="prevHokenexpStr" />
                    </c:if>
                     <input type="date" id="newHokenexp" name="newHokenexp" value="<c:out value='${prevHokenexpStr}'/>">
                </div>
                <button type="submit" class="button">変更内容を確認</button>
            </form>
        </c:if>
        <p style="margin-top:20px;"><a href="ReceptionListPatientsServlet">患者一覧へ戻る</a></p>
    </div>
</body>
</html>