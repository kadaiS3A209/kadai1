<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.EmployeeBean" %> <%-- パスを合わせる --%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>パスワード変更</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;}
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="password"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .button:hover { background-color: #0056b3; }
    .message { margin-bottom: 15px; padding:10px; }
    .error-message-js { color: red; font-size: 0.9em; margin-top: 3px; display: none; /* JSエラーメッセージ用 */ }
    .error-message-server { color: red; border: 1px solid red; background-color: #ffebeb; /* サーブレットからのエラーメッセージ用 */ }
    .success-message { color: green; border: 1px solid green; background-color: #e6ffe6;}
    .info-bar { padding: 10px; background-color: #f0f0f0; margin-bottom:15px; border-radius:4px; }
    .back-link { display:inline-block; margin-top: 20px; margin-right: 10px; }
    input.input-error { border-color: red; }
</style>
</head>
<body>
    <div class="container">
        <h1>パスワード変更</h1>

        <%-- ログイン中のユーザー情報を表示（任意） --%>
        <c:if test="${not empty sessionScope.loggedInUser}">
            <div class="info-bar">
                <strong>ユーザー:</strong> <c:out value="${sessionScope.loggedInUser.emplname} ${sessionScope.loggedInUser.empfname}"/>
                (ID: <c:out value="${sessionScope.loggedInUser.empid}"/>)
            </div>
        </c:if>

        <%-- サーブレットからのメッセージ表示 --%>
        <c:if test="${not empty requestScope.errorMessage}"><div class="message error-message-server"><c:out value="${requestScope.errorMessage}"/></div></c:if>
        <c:if test="${not empty requestScope.successMessage}"><div class="message success-message"><c:out value="${requestScope.successMessage}"/></div></c:if>

        <form id="changeOwnPasswordForm" action="EmployeeChangeOwnPasswordServlet" method="post">
        	<input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
            <div class="form-group">
                <label for="newPassword">新しいパスワード:</label>
                <input type="password" id="newPassword" name="newPassword" required>
                <span id="newPasswordError" class="error-message-js"></span>
            </div>
            <div class="form-group">
                <label for="confirmPassword">新しいパスワード (確認):</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
                <span id="confirmPasswordError" class="error-message-js"></span>
            </div>
            <button type="submit" class="button">パスワードを変更する</button>
        </form>

        <p style="margin-top:20px;">
            <a href="ReturnToMenuServlet" class="back-link">メニューへ戻る</a>
        </p>
    </div>

<script>
    const changePasswordForm = document.getElementById('changeOwnPasswordForm');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const newPasswordError = document.getElementById('newPasswordError');
    const confirmPasswordError = document.getElementById('confirmPasswordError');

    function validateRequired(inputEl, errorEl, message) {
        if (!inputEl.value.trim()) {
            errorEl.textContent = message;
            errorEl.style.display = 'block';
            inputEl.classList.add('input-error');
            return false;
        }
        errorEl.style.display = 'none';
        inputEl.classList.remove('input-error');
        return true;
    }

    function validateNewPassword() {
        // 基本設計書E103には最低文字数などの指定はないが、必要ならここに追加
        return validateRequired(newPasswordInput, newPasswordError, "新しいパスワードを入力してください。");
    }

    function validateConfirmPassword() {
        if (!validateRequired(confirmPasswordInput, confirmPasswordError, "確認用パスワードを入力してください。")) {
            return false;
        }
        if (newPasswordInput.value !== confirmPasswordInput.value) {
            confirmPasswordError.textContent = "新しいパスワードと確認用パスワードが一致しません。";
            confirmPasswordError.style.display = 'block';
            confirmPasswordInput.classList.add('input-error');
            return false;
        }
        confirmPasswordError.style.display = 'none';
        confirmPasswordInput.classList.remove('input-error');
        return true;
    }

    if(changePasswordForm) {
        newPasswordInput.onblur = validateNewPassword;
        newPasswordInput.oninput = () => {
             if(newPasswordError.style.display === 'block') validateNewPassword();
             if(confirmPasswordInput.value.trim() !== '') validateConfirmPassword();
        };

        confirmPasswordInput.onblur = validateConfirmPassword;
        confirmPasswordInput.oninput = () => {
            if(confirmPasswordError.style.display === 'block') validateConfirmPassword();
        };

        changePasswordForm.onsubmit = function(event) {
            const isNewPasswordValid = validateNewPassword();
            const isConfirmPasswordValid = validateConfirmPassword();

            if (!isNewPasswordValid || !isConfirmPasswordValid) {
                event.preventDefault();
                return false;
            }
            // テストケース: 両方空欄、片方空欄 は validateRequired でカバー
            // テストケース: 不一致 は validateConfirmPassword でカバー
            return true;
        };
    }
</script>

</body>
</html>
