<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.PatientBean, model.LabTestBean, java.util.List" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>診察・指示画面</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 800px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .patient-info-bar { background-color: #e7f3fe; border-left: 6px solid #2196F3; margin-bottom: 20px; padding: 10px 15px; }
    .order-section { margin-bottom: 20px; padding: 15px; border: 1px solid #eee; }
    .lab-test-list { display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; max-height: 200px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; }
    .lab-test-item { display: block; } /* ラベル全体をクリック可能にする */
    .button { padding: 10px 20px; background-color: #28a745; color: white; border: none; border-radius: 4px; font-size: 1.1em; cursor: pointer; }
    .error-message { color: red; }
</style>
</head>
<body>
    <div class="container">
        <h1>診察・指示入力</h1>

        <c:if test="${not empty patient}">
            <div class="patient-info-bar">
                <strong>対象患者:</strong> <c:out value="${patient.patLname} ${patient.patFname}"/>
                (ID: <c:out value="${patient.patId}"/>)
            </div>
        </c:if>

        <c:if test="${not empty errorMessage_consultation}">
            <p class="error-message"><c:out value="${errorMessage_consultation}"/></p>
        </c:if>

        <form action="DoctorCreateConsultationServlet" method="post">
            <%-- CSRF対策トークン --%>
            <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
            <%-- どの患者に対する指示かを送信 --%>
            <input type="hidden" name="patientId" value="<c:out value='${patient.patId}'/>">

            <fieldset class="order-section">
                <legend><h3>レントゲン指示</h3></legend>
                <label>
                    <input type="checkbox" name="xrayOrder" value="true"> レントゲン撮影を指示する
                </label>
            </fieldset>

            <fieldset class="order-section">
                <legend><h3>検査指示</h3></legend>
                <p>指示する検査項目を全て選択してください:</p>
                <div class="lab-test-list">
                    <c:forEach var="test" items="${labTestList}">
                        <label class="lab-test-item">
                            <input type="checkbox" name="testCodes" value="<c:out value='${test.jlac11Code}'/>">
                            <c:out value="${test.jlacTestName}"/> (<c:out value="${test.salesName}"/>)
                        </label>
                    </c:forEach>
                </div>
            </fieldset>

            <div style="text-align:center; margin-top:30px;">
                <button type="submit" class="button">指示を確定する</button>
            </div>
        </form>
         <p style="margin-top:20px;"><a href="DoctorListAllPatientsServlet">患者一覧へ戻る</a></p>
    </div>
</body>
</html>
