<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先検索結果 - 資本金</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 90%; margin: 20px auto; }
    h1, h2 { text-align: center; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    .message { margin-bottom: 15px; padding:10px; border: 1px solid #eee; background-color: #f9f9f9; }
    .no-data-message { text-align: center; color: #777; padding: 20px; }
    .button-bar { margin-bottom: 20px; text-align: center; }
    .button { padding: 8px 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; text-decoration: none; margin-right: 5px; }
    .button:hover { background-color: #0056b3; }
</style>
</head>
<body>
    <div class="container">
        <h1>仕入先検索結果 - 資本金</h1>

        <div class="message">
            検索条件: 資本金 <fmt:formatNumber value="${searchedMinCapital}" type="number" groupingUsed="true"/> 円 以上
        </div>

        <div class="button-bar">
            <a href="AdminSearchSupplierByCapitalServlet" class="button">別の条件で検索する</a>
            <a href="ReturnToMenuServlet" class="button">管理者メニューへ戻る</a>
        </div>

        <c:choose>
            <c:when test="${not empty supplierList}">
                <h2>検索結果 (<c:out value="${supplierList.size()}"/> 件)</h2>
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
                        <c:forEach var="supplier" items="${supplierList}">
                            <tr>
                                <td><c:out value="${supplier.shiireId}" /></td>
                                <td><c:out value="${supplier.shiireMei}" /></td>
                                <td><c:out value="${supplier.shiireAddress}" /></td>
                                <td><c:out value="${supplier.shiireTel}" /></td>
                                <td style="text-align: right;"><fmt:formatNumber value="${supplier.shihonkin}" type="number" groupingUsed="true" /></td>
                                <td style="text-align: right;"><c:out value="${supplier.nouki}" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <%-- テストケース「登録データにおける資本金最大値+1の半角数値で検索結果が0件になる」 -> 「検索結果が0件と表示」 [cite: 4] --%>
                <p class="no-data-message">検索条件に一致する仕入先は見つかりませんでした。(0件)</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>