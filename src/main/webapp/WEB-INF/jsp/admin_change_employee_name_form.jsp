<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員氏名変更</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;}
    .form-group { margin-bottom: 15px; } label { display: block; margin-bottom: 5px; }
    input[type="text"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right:10px;}
    .message { margin:10px 0; padding:10px; border-radius:4px; }
    .error-message { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb;}
    .info-bar { padding: 10px; background-color: #f0f0f0; margin-bottom:15px; border-radius:4px; }
</style>
</head>
<body>
    <div class="container">
        <h1>従業員氏名変更</h1>

        <c:if test="${not empty employeeToChange}">
            <div class="info-bar">
                <strong>対象従業員ID:</strong> <c:out value="${employeeToChange.empid}"/>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage_nameChange}"><div class="message error-message"><c:out value="${errorMessage_nameChange}"/></div></c:if>
        <%-- 成功メッセージは一覧画面に表示するため、ここでは通常不要 --%>

        <c:if test="${not empty employeeToChange}">
            <form action="AdminManageEmployeesServlet" method="post">
                <input type="hidden" name="action" value="updateName">
                <input type="hidden" name="empIdToChange" value="<c:out value='${employeeToChange.empid}'/>">
                <input type="hidden" name="sourcePage" value="<c:out value='${sourcePage}'/>"> <%-- ★これを追加 --%>
                <div class="form-group">
                    <label for="newLname">新しい姓:</label>
                    <input type="text" id="newLname" name="newLname" value="<c:out value='${employeeToChange.emplname}'/>" required>
                </div>
                <div class="form-group">
                    <label for="newFname">新しい名:</label>
                    <input type="text" id="newFname" name="newFname" value="<c:out value='${employeeToChange.empfname}'/>" required>
                </div>
                <button type="submit" class="button">氏名を変更する</button>
                <c:choose>
                        <c:when test="${sourcePage == 'staffList'}">
                            <a href="AdminListStaffServlet" class="button" style="background-color:#6c757d;">スタッフ一覧へ戻る</a>
                        </c:when>
                        <c:when test="${sourcePage == 'adminList'}">
                            <a href="AdminListAdministratorsServlet" class="button" style="background-color:#6c757d;">管理者一覧へ戻る</a>
                        </c:when>
                        <c:when test="${sourcePage == 'allList'}">
                            <a href="AdminManageEmployeesServlet" class="button" style="background-color:#6c757d;">全従業員一覧へ戻る</a>
                        </c:when>
                        <c:otherwise> <%-- sourcePageが不明な場合のデフォルト --%>
                            <a href="AdminManageEmployeesServlet" class="button" style="background-color:#6c757d;">従業員一覧へ戻る</a>
                        </c:otherwise>
                </c:choose>
            </form>
        </c:if>
        <c:if test="${empty employeeToChange && empty errorMessage_nameChange}">
             <p>変更対象の従業員が選択されていません。</p>
             <c:choose>
                    <c:when test="${sourcePage == 'staffList'}">
                        <p><a href="AdminListStaffServlet">スタッフ一覧へ戻る</a></p>
                    </c:when>
                    <c:when test="${sourcePage == 'adminList'}">
                         <p><a href="AdminListAdministratorsServlet">管理者一覧へ戻る</a></p>
                    </c:when>
                    <c:when test="${sourcePage == 'allList'}">
                         <p><a href="AdminManageEmployeesServlet">全従業員一覧へ戻る</a></p>
                    </c:when>
                    <c:otherwise>
                        <p><a href="AdminManageEmployeesServlet">従業員一覧へ戻る</a></p>
                    </c:otherwise>
             </c:choose>
        </c:if>
    </div>
</body>
</html>