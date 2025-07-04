<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>レントゲン指示一覧</title>
<style>
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
        <h1>レントゲン指示一覧</h1>

        <c:choose>
            <c:when test="${not empty orderList}">
                <table>
                    <thead>
                        <tr>
                            <th>指示ID</th>
                            <th>患者名 (患者ID)</th>
                            <th>指示日時</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orderList}">
                            <tr>
                                <td><c:out value="${order.xray_order_id}" /></td>
                                <td><c:out value="${order.patient_name}" /> (<c:out value="${order.patient_id}" />)</td>
                                <td><fmt:formatDate value="${order.ordered_at}" pattern="yyyy年MM月dd日 HH:mm" /></td>
                                <td>
                                    <%-- 次のステップで作成する「写真登録」機能へのリンク --%>
                                    <a href="RadiologyRegisterImageServlet?xrayOrderId=${order.xray_order_id}" class="action-button">写真登録へ</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p class="no-orders">現在、新しいレントゲン指示はありません。</p>
            </c:otherwise>
        </c:choose>
        <p style="margin-top:20px;"><a href="ReturnToMenuServlet">メニューへ戻る</a></p>
    </div>
</body>
</html>
