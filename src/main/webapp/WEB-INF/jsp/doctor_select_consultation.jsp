<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>処方対象の診察選択</title>
<style>
    body {
        font-family: 'Segoe UI', Meiryo, sans-serif;
        background-color: #f4f7f6;
        color: #333;
        margin: 0;
        padding: 20px;
    }
    .container {
        width: 90%;
        max-width: 800px;
        margin: 20px auto;
        padding: 25px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    }
    h1 {
        text-align: center;
        color: #007bff;
        border-bottom: 2px solid #007bff;
        padding-bottom: 10px;
        margin-bottom: 25px;
    }
    .patient-info-bar {
        background-color: #e7f3fe;
        border-left: 6px solid #2196F3;
        margin-bottom: 25px;
        padding: 15px;
        border-radius: 4px;
        font-size: 1.1em;
    }
    table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }
    th, td {
        border: 1px solid #ddd;
        padding: 12px;
        text-align: left;
    }
    th {
        background-color: #f2f2f2;
        font-weight: bold;
    }
    .action-button {
        display: inline-block;
        padding: 6px 12px;
        background-color: #28a745;
        color: white !important;
        text-decoration: none;
        border-radius: 4px;
        font-size: 0.9em;
        text-align: center;
        transition: background-color 0.2s;
    }
    .action-button:hover {
        background-color: #218838;
    }
    .no-data {
        text-align: center;
        color: #777;
        padding: 30px;
        font-size: 1.1em;
        background-color: #fafafa;
        border: 1px dashed #ddd;
    }
    .back-link {
        display: block;
        text-align: center;
        margin-top: 30px;
    }
</style>
</head>
<body>
    <div class="container">
        <h1>処方対象の診察を選択</h1>

        <c:if test="${not empty patient}">
            <div class="patient-info-bar">
                <strong>患者:</strong> <c:out value="${patient.patLname} ${patient.patFname}"/>
                (ID: <c:out value="${patient.patId}"/>)
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty consultationList}">
                <p>処方を行う診察（確定診断済み）を選択してください。</p>
                <table>
                    <thead>
                        <tr>
                            <th>診察日</th>
                            <th>確定疾病名 (コード)</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="consultation" items="${consultationList}">
                            <tr>
                                <td>
                                    <fmt:formatDate value="${consultation.consultationDate}" pattern="yyyy年MM月dd日"/>
                                </td>
                                <td>
                                    <c:out value="${consultation.diseaseName}"/>
                                    (<c:out value="${consultation.diseaseCode}"/>)
                                </td>
                                <td>
                                    <a href="DoctorPrescriptionServlet?action=showPrescriptionForm&consultationId=${consultation.consultationId}" class="action-button">この診察に処方する</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="no-data">
                    この患者には処方可能な確定診断済みの診察がありません。
                </div>
            </c:otherwise>
        </c:choose>

        <div class="back-link">
            <a href="DoctorListAllPatientsServlet">患者一覧へ戻る</a>
        </div>
    </div>
</body>
</html>
