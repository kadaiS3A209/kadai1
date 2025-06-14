<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLのCoreライブラリを使用するための宣言を追加 --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.ShiiregyoshaBean" %> <%-- jsp:useBeanで使用 --%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先登録</title>
<style>
    body { font-family: sans-serif; }
    .form-container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="tel"], input[type="number"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .error-message { color: red; font-size: 0.9em; margin-top: 3px; display: none; }
    .error-message-server { color: red; background-color: #ffebeb; border: 1px solid #ffcdd2; padding: 8px; border-radius: 4px; margin-bottom: 10px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .button:hover { background-color: #0056b3; }
    input.input-error { border-color: red; }
</style>
</head>
<body>
    <div class="form-container">
        <h1>仕入先登録</h1>

        <%-- サーブレットからのエラーメッセージ表示 --%>
        <c:if test="${not empty requestScope.formError}">
            <p class="error-message-server">${requestScope.formError}</p>
        </c:if>

        <%--
            フォームに表示する値を持つBeanを決定するロジック。
            優先順位：
            1. リクエストスコープの "prevSupplierInput" (バリデーションエラーで戻ってきた場合)
            2. セッションスコープの "tempSupplier" (確認画面から「修正」で戻ってきた場合)
            3. どちらもなければ、空のBean (新規作成時)
        --%>
        <c:choose>
            <c:when test="${not empty requestScope.prevSupplierInput}">
                <c:set var="formBean" value="${requestScope.prevSupplierInput}" scope="page" />
            </c:when>
            <c:when test="${not empty sessionScope.tempSupplier}">
                <c:set var="formBean" value="${sessionScope.tempSupplier}" scope="page" />
            </c:when>
            <c:otherwise>
                <jsp:useBean id="formBean" class="model.ShiiregyoshaBean" scope="page" />
            </c:otherwise>
        </c:choose>


        <form id="addSupplierForm" action="AdminAddSupplierServlet" method="post" onsubmit="return validateSupplierForm()">
            <%-- CSRF対策トークン --%>
            <input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
            <input type="hidden" name="action" value="confirm">

            <div class="form-group">
                <label for="shiireid">仕入先ID (8桁以内):</label>
                <input type="text" id="shiireid" name="shiireid" value="<c:out value='${formBean.shiireId}'/>" required maxlength="8">
                <span id="shiireidError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="shiiremei">仕入先名:</label>
                <input type="text" id="shiiremei" name="shiiremei" value="<c:out value='${formBean.shiireMei}'/>" required>
                <span id="shiiremeiError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="shiireaddress">住所:</label>
                <input type="text" id="shiireaddress" name="shiireaddress" value="<c:out value='${formBean.shiireAddress}'/>" required>
                <span id="shiireaddressError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="shiiretel">電話番号:</label>
                <input type="tel" id="shiiretel" name="shiiretel" value="<c:out value='${formBean.shiireTel}'/>" required placeholder="例: 03-1234-5678">
                <span id="shiiretelError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="shihonkin">資本金 (円):</label>
                <input type="text" id="shihonkin" name="shihonkin" value="<c:if test='${formBean.shihonkin != 0}'><c:out value='${formBean.shihonkin}'/></c:if>" required placeholder="例: 1000000 (カンマなし)">
                <span id="shihonkinError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="nouki">納期 (日数):</label>
                <input type="text" id="nouki" name="nouki" value="<c:if test='${formBean.nouki != 0}'><c:out value='${formBean.nouki}'/></c:if>" required placeholder="例: 7">
                <span id="noukiError" class="error-message"></span>
            </div>

            <button type="submit" class="button">確認画面へ</button>
        </form>
        <p style="margin-top:15px;"><a href="ReturnToMenuServlet">管理者メニューへ戻る</a></p>
    </div>

<script>
    // フォーム要素の取得
    const shiireidInput = document.getElementById('shiireid');
    const shiiremeiInput = document.getElementById('shiiremei');
    const shiireaddressInput = document.getElementById('shiireaddress');
    const shiiretelInput = document.getElementById('shiiretel'); // ★電話番号の入力フィールド
    const shihonkinInput = document.getElementById('shihonkin');
    const noukiInput = document.getElementById('nouki');

    // エラーメッセージ要素の取得
    const shiireidError = document.getElementById('shiireidError');
    const shiiremeiError = document.getElementById('shiiremeiError');
    const shiireaddressError = document.getElementById('shiireaddressError');
    const shiiretelError = document.getElementById('shiiretelError'); // ★電話番号のエラー表示用span
    const shihonkinError = document.getElementById('shihonkinError');
    const noukiError = document.getElementById('noukiError');

    // 汎用的な必須チェック関数 (変更なし)
    function validateRequired(inputEl, errorEl, message) {
        if (!inputEl.value.trim()) {
            errorEl.textContent = message;
            errorEl.style.display = 'block';
            inputEl.classList.add('input-error'); // エラー時に枠線を赤くするスタイル用
            return false;
        }
        errorEl.style.display = 'none';
        inputEl.classList.remove('input-error');
        return true;
    }

    // ▼▼▼ ここからが新しい電話番号バリデーション関数 ▼▼▼
    /**
     * 仕入先電話番号のバリデーションチェック
     */
    function validateShiireTel() {
        // 必須チェック
        if (!validateRequired(shiiretelInput, shiiretelError, "電話番号を入力してください。")) {
            return false;
        }

        const telValue = shiiretelInput.value;
        let errorMessage = null;
        const trimmedInput = telValue.trim();

        // 2. 許可されていない文字が含まれていないかチェック
        if (trimmedInput.match(/[^0-9()-]/)) {
            errorMessage = "電話番号には数字、ハイフン、括弧以外の文字は使用できません。";
        } else {
            // 3. 数字のみを抽出し、桁数と先頭文字をチェック
            const digitsOnly = trimmedInput.replace(/[^0-9]/g, "");

            if (digitsOnly.length < 10) {
                errorMessage = "電話番号の桁数が不足しています。数字のみで10桁以上必要です。";
            } else if (digitsOnly.length > 11){
                errorMessage = "電話番号の桁数が超過しています。数字のみで11桁以下です。";
            } else if (!digitsOnly.startsWith("0")) {
                errorMessage = "電話番号は0から始まる必要があります。";
            } else {
                 // 4. 書式のチェック
                 const hyphenGeneralPattern = /^0[1-9]\d{0,3}-\d{1,4}-\d{4}$/;
                 const hyphenSpecialPattern = /^(0120|0800|0570|0990)-\d{3}-\d{3}$/;
                 const parenPattern = /^0[1-9]\d{0,3}\(\d{1,4}\)\d{4}$/;

                 if (trimmedInput.includes('-') || trimmedInput.includes('(') || trimmedInput.includes(')')) {
                     if (!hyphenGeneralPattern.test(trimmedInput) &&
                         !hyphenSpecialPattern.test(trimmedInput) &&
                         !parenPattern.test(trimmedInput)) {
                         errorMessage = "電話番号の形式が正しくありません。(例: 03-1234-5678)";
                     }
                 }
            }
        }

        // 5. エラー判定とメッセージ表示
        if (errorMessage) {
            shiiretelError.textContent = errorMessage;
            shiiretelError.style.display = 'block';
            shiiretelInput.classList.add('input-error');
            return false;
        } else {
            shiiretelError.style.display = 'none';
            shiiretelInput.classList.remove('input-error');
            return true;
        }
    }
    // ▲▲▲ 新しい電話番号バリデーション関数の終わり ▲▲▲

    // --- 他のフィールドのバリデーション関数 ---
    function validateShiireId() {
        if(!validateRequired(shiireidInput, shiireidError, "仕入先IDを入力してください。")) return false;
        if (shiireidInput.value.trim().length > 8) {
            shiireidError.textContent = "仕入先IDは8文字以内で入力してください。";
            shiireidError.style.display = 'block';
            shiireidInput.classList.add('input-error');
            return false;
        }
        shiireidError.style.display = 'none';
        shiireidInput.classList.remove('input-error');
        return true;
    }
    function validateShiireMei() {
        return validateRequired(shiiremeiInput, shiiremeiError, "仕入先名を入力してください。");
    }
    function validateShiireAddress() {
        return validateRequired(shiireaddressInput, shiireaddressError, "住所を入力してください。");
    }
    function validateShihonkin() {
        if(!validateRequired(shihonkinInput, shihonkinError, "資本金を入力してください。")) return false;
        if (isNaN(Number(shihonkinInput.value.trim().replace(/,/g, '')))) {
            shihonkinError.textContent = "資本金は数値で入力してください。";
            shihonkinError.style.display = 'block';
            shihonkinInput.classList.add('input-error');
            return false;
        }
        shihonkinError.style.display = 'none';
        shihonkinInput.classList.remove('input-error');
        return true;
    }
    function validateNouki() {
        if(!validateRequired(noukiInput, noukiError, "納期を入力してください。")) return false;
        if (isNaN(Number(noukiInput.value.trim()))) {
            noukiError.textContent = "納期は数値（日数）で入力してください。";
            noukiError.style.display = 'block';
            noukiInput.classList.add('input-error');
            return false;
        }
        noukiError.style.display = 'none';
        noukiInput.classList.remove('input-error');
        return true;
    }


    // --- イベントリスナーの設定 ---
    shiireidInput.onblur = validateShiireId;
    shiiremeiInput.onblur = validateShiireMei;
    shiireaddressInput.onblur = validateShiireAddress;
    shiiretelInput.onblur = validateShiireTel; // ★古いチェックを新しい関数に置き換え
    shihonkinInput.onblur = validateShihonkin;
    noukiInput.onblur = validateNouki;

    // --- フォーム送信時の総合バリデーションを修正 ---
    function validateSupplierForm() {
        let isValid = true;
        isValid &= validateShiireId();
        isValid &= validateShiireMei();
        isValid &= validateShiireAddress();
        isValid &= validateShiireTel(); // ★新しい電話番号チェック関数を呼び出す
        isValid &= validateShihonkin();
        isValid &= validateNouki();
        return !!isValid; // booleanに変換して返す
    }
</script>
</body>
</html>