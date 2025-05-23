<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.ShiiregyoshaBean" %> <%-- パスを合わせる --%>
<%-- JSTLを使う場合 --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先一覧</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 90%; margin: 20px auto; }
    h1 { text-align: center; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    .action-links a { margin-right: 10px; text-decoration: none; }
    .message { margin-bottom: 15px; }
    .error-message { color: red; border: 1px solid red; padding: 10px; }
    .no-data-message { text-align: center; color: #777; padding: 20px; }
    .button-bar { margin-bottom: 20px; }
    .button { padding: 8px 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; text-decoration: none; margin-right: 5px; }
    .button:hover { background-color: #0056b3; }
</style>
</head>
<body>
    <div class="container">
        <h1>仕入先一覧</h1>

        <div class="button-bar">
            <a href="AdminAddSupplierServlet" class="button">新規仕入先登録</a>
            <a href="ReturnToMenuServlet" class="button">管理者メニューへ戻る</a>
        </div>

        <%-- エラーメッセージ表示 --%>
        <c:if test="${not empty errorMessage}">
            <div class="message error-message">
                <c:out value="${errorMessage}" />
            </div>
        </c:if>

        <%-- JSTLを使ったリスト表示 --%>
        <c:choose>
            <c:when test="${not empty supplierList}">
                <table>
                    <thead>
                        <tr>
                            <th>仕入先ID</th>
                            <th>仕入先名</th>
                            <th>住所</th>
                            <th>電話番号</th>
                            <th>資本金 (円)</th>
                            <th>納期 (日)</th>
                            <%-- <th>操作</th> --%>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="supplier" items="${supplierList}">
                            <tr>
                                <td><c:out value="${supplier.shiireId}" /></td>
                                <td><c:out value="${supplier.shiireMei}" /></td>
                                <td><c:out value="${supplier.shiireAddress}" /></td>
                                <td><c:out value="${supplier.shiireTel}" /></td>
                                <td style="text-align: right;"><fmt:formatNumber value="${supplier.shihonkin}" type="number" groupingUsed="true" /></td>
                                <td style="text-align: right;"><c:out value="${supplier.nouki}" /></td>
                                <%--
                                <td>
                                    <a href="AdminEditSupplierServlet?id=${supplier.shiireId}">編集</a>
                                    <a href="AdminDeleteSupplierServlet?id=${supplier.shiireId}" onclick="return confirm('本当に削除しますか？');">削除</a>
                                </td>
                                --%>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <%-- エラーメッセージがない場合（リストが空、またはnullだがエラーではない） --%>
                <c:if test="${empty errorMessage}">
                    <p class="no-data-message">登録されている仕入先はありません。</p>
                </c:if>
            </c:otherwise>
        </c:choose>

        <%-- スクリプトレットを使ったリスト表示の例 (JSTLが使えない場合の代替)
        <%
            List<ShiiregyoshaBean> supplierListScriptlet = (List<ShiiregyoshaBean>) request.getAttribute("supplierList");
            String errorMessageScriptlet = (String) request.getAttribute("errorMessage");

            if (errorMessageScriptlet != null) {
        %>
            <div class="message error-message"><%= errorMessageScriptlet %></div>
        <%
            }

            if (supplierListScriptlet != null && !supplierListScriptlet.isEmpty()) {
        %>
            <table>
                <thead>
                    <tr>
                        <th>仕入先ID</th>
                        <th>仕入先名</th>
                        <th>住所</th>
                        <th>電話番号</th>
                        <th>資本金 (円)</th>
                        <th>納期 (日)</th>
                    </tr>
                </thead>
                <tbody>
        <%
                for (ShiiregyoshaBean supplier : supplierListScriptlet) {
        %>
                    <tr>
                        <td><%= supplier.getShiireId() %></td>
                        <td><%= supplier.getShiireMei() %></td>
                        <td><%= supplier.getShiireAddress() %></td>
                        <td><%= supplier.getShiireTel() %></td>
                        <td style="text-align: right;"><%= String.format("%,d", supplier.getShihonkin()) %></td>
                        <td style="text-align: right;"><%= supplier.getNouki() %></td>
                    </tr>
        <%
                }
        %>
                </tbody>
            </table>
        <%
            } else if (errorMessageScriptlet == null) { // エラーでもなく、リストも空
        %>
            <p class="no-data-message">登録されている仕入先はありません。</p>
        <%
            }
        %>
        --%>

    </div>
</body>
</html>