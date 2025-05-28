<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.EmployeeBean" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザーパスワード変更</title>
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
    input.input-error { border-color: red; } /* JSバリデーションエラー時のスタイル */
</style>
</head>
<body>
    <div class="container">
        <h1>ユーザーパスワード変更</h1>

        <c:if test="${not empty userToChange}">
            <div class="info-bar">
                <strong>対象ユーザー:</strong> <c:out value="${userToChange.emplname} ${userToChange.empfname}"/>
                (ID: <c:out value="${userToChange.empid}"/>,
                ロール:
                <c:choose>
                    <c:when test="${userToChange.role == 3}">管理者</c:when>
                    <c:when test="${userToChange.role == 1}">受付</c:when>
                    <c:when test="${userToChange.role == 2}">医師</c:when>
                    <c:otherwise>不明</c:otherwise>
                </c:choose>
                )
            </div>
        </c:if>

        <%-- サーブレットからのメッセージ表示 --%>
        <c:if test="${not empty errorMessage}"><div class="message error-message-server"><c:out value="${errorMessage}"/></div></c:if>
        <c:if test="${not empty successMessage}"><div class="message success-message"><c:out value="${successMessage}"/></div></c:if>

        <c:if test="${not empty userToChange}">
            <form id="changePasswordForm" action="AdminChangeUserPasswordServlet" method="post">
                <input type="hidden" name="empIdToChange" value="<c:out value='${userToChange.empid}'/>">
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
        </c:if>
        <c:if test="${empty userToChange && empty errorMessage && empty successMessage}">
             <p>パスワードを変更するユーザーが選択されていません。</p>
        </c:if>

        <p style="margin-top:20px;">
            <a href="ReturnToMenuServlet">管理者メニューへ戻る</a>
            <%-- 適切なリストに戻るためのロジックを検討 (例: userToChange.empRoleに応じて分岐) --%>
            <%--
            <c:choose>
                <c:when test="${userToChange.empRole == 0}">
                    <a href="AdminListAdministratorsServlet" class="back-link">管理者一覧へ戻る</a>
                </c:when>
                <c:otherwise>
                    <a href="AdminListStaffServlet" class="back-link">従業員一覧へ戻る</a>
                </c:otherwise>
            </c:choose>
            --%>
        </p>
    </div>

<script>
    const changePasswordForm = document.getElementById('changePasswordForm');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const newPasswordError = document.getElementById('newPasswordError');
    const confirmPasswordError = document.getElementById('confirmPasswordError');

    // 汎用的な必須チェック関数 (入力があればエラーを消す)
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

    // 新しいパスワードのバリデーション
    function validateNewPassword() {
        return validateRequired(newPasswordInput, newPasswordError, "新しいパスワードを入力してください。");
    }

    // 確認用パスワードのバリデーション (一致チェックも含む)
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

    if(changePasswordForm) { // フォームが存在する場合のみイベントリスナーを設定
        newPasswordInput.onblur = validateNewPassword;
        newPasswordInput.oninput = () => { // 入力中にもエラーをクリアする（任意）
             if(newPasswordError.style.display === 'block') validateNewPassword();
             // 新しいパスワードが変更されたら、確認用パスワードの一致も再チェック
             if(confirmPasswordInput.value.trim() !== '') validateConfirmPassword();
        };

        confirmPasswordInput.onblur = validateConfirmPassword;
        confirmPasswordInput.oninput = () => { // 入力中にもエラーをクリアする（任意）
            if(confirmPasswordError.style.display === 'block') validateConfirmPassword();
        };

        changePasswordForm.onsubmit = function(event) {
            // フォーム送信時に全てのバリデーションを再度実行
            const isNewPasswordValid = validateNewPassword();
            const isConfirmPasswordValid = validateConfirmPassword(); // これで一致チェックも行われる

            if (!isNewPasswordValid || !isConfirmPasswordValid) {
                event.preventDefault(); // バリデーションエラーがあれば送信を中止
                // alert("入力内容にエラーがあります。確認してください。"); // 個別のエラーメッセージが表示されるので不要かも
                return false;
            }
            // パスワードが空欄の場合のテストケース  は validateRequired でカバー
            // パスワードが一致しない場合は validateConfirmPassword でカバー
            return true; // バリデーションOKなら送信
        };
    }
</script>

</body>
</html>