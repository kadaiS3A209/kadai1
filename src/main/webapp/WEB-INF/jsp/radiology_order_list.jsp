<%-- レントゲン指示一覧画面 --%>
<h1>レントゲン指示一覧</h1>
<c:choose>
    <c:when test="${not empty orderList}">
        <table>
            <thead>
                <tr><th>指示ID</th><th>患者名 (ID)</th><th>指示日時</th><th>操作</th></tr>
            </thead>
            <tbody>
                <c:forEach var="order" items="${orderList}">
                    <tr>
                        <td><c:out value="${order.xray_order_id}" /></td>
                        <td><c:out value="${order.patient_name}" /> (<c:out value="${order.patient_id}" />)</td>
                        <td><fmt:formatDate value="${order.ordered_at}" pattern="yyyy/MM/dd HH:mm" /></td>
                        <td>
                            <a href="RadiologyRegisterImageServlet?xray_order_id=${order.xray_order_id}">写真登録</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p>現在、新しいレントゲン指示はありません。</p>
    </c:otherwise>
</c:choose>
<p><a href="ReturnToMenuServlet">メニューへ戻る</a></p>
