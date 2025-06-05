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
    // 簡易的なクライアントサイドバリデーション (仕入先登録フォームのものを参考に調整)
    const tabyouinIdInput = document.getElementById('tabyouinId');
    const tabyouinIdError = document.getElementById('tabyouinIdError');
    const tabyouinMeiInput = document.getElementById('tabyouinMei');
    const tabyouinMeiError = document.getElementById('tabyouinMeiError');
    const tabyouinAddrssInput = document.getElementById('tabyouinAddrss');
    const tabyouinAddrssError = document.getElementById('tabyouinAddrssError');
    const tabyouinTelInput = document.getElementById('tabyouinTel');
    const tabyouinTelError = document.getElementById('tabyouinTelError');
    const tabyouinShihonkinInput = document.getElementById('tabyouinShihonkin');
    const tabyouinShihonkinError = document.getElementById('tabyouinShihonkinError');
    const kyukyuInput = document.getElementById('kyukyu');
    const kyukyuError = document.getElementById('kyukyuError');
    // 他の入力フィールドとエラーメッセージ要素も同様に取得

    function validateRequired(inputEl, errorEl, message) {
        if (!inputEl.value.trim()) {
            errorEl.textContent = message; errorEl.style.display = 'block'; inputEl.classList.add('input-error'); return false;
        }
        errorEl.style.display = 'none'; inputEl.classList.remove('input-error'); return true;
    }

    tabyouinIdInput.onblur = () => {
        if(!validateRequired(tabyouinIdInput, tabyouinIdError, "他病院IDを入力してください。")) return;
        if (tabyouinIdInput.value.trim().length > 8) {
            tabyouinIdError.textContent = "他病院IDは8文字以内で入力してください。"; tabyouinIdError.style.display = 'block'; tabyouinIdInput.classList.add('input-error');
        } else { tabyouinIdError.style.display = 'none'; tabyouinIdInput.classList.remove('input-error'); }
    };

 // 他病院名のバリデーション (必須チェックのみ)
    function validateTabyouinMei() {
        return validateRequired(tabyouinMeiInput, tabyouinMeiError, "他病院名を入力してください。");
    }

    // 住所のバリデーション (必須チェックのみ)
    function validateTabyouinAddrss() {
        return validateRequired(tabyouinAddrssInput, tabyouinAddrssError, "住所を入力してください。");
    }

    // 電話番号のバリデーション (必須チェック + 簡単な形式チェック)
    function validateTabyouinTel() {
        if (!validateRequired(tabyouinTelInput, tabyouinTelError, "電話番号を入力してください。")) {
            return false;
        }
        const telValue = tabyouinTelInput.value.trim();
        const telPattern = /^[0-9\-()]{10,15}$/; // 数字、ハイフン、括弧のみ、10～15桁程度の想定
        if (!telPattern.test(telValue)) {
            tabyouinTelError.textContent = "電話番号の形式が正しくありません。(例: 012-345-6789)";
            tabyouinTelError.style.display = 'block';
            tabyouinTelInput.classList.add('input-error');
            return false;
        }
        tabyouinTelError.style.display = 'none';
        tabyouinTelInput.classList.remove('input-error');
        return true;
    }

    // 資本金のバリデーション (必須チェック + 数値チェック)
    function validateTabyouinShihonkin() {
        if (!validateRequired(tabyouinShihonkinInput, tabyouinShihonkinError, "資本金を入力してください。")) {
            return false;
        }
        const shihonkinValue = tabyouinShihonkinInput.value.trim().replace(/,/g, ''); // カンマを除去
        if (isNaN(Number(shihonkinValue)) || Number(shihonkinValue) < 0) { // 数値であること、かつ0以上
            tabyouinShihonkinError.textContent = "資本金は0以上の数値を入力してください。";
            tabyouinShihonkinError.style.display = 'block';
            tabyouinShihonkinInput.classList.add('input-error');
            return false;
        }
        tabyouinShihonkinError.style.display = 'none';
        tabyouinShihonkinInput.classList.remove('input-error');
        return true;
    }

    // 救急対応のバリデーション (select要素なので必須チェックのみ)
    function validateKyukyu() {
        // select要素の場合、valueが空文字（初期値の「選択してください」など）でないことを確認
        if (!kyukyuSelect.value) { // valueが "" の場合
            kyukyuError.textContent = "救急対応を選択してください。";
            kyukyuError.style.display = 'block';
            kyukyuSelect.classList.add('input-error');
            return false;
        }
        kyukyuError.style.display = 'none';
        kyukyuSelect.classList.remove('input-error');
        return true;
    }


    // イベントリスナーの設定 (onblur: フォーカスが外れた時)
    tabyouinMeiInput.onblur = validateTabyouinMei;
    tabyouinAddrssInput.onblur = validateTabyouinAddrss;
    tabyouinTelInput.onblur = validateTabyouinTel;
    tabyouinShihonkinInput.onblur = validateTabyouinShihonkin;
    kyukyuSelect.onblur = validateKyukyu; // select要素もonblurでチェック可能

    // フォーム送信時の総合バリデーション (既存の addTabyouinForm.onsubmit を修正)
    const form = document.getElementById('addTabyouinForm');
    if(form) {
        form.onsubmit = function(event) {
            let isValid = true;
            isValid &= validateRequired(tabyouinIdInput, tabyouinIdError, "他病院IDを入力してください。") && (tabyouinIdInput.value.trim().length <= 8 || (tabyouinIdError.textContent = "他病院IDは8文字以内で入力してください。", tabyouinIdError.style.display = 'block', tabyouinIdInput.classList.add('input-error'), false));
            isValid &= validateTabyouinMei();
            isValid &= validateTabyouinAddrss();
            isValid &= validateTabyouinTel();
            isValid &= validateTabyouinShihonkin();
            isValid &= validateKyukyu();

            if (!isValid) {
                event.preventDefault(); // バリデーションエラーがあれば送信を中止
                // alert("入力内容にエラーがあります。各項目を確認してください。"); // 必要なら全体メッセージ
                // 最初のエラーフィールドにフォーカスを当てるなどの処理も可能
                if (!tabyouinIdError.style.display === 'none') tabyouinIdInput.focus();
                else if (!tabyouinMeiError.style.display === 'none') tabyouinMeiInput.focus();
                // ...以下同様に
                return false;
            }
            return true; // バリデーションOKなら送信
        };
    // 他のフィールド (tabyouinMei, tabyouinAddrss, tabyouinTel, tabyouinShihonkin) の onblur バリデーションも同様に追加
    // 電話番号: /^[0-9()-]+$/ など
    // 資本金: isNaN(Number(value.replace(/,/g, ''))) など
</script>
</body>
</html>