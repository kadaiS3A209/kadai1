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
<title>仕入先一覧・検索</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 90%; margin: 20px auto; }
    h1 { text-align: center; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    .message { margin-bottom: 15px; padding:10px; }
    .error-message { color: red; border: 1px solid red; background-color: #ffebeb;}
    .info-message { border: 1px solid #add8e6; background-color: #f0f8ff; }
    .no-data-message { text-align: center; color: #777; padding: 20px; }
    .button-bar { margin-bottom: 20px; }
    .button { padding: 8px 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; text-decoration: none; margin-right: 5px; cursor:pointer; }
    .button:hover { background-color: #0056b3; }
    .search-form { margin-bottom: 20px; padding: 15px; border: 1px solid #eee; background-color: #f9f9f9; display: flex; align-items: center; gap: 10px; }
    .search-form label { margin-right: 5px; }
    .search-form input[type="text"] { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
</style>
</head>
<body>
    <div class="container">
        <h1>仕入先一覧・検索</h1>

        <div class="button-bar">
            <a href="AdminAddSupplierServlet" class="button">新規仕入先登録</a>
            <a href="ReturnToMenuServlet" class="button">管理者メニューへ戻る</a>
        </div>

        <%-- 検索フォーム --%>
        <form class="search-form" action="AdminListSuppliersServlet" method="post"> <%-- POSTでもGETでも対応できるようにサーブレット側を修正済 --%>
            <label for="minCapital">資本金 (以上):</label>
            <input type="text" id="minCapital" name="minCapital" value="<c:out value='${minCapitalInput}'/>" placeholder="例: 1000000 (半角数字)">
            <button type="submit" class="button">検索</button>
            <a href="AdminListSuppliersServlet" class="button">全件表示/クリア</a> <%-- 全件表示に戻るリンク --%>
        </form>

        <%-- エラーメッセージ表示 --%>
        <c:if test="${not empty errorMessage}">
            <div class="message error-message">
                <c:out value="${errorMessage}" />
            </div>
        </c:if>

        <%-- 検索条件表示 --%>
        <c:if test="${isSearchResult and not empty searchedMinCapital}">
            <div class="message info-message">
                検索条件: 資本金 <fmt:formatNumber value="${searchedMinCapital}" type="number" groupingUsed="true"/> 円 以上 の検索結果
            </div>
        </c:if>
        <c:if test="${isSearchResult and empty searchedMinCapital and empty errorMessage and minCapitalInput != ''}">
             <div class="message info-message">
                資本金 <c:out value="${minCapitalInput}"/> 円 以上 で検索しましたが、入力が正しくない可能性があります。
            </div>
        </c:if>


        <%-- JSTLを使ったリスト表示 --%>
        <c:choose>
            <c:when test="${not empty supplierList}">
                <h2>
                    <c:if test="${isSearchResult}">検索結果</c:if>
                    <c:if test="${not isSearchResult}">全仕入先</c:if>
                    (<c:out value="${supplierList.size()}"/> 件)
                </h2>
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
                <%-- エラーメッセージがない場合のみ「データなし」を表示 --%>
                <c:if test="${empty errorMessage}">
                    <p class="no-data-message">
                        <c:if test="${isSearchResult}">検索条件に一致する仕入先は見つかりませんでした。(0件)</c:if>
                        <c:if test="${not isSearchResult}">登録されている仕入先はありません。</c:if>
                    </p>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>