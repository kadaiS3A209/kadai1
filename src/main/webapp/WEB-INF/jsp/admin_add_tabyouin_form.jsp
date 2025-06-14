<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.TabyouinBean" %> <%-- パスを合わせる --%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>他病院 新規登録</title>
<style>
    body { font-family: sans-serif; }
    .form-container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="number"], select { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .error-message { color: red; font-size: 0.9em; margin-top: 3px; display: none; } /* JS用 */
    .error-message-server { color: red; background-color: #ffebeb; border:1px solid #ffcdd2; padding:8px; border-radius:4px; margin-bottom:10px;} /* サーブレットからのエラー用 */
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    input.input-error { border-color: red; }
</style>
</head>
<body>
    <div class="form-container">
        <h1>他病院 新規登録</h1>

        <c:if test="${not empty formError_tabyouin_register}">
            <p class="error-message-server"><c:out value="${formError_tabyouin_register}" escapeXml="false"/></p>
        </c:if>

        <%
            TabyouinBean prevInput = null;
            Object prevInputObj = request.getAttribute("prevTabyouinInput");
            if (prevInputObj instanceof TabyouinBean) {
                prevInput = (TabyouinBean) prevInputObj;
            }
            if (prevInput == null) { // 確認画面から戻ってきた場合
                prevInputObj = session.getAttribute("tempTabyouin");
                 if (prevInputObj instanceof TabyouinBean) {
                    prevInput = (TabyouinBean) prevInputObj;
                }
            }

            String prevId = (prevInput != null && prevInput.getTabyouinId() != null) ? prevInput.getTabyouinId() : "";
            String prevMei = (prevInput != null && prevInput.getTabyouinMei() != null) ? prevInput.getTabyouinMei() : "";
            String prevAddr = (prevInput != null && prevInput.getTabyouinAddrss() != null) ? prevInput.getTabyouinAddrss() : "";
            String prevTel = (prevInput != null && prevInput.getTabyouinTel() != null) ? prevInput.getTabyouinTel() : "";
            String prevShihonkin = (prevInput != null && prevInput.getTabyouinShihonkin() != 0) ? String.valueOf(prevInput.getTabyouinShihonkin()) : (request.getParameter("tabyouinShihonkin") != null ? request.getParameter("tabyouinShihonkin") : "");
            String prevKyukyu = (prevInput != null) ? String.valueOf(prevInput.getKyukyu()) : "0"; // デフォルトを0（非対応）など
        %>

        <form id="addTabyouinForm" action="AdminAddTabyouinServlet" method="post">
            <input type="hidden" name="action" value="confirm">

            <div class="form-group">
                <label for="tabyouinId">他病院ID (8桁以内):</label>
                <input type="text" id="tabyouinId" name="tabyouinId" value="<%= prevId %>" required maxlength="8">
                <span id="tabyouinIdError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="tabyouinMei">他病院名:</label>
                <input type="text" id="tabyouinMei" name="tabyouinMei" value="<%= prevMei %>" required>
                <span id="tabyouinMeiError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="tabyouinAddrss">住所:</label>
                <input type="text" id="tabyouinAddrss" name="tabyouinAddrss" value="<%= prevAddr %>" required>
                <span id="tabyouinAddrssError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="tabyouinTel">電話番号:</label>
                <input type="text" id="tabyouinTel" name="tabyouinTel" value="<%= prevTel %>" required placeholder="例: 03-1234-5678">
                <span id="tabyouinTelError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="tabyouinShihonkin">資本金 (円):</label>
                <input type="text" id="tabyouinShihonkin" name="tabyouinShihonkin" value="<%= prevShihonkin %>" required placeholder="例: 1000000 (カンマなし数字)">
                <span id="tabyouinShihonkinError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="kyukyu">救急対応:</label>
                <select id="kyukyu" name="kyukyu" required>
                    <option value="1" <%= "1".equals(prevKyukyu) ? "selected" : "" %>>あり</option>
                    <option value="0" <%= "0".equals(prevKyukyu) ? "selected" : "" %>>なし</option>
                </select>
                <span id="kyukyuError" class="error-message"></span>
            </div>

            <button type="submit" class="button">確認画面へ</button>
        </form>
        <p style="margin-top:15px;"><a href="ReturnToMenuServlet">管理者メニューへ戻る</a></p>
    </div>

<script>
    // フォーム要素の取得
    const tabyouinIdInput = document.getElementById('tabyouinId');
    const tabyouinMeiInput = document.getElementById('tabyouinMei');
    const tabyouinAddrssInput = document.getElementById('tabyouinAddrss');
    const tabyouinTelInput = document.getElementById('tabyouinTel'); // ★対象の電話番号入力フィールド
    const tabyouinShihonkinInput = document.getElementById('tabyouinShihonkin');
    const kyukyuSelect = document.getElementById('kyukyu'); // IDをkyukyuSelectに修正

    // エラーメッセージ要素の取得
    const tabyouinIdError = document.getElementById('tabyouinIdError');
    const tabyouinMeiError = document.getElementById('tabyouinMeiError');
    const tabyouinAddrssError = document.getElementById('tabyouinAddrssError');
    const tabyouinTelError = document.getElementById('tabyouinTelError'); // ★対象のエラー表示用span
    const tabyouinShihonkinError = document.getElementById('tabyouinShihonkinError');
    const kyukyuError = document.getElementById('kyukyuError');

    // 汎用的な必須チェック関数
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

    // --- 各フィールドのバリデーション関数 ---

    function validateTabyouinId() {
        if(!validateRequired(tabyouinIdInput, tabyouinIdError, "他病院IDを入力してください。")) return false;
        if (tabyouinIdInput.value.trim().length > 8) {
            tabyouinIdError.textContent = "他病院IDは8文字以内で入力してください。";
            tabyouinIdError.style.display = 'block';
            tabyouinIdInput.classList.add('input-error');
            return false;
        }
        tabyouinIdError.style.display = 'none';
        tabyouinIdInput.classList.remove('input-error');
        return true;
    }

    function validateTabyouinMei() {
        return validateRequired(tabyouinMeiInput, tabyouinMeiError, "他病院名を入力してください。");
    }

    function validateTabyouinAddrss() {
        return validateRequired(tabyouinAddrssInput, tabyouinAddrssError, "住所を入力してください。");
    }

    // ▼▼▼ ここからが新しい電話番号バリデーション関数 ▼▼▼
    function validateTabyouinTel() {
        if (!validateRequired(tabyouinTelInput, tabyouinTelError, "電話番号を入力してください。")) {
            return false;
        }

        const telValue = tabyouinTelInput.value;
        let errorMessage = null;
        const trimmedInput = telValue.trim();

        // 許可されていない文字が含まれていないかチェック
        if (trimmedInput.match(/[^0-9()-]/)) {
            errorMessage = "電話番号には数字、ハイフン、括弧以外の文字は使用できません。";
        } else {
            // 数字のみを抽出し、桁数と先頭文字をチェック
            const digitsOnly = trimmedInput.replace(/[^0-9]/g, "");

            if (digitsOnly.length < 10) {
                errorMessage = "電話番号の桁数が不足しています。数字のみで10桁以上必要です。";
            } else if (digitsOnly.length > 11) {
                errorMessage = "電話番号の桁数が超過しています。数字のみで11桁以下です。";
            } else if (!digitsOnly.startsWith("0")) {
                errorMessage = "電話番号は0から始まる必要があります。";
            } else {
                // 書式のチェック
                const hyphenGeneralPattern = /^0[1-9]\d{0,3}-\d{1,4}-\d{4}$/;
                const hyphenSpecialPattern = /^(0120|0800|0570|0990)-\d{3}-\d{3}$/;
                const parenPattern = /^0[1-9]\d{0,3}\(\d{1,4}\)\d{4}$/;

                // ハイフンや括弧があるのに、どのパターンにも一致しない場合はエラー
                if (trimmedInput.includes('-') || trimmedInput.includes('(') || trimmedInput.includes(')')) {
                    if (!hyphenGeneralPattern.test(trimmedInput) &&
                        !hyphenSpecialPattern.test(trimmedInput) &&
                        !parenPattern.test(trimmedInput)) {
                        errorMessage = "電話番号の形式が正しくありません。(例: 03-1234-5678)";
                    }
                }
            }
        }

        // エラー判定とメッセージ表示
        if (errorMessage) {
            tabyouinTelError.textContent = errorMessage;
            tabyouinTelError.style.display = 'block';
            tabyouinTelInput.classList.add('input-error');
            return false;
        } else {
            tabyouinTelError.style.display = 'none';
            tabyouinTelInput.classList.remove('input-error');
            return true;
        }
    }
    // ▲▲▲ 新しい電話番号バリデーション関数の終わり ▲▲▲

    function validateTabyouinShihonkin() {
        if (!validateRequired(tabyouinShihonkinInput, tabyouinShihonkinError, "資本金を入力してください。")) return false;
        const shihonkinValue = tabyouinShihonkinInput.value.trim().replace(/,/g, '');
        if (isNaN(Number(shihonkinValue)) || Number(shihonkinValue) < 0) {
            tabyouinShihonkinError.textContent = "資本金は0以上の数値を入力してください。";
            tabyouinShihonkinError.style.display = 'block';
            tabyouinShihonkinInput.classList.add('input-error');
            return false;
        }
        tabyouinShihonkinError.style.display = 'none';
        tabyouinShihonkinInput.classList.remove('input-error');
        return true;
    }

    function validateNouki() {
        if (!validateRequired(noukiInput, noukiError, "納期を入力してください。")) return false;
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
    tabyouinIdInput.onblur = validateTabyouinId;
    tabyouinMeiInput.onblur = validateTabyouinMei;
    tabyouinAddrssInput.onblur = validateTabyouinAddrss;
    tabyouinTelInput.onblur = validateTabyouinTel; // ★新しい関数を呼び出すように設定
    tabyouinShihonkinInput.onblur = validateTabyouinShihonkin;
    noukiInput.onblur = validateNouki;
    // kyukyuSelect.onblur の設定が抜けていたので追加
    // kyukyuSelect.onblur = () => validateRequired(kyukyuSelect, kyukyuError, "救急対応を選択してください。");

    // --- フォーム送信時の総合バリデーションを修正 ---
    const form = document.getElementById('addTabyouinForm');
    if(form) {
        form.onsubmit = function(event) {
            // 各バリデーション関数を呼び出して結果をチェック
            const isIdValid = validateTabyouinId();
            const isMeiValid = validateTabyouinMei();
            const isAddrssValid = validateTabyouinAddrss();
            const isTelValid = validateTabyouinTel(); // ★新しい関数を呼び出す
            const isShihonkinValid = validateTabyouinShihonkin();
            const isNoukiValid = validateNouki();
            // const isKyukyuValid = validateRequired(kyukyuSelect, kyukyuError, "救急対応を選択してください。"); // 救急対応もチェック

            if (!isIdValid || !isMeiValid || !isAddrssValid || !isTelValid || !isShihonkinValid || !isNoukiValid) {
                event.preventDefault(); // いずれかのバリデーションが失敗したら送信を中止
                return false;
            }
            return true;
        }
    }
</script>
</body>
</html>