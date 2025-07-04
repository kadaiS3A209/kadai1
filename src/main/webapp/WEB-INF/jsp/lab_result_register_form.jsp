<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>臨床検査 結果登録</title>
<style>
    /* radiology_order_list.jsp と同じスタイルを適用 */
    body { font-family: sans-serif; }
    .container { width: 90%; margin: 20px auto; }
    h1 { text-align: center; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
    th { background-color: #f2f2f2; }
    .action-button { padding: 5px 10px; background-color: #198754; color:white !important; text-decoration:none; border-radius:3px; }
    .no-orders { text-align: center; color: #777; padding: 30px; font-size: 1.1em; }
</style>
</head>
<body>
    <div class="container">
        <h1>臨床検査 結果登録</h1>
        <%-- (ここに患者情報などを表示) --%>
        <p><strong>指示ID:</strong> <c:out value="${labTestOrderId}"/></p>
        
        <form action="LabResultRegisterServlet" method="post">
            <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
            <input type="hidden" name="labTestOrderId" value="<c:out value='${labTestOrderId}'/>">

            <table>
                <thead>
                    <tr><th>検査項目</th><th>販売名</th><th>結果</th><th>単位</th><th>基準値</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${testItems}">
                        <tr>
                            <td><c:out value="${item.testName}"/></td>
                            <td>(<c:out value="${item.salesName}"/>)</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty item.resultValue}">
                                        <%-- 登録済みの結果は表示のみ --%>
                                        <c:out value="${item.resultValue}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <%-- 未登録の項目は入力欄を表示 --%>
                                        <input type="text" name="result_${item.labTestItemId}">
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><c:out value="${item.unit}"/></td>
                            <td><c:out value="${item.referenceValue}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div style="text-align:center; margin-top:20px;">
                <button type="submit" class="button">結果を登録する</button>
            </div>
        </form>
        <p><a href="LabOrderListServlet">指示一覧へ戻る</a></p>
    </div>
</body>
</html>
