<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員一覧・検索</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 90%; margin: 20px auto; }
    h1 { text-align: center; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    .message { margin-bottom: 15px; padding:10px; }
    .error-message { color: red; border: 1px solid red; background-color: #ffebeb;}
    .no-data-message { text-align: center; color: #777; padding: 20px; }
    .button-bar { margin-bottom: 20px; }
    .button, .action-button { padding: 8px 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; text-decoration: none; margin-right: 5px; cursor:pointer; font-size: 0.9em; }
    .button:hover, .action-button:hover { background-color: #0056b3; }
    .search-form { margin-bottom: 20px; padding: 15px; border: 1px solid #eee; background-color: #f9f9f9; display: flex; align-items: center; gap: 10px; }
    .search-form label { margin-right: 5px; }
    .search-form input[type="text"] { padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
</style>
</head>
<body>
    <div class="container">
        <h1>従業員一覧・検索</h1>

        <div class="button-bar">
            <a href="AdminRegisterEmployeeServlet" class="button">新規従業員登録</a>
            <a href="ReturnToMenuServlet" class="button">管理者メニューへ戻る</a>
        </div>

        <form class="search-form" action="AdminListEmployeesServlet" method="post">
            <label for="searchEmpId">従業員IDで検索:</label>
            <input type="text" id="searchEmpId" name="searchEmpId" value="<c:out value='${searchedEmpId}'/>" placeholder="従業員IDを入力">
            <button type="submit" class="button">検索</button>
            <a href="AdminListEmployeesServlet" class="button">全件表示/クリア</a>
        </form>

        <c:if test="${not empty errorMessage}">
            <div class="message error-message"><c:out value="${errorMessage}" /></div>
        </c:if>

        <c:choose>
            <c:when test="${not empty employeeList}">
                <table>
                    <thead>
                        <tr>
                            <th>従業員ID</th>
                            <th>氏名</th>
                            <th>ロール</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="emp" items="${employeeList}">
                            <tr>
                                <td><c:out value="${emp.empId}" /></td>
                                <td><c:out value="${emp.empLname} ${emp.empFname}" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${emp.empRole == 3}">管理者</c:when>
                                        <c:when test="${emp.empRole == 1}">受付</c:when>
                                        <c:when test="${emp.empRole == 2}">医師</c:when>
                                        <c:otherwise>不明</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="AdminChangeEmpPasswordServlet?action=showForm&empId=<c:out value='${emp.empId}'/>" class="action-button">パスワード変更</a>
                                    <%-- ここに氏名変更など他の操作へのリンクも追加可能 --%>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <c:if test="${empty errorMessage}"> <%-- エラーメッセージが無く、リストが空の場合 --%>
                    <p class="no-data-message">
                        <c:if test="${not empty searchedEmpId}">検索条件に一致する従業員は見つかりませんでした。</c:if>
                        <c:if test="${empty searchedEmpId}">登録されている従業員はいません。</c:if>
                    </p>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>