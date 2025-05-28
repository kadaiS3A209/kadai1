<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>処置履歴</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 90%; margin: 20px auto; }
    h1, h2 { text-align: center; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; font-size: 0.9em; }
    th { background-color: #f2f2f2; }
    .info-bar { padding: 10px; background-color: #e7f3fe; margin-bottom:15px; border-left: 6px solid #2196F3; }
    .no-history-message { text-align: center; color: #777; padding: 20px; }
    .button-bar { margin-top: 20px; text-align: center; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; text-decoration:none; margin: 0 5px; }
</style>
</head>
<body>
    <div class="container">
        <h1>処置履歴</h1>

        <div class="info-bar">
            <c:choose>
                <c:when test="${not empty displayPatientNameForHistory}">
                    <strong>対象患者:</strong> <c:out value="${displayPatientNameForHistory}"/> (ID: <c:out value="${searchedPatientIdForHistory}"/>)
                </c:when>
                <c:otherwise>
                     <strong>対象患者ID:</strong> <c:out value="${searchedPatientIdForHistory}"/>
                </c:otherwise>
            </c:choose>
        </div>

        <c:choose>
            <c:when test="${not empty treatmentHistoryList}">
                <h2>処置履歴一覧</h2>
                <table>
                    <thead>
                        <tr>
                            <th>処置日付</th>
                            <th>薬品名</th>
                            <th>数量</th>
                            <th>単位</th>
                            <th>担当医名</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="historyItem" items="${treatmentHistoryList}">
                            <tr>
                                <td><fmt:formatDate value="${historyItem.treatmentDate}" pattern="yyyy年MM月dd日" /></td>
                                <td><c:out value="${historyItem.medicineName}" /></td>
                                <td style="text-align:right;"><c:out value="${historyItem.quantity}" /></td>
                                <td><c:out value="${historyItem.unit}" /></td>
                                <td><c:out value="${historyItem.doctorName}" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <%-- D104 備考: 該当患者に過去の処置履歴がない場合は、処置履歴がない旨の表示をする。 --%>
                <p class="no-history-message">この患者の処置履歴はありません。</p>
            </c:otherwise>
        </c:choose>

        <div class="button-bar">
            <%-- D104 画面での入力: 「患者IDを指定する」ボタン --%>
            <a href="DoctorViewTreatmentHistoryServlet" class="button">別の患者IDを指定する</a>
            <%-- D104 画面での入力: 「メニューに戻る」ボタン --%>
            <a href="ReturnToMenuServlet" class="button" style="background-color:#6c757d;">医師メニューへ戻る</a>
        </div>
    </div>
</body>
</html>