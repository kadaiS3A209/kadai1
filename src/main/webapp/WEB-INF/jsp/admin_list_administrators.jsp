<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>管理者一覧</title>
<style> /* 共通スタイルを適用 */
    body { font-family: sans-serif; } .container { width: 90%; margin: 20px auto; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    .search-form { margin-bottom: 20px; padding: 10px; background-color:#f9f9f9; border:1px solid #eee; }
    .button, .action-button { padding: 5px 10px; background-color: #007bff; color:white; text-decoration:none; border-radius:3px; border:none; cursor:pointer; }
    .button-bar { margin-bottom:15px;}
</style>
</head>
<body>
    <div class="container">
        <h1>管理者一覧・検索</h1>

        <div class="button-bar">
             <%-- 管理者自身をこの画面から新規登録することは通常ないため、リンクはメニュー戻りのみなど --%>
             <a href="ReturnToMenuServlet" class="button">管理者メニューへ戻る</a>
        </div>

        <form class="search-form" action="AdminListAdministratorsServlet" method="post">
            管理者ID検索: <input type="text" name="searchAdminId" value="<c:out value='${searchedAdminId}'/>">
            <button type="submit" class="button">検索</button>
            <a href="AdminListAdministratorsServlet" class="button">全件表示/クリア</a>
        </form>

        <c:if test="${not empty adminList}">
            <table>
                <thead>
                    <tr>
                        <th>管理者ID</th>
                        <th>氏名</th>
                        <th>ロール</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="admin" items="${adminList}">
                        <tr>
                            <td><c:out value="${admin.empid}" /></td>
                            <td><c:out value="${admin.emplname} ${admin.empfname}" /></td>
                            <td>管理者</td>
                            <td>
                                <a href="AdminChangeUserPasswordServlet?action=showForm&empId=<c:out value='${admin.empid}'/>" class="action-button">パスワード変更</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty adminList}">
            <p>
                <c:choose>
                    <c:when test="${not empty searchedAdminId}">検索条件に一致する管理者は見つかりませんでした。</c:when>
                    <c:otherwise>登録されている管理者はいません。</c:otherwise>
                </c:choose>
            </p>
        </c:if>
    </div>
</body>
</html>