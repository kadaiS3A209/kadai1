<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.EmployeeBean" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員登録</title>
<style>
    body { font-family: sans-serif; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="password"], select {
        width: 300px;
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }
    .error-message { color: red; font-size: 0.9em; margin-top: 3px; display: none; /* 初期状態は非表示 */}
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .button:hover { background-color: #0056b3; }
</style>
</head>
<body>

    <h1>従業員登録</h1>

    <%-- エラーメッセージ表示領域 --%>
    <%
        String formError = (String) request.getAttribute("formError");
        if (formError != null) {
    %>
        <p style="color:red;"><%= formError %></p>
    <%
        }
        // 確認画面から戻ってきた場合の値を取得
        EmployeeBean prevInput = (EmployeeBean) session.getAttribute("tempEmployee");
        String prevLname = (prevInput != null && prevInput.getEmplname() != null) ? prevInput.getEmplname() : "";
        String prevFname = (prevInput != null && prevInput.getEmpfname() != null) ? prevInput.getEmpfname() : "";
        String prevRole = (prevInput != null && prevInput.getRole() != 0) ? String.valueOf(prevInput.getRole()) : "";
        // パスワードは再入力させる
    %>

    <form id="registerForm" action="AdminRegisterEmployeeServlet" method="post" onsubmit="return validateForm()">
        <%-- この隠しフィールドで、入力画面からの送信であることを示す --%>
        <input type="hidden" name="action" value="confirm">

        <div class="form-group">
            <label for="emplname">姓:</label>
            <input type="text" id="emplname" name="emplname" value="<%= prevLname %>" required>
            <span id="lnameError" class="error-message">姓を入力してください。</span>
        </div>

        <div class="form-group">
            <label for="empfname">名:</label>
            <input type="text" id="empfname" name="empfname" value="<%= prevFname %>" required>
            <span id="fnameError" class="error-message">名を入力してください。</span>
        </div>

        <div class="form-group">
            <label for="emprole">ロール:</label>
            <select id="emprole" name="emprole" required>
                <option value="">選択してください</option>
                <%-- valueはDBのemproleに保存する整数値に合わせる --%>
                <option value="1" <%= "1".equals(prevRole) ? "selected" : "" %>>受付</option>
                <option value="2" <%= "2".equals(prevRole) ? "selected" : "" %>>医師</option>
                <option value="3" <%= "3".equals(prevRole) ? "selected" : "" %>>管理者</option>
                <%-- 他のロールがあれば追加 --%>
            </select>
            <span id="roleError" class="error-message">ロールを選択してください。</span>
        </div>

        <div class="form-group">
            <label for="password">パスワード:</label>
            <input type="password" id="password" name="password" required>
            <span id="passwordError" class="error-message">パスワードを入力してください。</span>
        </div>

        <div class="form-group">
            <label for="passwordConfirm">パスワード (確認):</label>
            <input type="password" id="passwordConfirm" name="passwordConfirm" required>
            <span id="passwordConfirmError" class="error-message">パスワードが一致しません。</span>
        </div>

        <button type="submit" class="button">確認画面へ</button>
    </form>

<script>
    const form = document.getElementById('registerForm');
    const lnameInput = document.getElementById('emplname');
    const fnameInput = document.getElementById('empfname');
    const roleSelect = document.getElementById('emprole');
    const passwordInput = document.getElementById('password');
    const passwordConfirmInput = document.getElementById('passwordConfirm');

    // エラーメッセージ表示用Span
    const lnameError = document.getElementById('lnameError');
    const fnameError = document.getElementById('fnameError');
    const roleError = document.getElementById('roleError');
    const passwordError = document.getElementById('passwordError');
    const passwordConfirmError = document.getElementById('passwordConfirmError');

    // リアルタイムチェックの簡略版 (onblur: フォーカスが外れた時)
    lnameInput.onblur = () => validateField(lnameInput, lnameError, "姓を入力してください。");
    fnameInput.onblur = () => validateField(fnameInput, fnameError, "名を入力してください。");
    roleSelect.onblur = () => validateField(roleSelect, roleError, "ロールを選択してください。");
    passwordInput.onblur = () => {
        validateField(passwordInput, passwordError, "パスワードを入力してください。");
        validatePasswordConfirm(); // パスワード入力が変わったら確認欄も再チェック
    };
    passwordConfirmInput.onblur = validatePasswordConfirm;
    passwordConfirmInput.onkeyup = validatePasswordConfirm; // 入力中もチェック

    function validateField(inputElement, errorElement, message) {
        if (!inputElement.value.trim()) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
            return false;
        } else {
            errorElement.style.display = 'none';
            return true;
        }
    }

    function validatePasswordConfirm() {
        if (passwordInput.value !== passwordConfirmInput.value) {
            passwordConfirmError.textContent = "パスワードが一致しません。";
            passwordConfirmError.style.display = 'block';
            return false;
        } else if (!passwordConfirmInput.value) {
             passwordConfirmError.textContent = "確認用パスワードを入力してください。";
             passwordConfirmError.style.display = 'block';
             return false;
        }
         else {
            passwordConfirmError.style.display = 'none';
            return true;
        }
    }

    // フォーム送信時の最終チェック
    function validateForm() {
        let isValid = true;
        isValid &= validateField(lnameInput, lnameError, "姓を入力してください。");
        isValid &= validateField(fnameInput, fnameError, "名を入力してください。");
        isValid &= validateField(roleSelect, roleError, "ロールを選択してください。");
        isValid &= validateField(passwordInput, passwordError, "パスワードを入力してください。");
        //isValid &= validateField(passwordConfirmInput, passwordConfirmError, "確認用パスワードを入力してください。"); // onblurで必須チェック済み前提
        isValid &= validatePasswordConfirm();

        return !!isValid; // booleanに変換して返す
    }
</script>

</body>
</html>