<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員(受付・医師)一覧</title>
<style> /* 既存のスタイルシートや共通スタイルを適用 */
    body { font-family: sans-serif; } .container { width: 90%; margin: 20px auto; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    
    .action-button {
        display: inline-block;
        padding: 5px 10px;
        margin-right: 5px;
        color: white !important;
        text-decoration: none;
        border-radius: 3px;
        border: none;
        cursor: pointer;
        font-size: 0.9em;
        text-align: center;
    }
    .button-change-name { background-color: #17a2b8; } /* 氏名変更ボタンの色 (例: Info色) */
    .button-change-name:hover { background-color: #138496; }
    .button-change-password { background-color: #ffc107; color: black !important; } /* パスワード変更ボタンの色 (例: Warning色) */
    .button-change-password:hover { background-color: #e0a800; }
    .search-form { margin-bottom: 20px; padding: 10px; background-color:#f9f9f9; border:1px solid #eee; }
    .button { padding: 5px 10px; background-color: #007bff; color:white; text-decoration:none; border-radius:3px; border:none; cursor:pointer; }
    .button-bar { margin-bottom:15px;}
</style>
</head>
<body>
    <div class="container">
        <h1>従業員(受付・医師)一覧・検索</h1>

        <div class="button-bar">
             <a href="AdminRegisterEmployeeServlet" class="button">新規従業員登録</a>
             <a href="ReturnToMenuServlet" class="button">管理者メニューへ戻る</a>
        </div>

        <form class="search-form" action="AdminListStaffServlet" method="post">
            従業員ID検索: <input type="text" name="searchEmpId" value="<c:out value='${searchedEmpId}'/>">
            <button type="submit" class="button">検索</button>
            <a href="AdminListStaffServlet" class="button">全件表示/クリア</a>
        </form>

        <c:if test="${not empty staffList}">
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
                    <c:forEach var="staff" items="${staffList}">
                        <tr>
                            <td><c:out value="${staff.empid}" /></td>
                            <td><c:out value="${staff.emplname} ${staff.empfname}" /></td>
                            <td>
                                <c:if test="${staff.role == 1}">受付</c:if>
                                <c:if test="${staff.role == 2}">医師</c:if>
                            </td>
                            <td>
                                <%-- ★★★ 氏名変更とパスワード変更ボタンを追加 ★★★ --%>
                                <a href="AdminManageEmployeesServlet?action=showNameChangeForm&empId=<c:out value='${staff.empid}'/>&source=staffList" class="action-button button-change-name">氏名変更</a>
+                               <a href="AdminChangeUserPasswordServlet?action=showForm&empId=<c:out value='${staff.empid}'/>&source=staffList" class="action-button button-change-password">パスワード変更</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty staffList}">
            <p>
                <c:choose>
                    <c:when test="${not empty searchedEmpId}">検索条件に一致する従業員(受付・医師)は見つかりませんでした。</c:when>
                    <c:otherwise>登録されている従業員(受付・医師)はいません。</c:otherwise>
                </c:choose>
            </p>
        </c:if>
    </div>
</body>
</html>