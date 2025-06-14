<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.PatientBean" %>
<%@ page import="model.DrugOrderItemBean" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>薬剤投与指示 - 確認</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 70%; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;}
    .patient-info-bar { background-color: #e7f3fe; border-left: 6px solid #2196F3; margin-bottom: 20px; padding:10px 15px; }
    h1, h2 { text-align: center; margin-bottom: 20px;}
    table { width: 100%; border-collapse: collapse; margin-top: 15px; margin-bottom: 25px; }
    th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
    th { background-color: #f2f2f2; }
    .button-group { text-align: center; margin-top: 30px; }
    .button { padding: 12px 20px; color: white; border: none; border-radius: 4px; cursor: pointer; margin: 0 10px; text-decoration:none; font-size:1em; }
    .confirm-button { background-color: #28a745; } /* 緑系 */
    .confirm-button:hover { background-color: #1e7e34; }
    .back-button { background-color: #ffc107; color:black !important; } /* 黄色系 */
    .back-button:hover { background-color: #e0a800; }
    .error-message { color: #D8000C; background-color: #FFD2D2; padding: 10px; margin-bottom: 15px; border-radius: 4px; border: 1px solid #D8000C;}

</style>
</head>
<body>
    <div class="container">
        <h1>薬剤投与指示 - 内容確認</h1>

        <c:if test="${not empty confirmPageError}">
            <p class="error-message"><c:out value="${confirmPageError}"/></p>
        </c:if>

        <c:if test="${not empty patient}">
            <div class="patient-info-bar">
                <strong>対象患者:</strong> <c:out value="${patient.patLname} ${patient.patFname}"/>
                (ID: <c:out value="${patient.patId}"/>)
            </div>
        </c:if>
        <c:if test="${empty patient}">
            <p class="error-message">対象患者情報がありません。</p>
        </c:if>

        <%
            String patIdForCart = null;
            PatientBean patientForCartJSP = (PatientBean) request.getAttribute("patient");
            if (patientForCartJSP != null) {
                patIdForCart = patientForCartJSP.getPatId();
            }
            List<DrugOrderItemBean> cartToConfirm = null;
            if (patIdForCart != null) {
                cartToConfirm = (List<DrugOrderItemBean>) session.getAttribute("drugCart_" + patIdForCart);
            }
        %>
        <c:set var="cartItems" value="<%= cartToConfirm %>" />

        <h2>指示内容</h2>
        <c:choose>
            <c:when test="${not empty cartItems}">
                <table>
                    <thead>
                        <tr>
                            <th>薬剤名</th>
                            <th>数量</th>
                            <th>単位</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}">
                            <tr>
                                <td><c:out value="${item.medicineName}"/></td>
                                <td style="text-align:right;"><c:out value="${item.quantity}"/></td>
                                <td><c:out value="${item.unit}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <div class="button-group">
                    <%-- 薬剤選択画面に戻るボタン (D103 備考) --%>
                    <a href="DoctorDrugAdministrationServlet?action=start&patId=<c:out value='${patient.patId}'/>" class="button back-button">薬剤選択に戻る</a>

                    <%-- 処置確定ボタン (D103 機能説明2) --%>
                    <form action="DoctorDrugAdministrationServlet" method="post" style="display:inline;">
                        <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                        <input type="hidden" name="action" value="confirmTreatment">
                        <input type="hidden" name="patientId" value="<c:out value='${patient.patId}'/>">
                        <button type="submit" class="button confirm-button">この内容で処置を確定する</button>
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <p style="text-align:center; color:orange;">カートに薬剤がありません。薬剤選択画面から指示を追加してください。</p>
                 <div class="button-group">
                    <a href="DoctorDrugAdministrationServlet?action=start&patId=<c:out value='${patient.patId}'/>" class="button back-button">薬剤選択に戻る</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>