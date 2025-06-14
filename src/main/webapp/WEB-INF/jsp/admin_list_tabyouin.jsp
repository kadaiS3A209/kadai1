<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>他病院一覧・管理</title>
<style>
    body { font-family: sans-serif; } .container { width: 95%; margin: 20px auto; }
    h1 { text-align:center; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; font-size: 0.9em;}
    th { background-color: #f2f2f2; }
    .action-button { padding: 5px 10px; background-color: #ffc107; color:black !important; text-decoration:none; border-radius:3px; border:none; cursor:pointer; margin-right:5px; }
    .button { padding: 8px 12px; background-color: #007bff; color:white; text-decoration:none; border-radius:3px; border:none; cursor:pointer; }
    .button-bar, .search-forms-container { margin-bottom: 15px; padding:10px; background-color:#f9f9f9; border:1px solid #eee; border-radius:4px;}
    .search-form { display: inline-block; margin-right: 20px; }
    .search-form label { margin-right: 5px; }
    .search-form input[type="text"] { padding: 8px; border-radius:3px; border:1px solid #ccc; margin-right:5px;}
    .message { margin:10px 0; padding:10px; border-radius:4px; }
    .success-message { color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb;}
    .error-message { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb;}
    .info-message { border: 1px solid #add8e6; background-color: #f0f8ff; }
</style>
</head>
<body>
    <div class="container">
        <h1>他病院一覧・管理</h1>

        <div class="button-bar">
            <a href="AdminAddTabyouinServlet" class="button" style="background-color:#28a745;">新規他病院登録</a>
            <a href="ReturnToMenuServlet" class="button" style="background-color:#6c757d;">管理者メニューへ戻る</a>
        </div>

        <div class="search-forms-container">
            <form class="search-form" action="AdminManageTabyouinServlet" method="post">
                <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                <input type="hidden" name="action" value="searchAddress">
                <label for="searchAddress">住所で検索:</label>
                <input type="text" id="searchAddress" name="searchAddress" value="<c:out value='${searchedAddress}'/>" placeholder="住所の一部を入力">
                <button type="submit" class="button">住所検索</button>
            </form>

            <form class="search-form" action="AdminManageTabyouinServlet" method="post">
                <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                <input type="hidden" name="action" value="searchCapital">
                <label for="minCapital">資本金 (以上):</label>
                <input type="text" id="minCapital" name="minCapital" value="<c:out value='${minCapitalInput}'/>" placeholder="例: 1000000">
                <button type="submit" class="button">資本金検索</button>
            </form>
            <a href="AdminManageTabyouinServlet" class="button" style="background-color:#17a2b8; margin-left:10px;">全件表示/クリア</a>
        </div>

        <c:if test="${not empty listSuccessMessage_tabyouin}"><div class="message success-message"><c:out value="${listSuccessMessage_tabyouin}"/></div></c:if>
        <c:if test="${not empty listErrorMessage_tabyouin}"><div class="message error-message"><c:out value="${listErrorMessage_tabyouin}"/></div></c:if>

        <c:if test="${isSearchResult and not empty searchedAddress}">
            <div class="message info-message">検索条件: 住所に「<c:out value="${searchedAddress}"/>」を含む他病院</div>
        </c:if>
        <c:if test="${isSearchResult and not empty searchedMinCapital}">
            <div class="message info-message">検索条件: 資本金 <fmt:formatNumber value="${searchedMinCapital}" type="number" groupingUsed="true"/> 円 以上 の他病院</div>
        </c:if>

        <c:choose>
            <c:when test="${not empty tabyouinList}">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>病院名</th>
                            <th>住所</th>
                            <th>電話番号</th>
                            <th>資本金</th>
                            <th>救急対応</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="tb" items="${tabyouinList}">
                            <tr>
                                <td><c:out value="${tb.tabyouinId}" /></td>
                                <td><c:out value="${tb.tabyouinMei}" /></td>
                                <td><c:out value="${tb.tabyouinAddrss}" /></td>
                                <td><c:out value="${tb.tabyouinTel}" /></td>
                                <td style="text-align:right;"><fmt:formatNumber value="${tb.tabyouinShihonkin}" type="number" groupingUsed="true"/> 円</td>
                                <td style="text-align:center;"><c:out value="${tb.kyukyu == 1 ? 'あり' : 'なし'}" /></td>
                                <td>
                                    <a href="AdminManageTabyouinServlet?action=showTelChangeForm&tabyouinId=<c:out value='${tb.tabyouinId}'/>" class="action-button">電話番号変更</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p style="text-align:center;">
                    <c:choose>
                        <c:when test="${isSearchResult}">検索条件に一致する他病院は見つかりませんでした。</c:when>
                        <c:otherwise>登録されている他病院はありません。</c:otherwise>
                    </c:choose>
                </p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>