<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>診察状況の確認</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 800px; margin: 20px auto; padding: 20px; }
    h1, h2 { text-align: center; }
    .patient-info-bar { background-color: #e7f3fe; border-left: 6px solid #2196F3; margin-bottom: 20px; padding: 10px 15px; border-radius: 4px; }
    .status-section { margin-bottom: 25px; padding: 20px; border: 1px solid #eee; border-radius: 8px; }
    .status-section h3 { margin-top: 0; border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; }
    .status-label { font-weight: bold; }
    .status-value.completed { color: green; font-weight: bold; }
    .status-value.pending { color: orange; font-weight: bold; }
    table { width: 100%; border-collapse: collapse; margin-top: 15px; }
    th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
    th { background-color: #f2f2f2; }
    .disease-form { margin-top: 30px; padding: 20px; border: 2px solid #28a745; background-color: #f0fff0; border-radius: 8px; }
    .disease-form select { width: 100%; padding: 8px; font-size: 1.1em; }
    .button { padding: 10px 20px; background-color: #28a745; color: white; border: none; border-radius: 4px; font-size: 1.1em; cursor: pointer; }
</style>
</head>
<body>
    <div class="container">
        <h1>診察状況の確認</h1>

        <c:if test="${not empty patient}">
            <div class="patient-info-bar">
                <strong>対象患者:</strong> <c:out value="${patient.patLname} ${patient.patFname}"/>
                (ID: <c:out value="${patient.patId}"/>) <br>
                <strong>診察日:</strong> <fmt:formatDate value="${consultation.consultationDate}" pattern="yyyy年MM月dd日"/>
            </div>
        </c:if>

        <%-- レントゲン指示の状況表示 --%>
        <div class="status-section">
            <h3>レントゲン指示</h3>
            <c:choose>
                <c:when test="${not empty xrayOrder}">
                    <p><span class="status-label">ステータス:</span>
                        <c:choose>
                            <c:when test="${xrayOrder.orderStatus == '撮影完了'}">
                                <span class="status-value completed">撮影完了</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-value pending">指示済み</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                </c:when>
                <c:otherwise><p>レントゲン指示はありません。</p></c:otherwise>
            </c:choose>
        </div>

        <%-- 臨床検査の状況表示 --%>
        <div class="status-section">
            <h3>臨床検査指示</h3>
            <c:choose>
                <c:when test="${not empty labOrder.testItems}">
                    <p><span class="status-label">全体ステータス:</span>
                        <c:choose>
                            <c:when test="${labOrder.orderStatus == '完了'}">
                                <span class="status-value completed">完了</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status-value pending">${labOrder.orderStatus}</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <table>
                        <thead><tr><th>検査項目</th><th>結果</th><th>単位</th><th>基準値</th></tr></thead>
                        <tbody>
                            <c:forEach var="item" items="${labOrder.testItems}">
                                <tr>
                                    <td><c:out value="${item.testName}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty item.resultValue}"><c:out value="${item.resultValue}"/></c:when>
                                            <c:otherwise><span class="status-value pending">結果待ち</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${item.unit}"/></td>
                                    <td><c:out value="${item.referenceValue}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise><p>検査指示はありません。</p></c:otherwise>
            </c:choose>
        </div>

        <%-- ★要件: 検査が完了している場合は疾病名の登録を強制する --%>
        <c:if test="${xrayOrder.orderStatus == '撮影完了' && labOrder.orderStatus == '完了'}">
            <div class="disease-form">
                <h3>疾病名の登録</h3>
                <p>全ての指示結果が揃いました。疾病名を登録して診察を完了してください。</p>
                <form action="DoctorConsultationServlet" method="post">
                    <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                    <input type="hidden" name="action" value="registerDisease">
                    <input type="hidden" name="consultationId" value="<c:out value='${consultation.consultationId}'/>">
                    
                    <div class="form-group">
                        <label for="diseaseCode">疾病名:</label>
                        <select id="diseaseCode" name="diseaseCode" required>
                            <option value="">-- 疾病名を選択してください --</option>
                            <c:forEach var="disease" items="${diseaseList}">
                                <option value="<c:out value='${disease.code}'/>">
                                    <c:out value="${disease.name}"/> (<c:out value="${disease.code}"/>)
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div style="text-align:center; margin-top:20px;">
                        <button type="submit" class="button">この疾病名で診察を完了する</button>
                    </div>
                </form>
            </div>
        </c:if>

        <p style="margin-top:20px;"><a href="DoctorListAllPatientsServlet">患者一覧へ戻る</a></p>
    </div>
</body>
</html>
