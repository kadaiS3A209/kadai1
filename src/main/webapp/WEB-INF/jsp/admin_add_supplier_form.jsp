<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.ShiiregyoshaBean" %> <%-- パスを合わせる --%>
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
    input[type="text"], input[type="number"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .error-message { color: red; font-size: 0.9em; margin-top: 3px; display: none; /* 初期非表示 */ }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    .button:hover { background-color: #0056b3; }
</style>
</head>
<body>
    <div class="form-container">
        <h1>仕入先登録</h1>

        <%
            String formError = (String) request.getAttribute("formError");
            if (formError != null) {
        %>
            <p style="color:red;"><%= formError %></p>
        <%
            }
            // 確認画面から戻ってきた場合やエラー時の値を復元
            ShiiregyoshaBean prevInput = (ShiiregyoshaBean) request.getAttribute("prevSupplierInput");
            if (prevInput == null) { // セッションからも試みる (確認画面からの「修正」)
                prevInput = (ShiiregyoshaBean) session.getAttribute("tempSupplier");
            }

            String prevId = (prevInput != null && prevInput.getShiireId() != null) ? prevInput.getShiireId() : "";
            String prevMei = (prevInput != null && prevInput.getShiireMei() != null) ? prevInput.getShiireMei() : "";
            String prevAddr = (prevInput != null && prevInput.getShiireAddress() != null) ? prevInput.getShiireAddress() : "";
            String prevTel = (prevInput != null && prevInput.getShiireTel() != null) ? prevInput.getShiireTel() : "";
            String prevShihonkin = (prevInput != null && prevInput.getShihonkin() != 0) ? String.valueOf(prevInput.getShihonkin()) : (request.getParameter("shihonkin") != null ? request.getParameter("shihonkin") : "");
            String prevNouki = (prevInput != null && prevInput.getNouki() != 0) ? String.valueOf(prevInput.getNouki()) : (request.getParameter("nouki") != null ? request.getParameter("nouki") : "");

        %>

        <form id="addSupplierForm" action="AdminAddSupplierServlet" method="post" onsubmit="return validateSupplierForm()">
            <input type="hidden" name="action" value="confirm">

            <div class="form-group">
                <label for="shiireid">仕入先ID (8桁以内):</label>
                <input type="text" id="shiireid" name="shiireid" value="<%= prevId %>" required maxlength="8">
                <span id="shiireidError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="shiiremei">仕入先名:</label>
                <input type="text" id="shiiremei" name="shiiremei" value="<%= prevMei %>" required>
                <span id="shiiremeiError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="shiireaddress">住所:</label>
                <input type="text" id="shiireaddress" name="shiireaddress" value="<%= prevAddr %>" required>
                <span id="shiireaddressError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="shiiretel">電話番号:</label>
                <input type="text" id="shiiretel" name="shiiretel" value="<%= prevTel %>" required placeholder="例: 03-1234-5678">
                <span id="shiiretelError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="shihonkin">資本金 (円):</label>
                <input type="text" id="shihonkin" name="shihonkin" value="<%= prevShihonkin %>" required placeholder="例: 1000000 (カンマなし)">
                <span id="shihonkinError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="nouki">納期 (日数):</label>
                <input type="text" id="nouki" name="nouki" value="<%= prevNouki %>" required placeholder="例: 7">
                <span id="noukiError" class="error-message"></span>
            </div>

            <button type="submit" class="button">確認画面へ</button>
        </form>
    </div>

<script>
    // フォーム要素の取得
    const shiireidInput = document.getElementById('shiireid');
    const shiiremeiInput = document.getElementById('shiiremei');
    const shiireaddressInput = document.getElementById('shiireaddress');
    const shiiretelInput = document.getElementById('shiiretel');
    const shihonkinInput = document.getElementById('shihonkin');
    const noukiInput = document.getElementById('nouki');

    // エラーメッセージ要素の取得
    const shiireidError = document.getElementById('shiireidError');
    const shiiremeiError = document.getElementById('shiiremeiError');
    const shiireaddressError = document.getElementById('shiireaddressError');
    const shiiretelError = document.getElementById('shiiretelError');
    const shihonkinError = document.getElementById('shihonkinError');
    const noukiError = document.getElementById('noukiError');

    // 汎用的な必須チェック関数
    function validateRequired(inputEl, errorEl, message) {
        if (!inputEl.value.trim()) {
            errorEl.textContent = message;
            errorEl.style.display = 'block';
            return false;
        }
        errorEl.style.display = 'none';
        return true;
    }

    // 各フィールドのバリデーション (onblurなどで呼び出す)
    shiireidInput.onblur = () => {
        if(!validateRequired(shiireidInput, shiireidError, "仕入先IDを入力してください。")) return;
        if (shiireidInput.value.trim().length > 8) {
            shiireidError.textContent = "仕入先IDは8文字以内で入力してください。";
            shiireidError.style.display = 'block';
        } else {
            shiireidError.style.display = 'none';
        }
    };
    shiiremeiInput.onblur = () => validateRequired(shiiremeiInput, shiiremeiError, "仕入先名を入力してください。");
    shiireaddressInput.onblur = () => validateRequired(shiireaddressInput, shiireaddressError, "住所を入力してください。");

    shiiretelInput.onblur = () => {
        if(!validateRequired(shiiretelInput, shiiretelError, "電話番号を入力してください。")) return;
        // テストケース: 数字、()、- 以外の形式でエラー 
        // 簡単な正規表現例 (より厳密なものは要件に応じて調整)
        const telPattern = /^[0-9()-]+$/;
        if (!telPattern.test(shiiretelInput.value.trim())) {
            shiiretelError.textContent = "電話番号の形式が正しくありません (使用可能な文字: 数字、-、())。";
            shiiretelError.style.display = 'block';
        } else {
            shiiretelError.style.display = 'none';
        }
    };

    shihonkinInput.onblur = () => {
        if(!validateRequired(shihonkinInput, shihonkinError, "資本金を入力してください。")) return;
        // テストケース: 数値・カンマ以外の形式でエラー 
        // クライアントサイドでは単純に数値かどうかをチェック（カンマはサーバーサイドで処理も可）
        if (isNaN(Number(shihonkinInput.value.trim().replace(/,/g, '')))) { // カンマを除去して数値変換試行
            shihonkinError.textContent = "資本金は数値で入力してください。";
            shihonkinError.style.display = 'block';
        } else {
            shihonkinError.style.display = 'none';
        }
    };

    noukiInput.onblur = () => {
        if(!validateRequired(noukiInput, noukiError, "納期を入力してください。")) return;
        // テストケース: 数字以外の情報でエラー 
        if (isNaN(Number(noukiInput.value.trim()))) {
            noukiError.textContent = "納期は数値（日数）で入力してください。";
            noukiError.style.display = 'block';
        } else {
            noukiError.style.display = 'none';
        }
    };


    // フォーム送信時の総合バリデーション
    function validateSupplierForm() {
        let isValid = true;
        isValid &= validateRequired(shiireidInput, shiireidError, "仕入先IDを入力してください。") && (shiireidInput.value.trim().length <= 8 || (shiireidError.textContent = "仕入先IDは8文字以内で入力してください。", shiireidError.style.display = 'block', false));
        isValid &= validateRequired(shiiremeiInput, shiiremeiError, "仕入先名を入力してください。");
        isValid &= validateRequired(shiireaddressInput, shiireaddressError, "住所を入力してください。");
        isValid &= validateRequired(shiiretelInput, shiiretelError, "電話番号を入力してください。") && (/^[0-9()-]+$/.test(shiiretelInput.value.trim()) || (shiiretelError.textContent = "電話番号の形式が正しくありません。", shiiretelError.style.display = 'block', false));
        isValid &= validateRequired(shihonkinInput, shihonkinError, "資本金を入力してください。") && (!isNaN(Number(shihonkinInput.value.trim().replace(/,/g, ''))) || (shihonkinError.textContent = "資本金は数値で入力してください。", shihonkinError.style.display = 'block', false));
        isValid &= validateRequired(noukiInput, noukiError, "納期を入力してください。") && (!isNaN(Number(noukiInput.value.trim())) || (noukiError.textContent = "納期は数値（日数）で入力してください。", noukiError.style.display = 'block', false));
        return !!isValid;
    }
</script>
</body>
</html>