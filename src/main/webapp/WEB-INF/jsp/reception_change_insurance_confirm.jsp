<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.PatientBean" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>保険証情報変更 - 確認</title>
<style> /* 既存の確認画面スタイルを適用 */ </style>
</head>
<body>
    <div class="container"> <%-- スタイル用コンテナ --%>
        <h1>保険証情報変更 - 確認</h1>
        <c:set var="patient" value="${sessionScope.patientForInsuranceConfirm}" />
        <c:set var="newHokenmei" value="${sessionScope.newHokenmeiForConfirm}" />
        <c:set var="newHokenexp" value="${sessionScope.newHokenexpForConfirm}" />

        <c:if test="${empty patient}">
            <p>確認情報がありません。患者一覧からやり直してください。</p>
            <p><a href="ReceptionListPatientsServlet">患者一覧へ戻る</a></p>
            <% return; %>
        </c:if>

        <p>以下の内容で保険証情報を変更します。よろしいですか？</p>
        <p><strong>患者ID:</strong> <c:out value="${patient.patId}"/></p>
        <p><strong>氏名:</strong> <c:out value="${patient.patLname} ${patient.patFname}"/></p>

        <p><strong>新しい保険証記号番号:</strong>
            <c:choose>
                <c:when test="${not empty newHokenmei}">${newHokenmei}</c:when>
                <c:otherwise>（変更なし）</c:otherwise>
            </c:choose>
        </p>
        <p><strong>新しい有効期限:</strong>
            <c:choose>
                <c:when test="${not empty newHokenexp}"><fmt:formatDate value="${newHokenexp}" pattern="yyyy年MM月dd日" /></c:when>
                <c:otherwise>（変更なし）</c:otherwise>
            </c:choose>
        </p>

        <form action="ReceptionChangeInsuranceServlet" method="post" style="display:inline-block; margin-right:10px;">
            <input type="hidden" name="action" value="executeUpdate">
            <input type="hidden" name="patIdToChange" value="<c:out value='${patient.patId}'/>">
            <button type="submit" class="button">確定変更</button>
        </form>
        <form action="ReceptionChangeInsuranceServlet" method="get" style="display:inline-block;">
             <input type="hidden" name="action" value="showForm">
             <input type="hidden" name="patId" value="<c:out value='${patient.patId}'/>">
            <button type="submit" class="button" style="background-color:#6c757d;">修正する</button>
        </form>
    </div>
</body>
</html>