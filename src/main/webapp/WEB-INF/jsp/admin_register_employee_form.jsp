<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLのCoreライブラリを使用するための宣言を追加 --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%-- jsp:useBeanで使用するため、model.EmployeeBean の import は残しても良いですが、
     EL式で直接アクセスする場合は必須ではありません --%>
<%@ page import="model.EmployeeBean" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>従業員登録</title>
<style>
    body { font-family: sans-serif; }
    .form-container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="password"], select {
        width: 300px;
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }
    .error-message { color: red; font-size: 0.9em; margin-top: 3px; display: none; }
    .error-message-server { color: red; background-color: #ffebeb; border:1px solid #ffcdd2; padding:8px; border-radius:4px; margin-bottom:10px;}
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .button:hover { background-color: #0056b3; }
    input.input-error { border-color: red; }
</style>
</head>
<body>
    <div class="form-container">
        <h1>従業員登録</h1>

        <%-- サーブレットからのエラーメッセージ表示 --%>
        <c:if test="${not empty requestScope.formError}">
            <p class="error-message-server"><c:out value="${requestScope.formError}"/></p>
        </c:if>

        <%--
            フォームに表示する値を持つBeanを決定するロジック。
            優先順位：
            1. リクエストスコープの "prevEmployeeInput" (バリデーションエラーで戻ってきた場合)
            2. セッションスコープの "tempEmployee" (確認画面から「修正」で戻ってきた場合)
            3. どちらもなければ、空のBean (新規作成時)
        --%>
        <c:choose>
            <c:when test="${not empty requestScope.prevEmployeeInput}">
                <c:set var="formBean" value="${requestScope.prevEmployeeInput}" scope="page" />
            </c:when>
            <c:when test="${not empty sessionScope.tempEmployee}">
                <c:set var="formBean" value="${sessionScope.tempEmployee}" scope="page" />
            </c:when>
            <c:otherwise>
                <jsp:useBean id="formBean" class="model.EmployeeBean" scope="page" />
            </c:otherwise>
        </c:choose>

        <form id="registerForm" action="AdminRegisterEmployeeServlet" method="post" onsubmit="return validateForm()">
            <%-- CSRF対策トークン --%>
            <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
            <input type="hidden" name="action" value="confirm">

            <div class="form-group">
                <label for="empid">従業員ID (8桁以内):</label>
                <input type="text" id="empid" name="empid"  value="<c:out value='${formBean.empid}'/>" required maxlength="8">
                <span id="empidError" class="error-message"></span>
            </div>

            <div class="form-group">
                <label for="emplname">姓:</label>
                <input type="text" id="emplname" name="emplname" value="<c:out value='${formBean.emplname}'/>" required>
                <span id="lnameError" class="error-message"></span>
            </div>

            <div class="form-group">
                <label for="empfname">名:</label>
                <input type="text" id="empfname" name="empfname" value="<c:out value='${formBean.empfname}'/>" required>
                <span id="fnameError" class="error-message"></span>
            </div>

            <div class="form-group">
                <label for="emprole">ロール:</label>
                <select id="emprole" name="emprole" required>
                    <option value="">選択してください</option>
                    <option value="1" ${formBean.role == 1 ? 'selected' : ''}>受付</option>
                    <option value="2" ${formBean.role == 2 ? 'selected' : ''}>医師</option>
                    <option value="3" ${formBean.role == 3 ? 'selected' : ''}>管理者</option>
                </select>
                <span id="roleError" class="error-message"></span>
            </div>

            <div class="form-group">
                <label for="password">パスワード:</label>
                <input type="password" id="password" name="password" required>
                <span id="passwordError" class="error-message"></span>
            </div>

            <div class="form-group">
                <label for="passwordConfirm">パスワード (確認):</label>
                <input type="password" id="passwordConfirm" name="passwordConfirm" required>
                <span id="passwordConfirmError" class="error-message"></span>
            </div>

            <button type="submit" class="button">確認画面へ</button>
        </form>
        <p style="margin-top:15px;"><a href="ReturnToMenuServlet">管理者メニューへ戻る</a></p>
    </div>

<script>
    const form = document.getElementById('registerForm');
    const lnameInput = document.getElementById('emplname');
    const fnameInput = document.getElementById('empfname');
    const roleSelect = document.getElementById('emprole');
    const passwordInput = document.getElementById('password');
    const passwordConfirmInput = document.getElementById('passwordConfirm');
    const empidInput = document.getElementById('empid');

    // エラーメッセージ表示用Span
    const lnameError = document.getElementById('lnameError');
    const fnameError = document.getElementById('fnameError');
    const roleError = document.getElementById('roleError');
    const passwordError = document.getElementById('passwordError');
    const passwordConfirmError = document.getElementById('passwordConfirmError');
    const empidError = document.getElementById('empidError');

    // リアルタイムチェックの簡略版 (onblur: フォーカスが外れた時)
    lnameInput.onblur = () => validateField(lnameInput, lnameError, "姓を入力してください。");
    fnameInput.onblur = () => validateField(fnameInput, fnameError, "名を入力してください。");
    roleSelect.onblur = () => validateField(roleSelect, roleError, "ロールを選択してください。");
    empidInput.onblur = () => validateEmpId(empidInput, empidError,"従業員IDを入力してください"); //形式チェックも行うなら
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

    function validateEmpId(){
		//const empidValue = ;
		if(!empidInput.value.trim()){
			empidError.textContent = "従業員IDを入力してください";
			empidError.style.display = 'block';
			return false;

		}//ここにさらにelse if で詳細な形式チェックを追加可能
		else{
			empidError.style.display = 'none';
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
        isValid &= validateEmpId(empidInput, empidError,"従業員IDを入力してください");//empidのバリデーションを追加
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