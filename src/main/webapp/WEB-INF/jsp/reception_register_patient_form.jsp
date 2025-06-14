<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.PatientBean" %> <%-- パスを合わせる --%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>患者登録</title>
<style>
    /* 既存のフォームスタイルを再利用または調整 */
    body { font-family: sans-serif; }
    .form-container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="date"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .error-message { color: red; font-size: 0.9em; margin-top: 3px; display: none; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
    input.input-error {border-color: red;}
</style>
</head>
<body>
    <div class="form-container">
        <h1>患者登録</h1>

        <c:if test="${not empty formError}">
            <p style="color:red;"><c:out value="${formError}"/></p>
        </c:if>

        <%
            PatientBean prevInput = null;
            Object prevInputObj = request.getAttribute("prevPatientInput");
            if (prevInputObj instanceof PatientBean) {
                prevInput = (PatientBean) prevInputObj;
            }
            if (prevInput == null) {
                prevInputObj = session.getAttribute("tempPatient");
                 if (prevInputObj instanceof PatientBean) {
                    prevInput = (PatientBean) prevInputObj;
                }
            }

            String prevPatId = (prevInput != null && prevInput.getPatId() != null) ? prevInput.getPatId() : "";
            String prevPatLname = (prevInput != null && prevInput.getPatLname() != null) ? prevInput.getPatLname() : "";
            String prevPatFname = (prevInput != null && prevInput.getPatFname() != null) ? prevInput.getPatFname() : "";
            String prevHokenmei = (prevInput != null && prevInput.getHokenmei() != null) ? prevInput.getHokenmei() : "";
            String prevHokenexpStr = "";
            if (prevInput != null && prevInput.getHokenexp() != null) {
                 // java.util.Date を yyyy-MM-dd 形式の文字列に変換
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                prevHokenexpStr = sdf.format(prevInput.getHokenexp());
            } else if (request.getParameter("hokenexp") != null) { // サーブレットから文字列で戻された場合
                prevHokenexpStr = request.getParameter("hokenexp");
            }
        %>

        <form id="registerPatientForm" action="ReceptionRegisterPatientServlet" method="post">
            <input type="hidden" name="action" value="confirm">

            <div class="form-group">
                <label for="patid">患者ID (8桁以内):</label>
                <input type="text" id="patid" name="patid" value="<%= prevPatId %>" required maxlength="8">
                <span id="patidError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="patlname">患者姓:</label>
                <input type="text" id="patlname" name="patlname" value="<%= prevPatLname %>" required>
                <span id="patlnameError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="patfname">患者名:</label>
                <input type="text" id="patfname" name="patfname" value="<%= prevPatFname %>" required>
                <span id="patfnameError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="hokenmei">保険証記号番号:</label>
                <input type="text" id="hokenmei" name="hokenmei" value="<%= prevHokenmei %>" required>
                <span id="hokenmeiError" class="error-message"></span>
            </div>
            <div class="form-group">
                <label for="hokenexp">有効期限 (YYYY-MM-DD):</label>
                <input type="date" id="hokenexp" name="hokenexp" value="<%= prevHokenexpStr %>" required>
                <span id="hokenexpError" class="error-message"></span>
            </div>

            <button type="submit" class="button">確認画面へ</button>
        </form>
        <p style="margin-top:15px;"><a href="ReturnToMenuServlet">受付メニューへ戻る</a></p>
    </div>

<script>
    // フォーム要素の取得
    const patidInput = document.getElementById('patid');
    const patlnameInput = document.getElementById('patlname'); // 患者姓
    const patfnameInput = document.getElementById('patfname'); // 患者名
    const hokenmeiInput = document.getElementById('hokenmei'); // 保険証記号番号
    const hokenexpInput = document.getElementById('hokenexp'); // 有効期限

    // エラーメッセージ表示用Span要素の取得
    const patidError = document.getElementById('patidError');
    const patlnameError = document.getElementById('patlnameError');
    const patfnameError = document.getElementById('patfnameError');
    const hokenmeiError = document.getElementById('hokenmeiError');
    const hokenexpError = document.getElementById('hokenexpError');

    // 汎用的な必須チェック関数
    function validateRequired(inputEl, errorEl, message) {
        if (!inputEl.value.trim()) {
            errorEl.textContent = message;
            errorEl.style.display = 'block';
            inputEl.classList.add('input-error'); // エラー時に枠線を赤くするなどのスタイル用
            return false;
        }
        errorEl.style.display = 'none';
        inputEl.classList.remove('input-error');
        return true;
    }

    // 患者IDのバリデーション (既存のものを調整)
    function validatePatId() {
        if (!validateRequired(patidInput, patidError, "患者IDを入力してください。")) return false;
        if (patidInput.value.trim().length > 8) {
            patidError.textContent = "患者IDは8文字以内で入力してください。";
            patidError.style.display = 'block';
            patidInput.classList.add('input-error');
            return false;
        }
        // ここにさらに形式チェック（例: 英数字のみなど）を追加可能
        patidError.style.display = 'none';
        patidInput.classList.remove('input-error');
        return true;
    }

    // 患者姓のバリデーション
    function validatePatLname() {
        return validateRequired(patlnameInput, patlnameError, "患者姓を入力してください。");
    }

    // 患者名のバリデーション
    function validatePatFname() {
        return validateRequired(patfnameInput, patfnameError, "患者名を入力してください。");
    }

    // 保険証記号番号のバリデーション
    function validateHokenmei() {
        // ここでは必須チェックのみ。必要に応じて形式チェックを追加。
        return validateRequired(hokenmeiInput, hokenmeiError, "保険証記号番号を入力してください。");
    }

    // 有効期限のバリデーション
    function validateHokenexp() {
        // 必須チェック
        if (!validateRequired(hokenexpInput, hokenexpError, "有効期限を入力してください。")) {
            return false;
        }

        const today = new Date();
        // 今日の日付の時刻部分をリセットして、純粋な日付で比較できるようにする
        // 例: 2025-06-15 00:00:00
        today.setHours(0, 0, 0, 0);

        // ユーザーが入力した日付を取得
        const selectedDate = new Date(hokenexpInput.value);
        // 時刻の差異による問題を避けるため、こちらも時刻部分をリセット
        // new Date("2025-06-14") は、環境によって前日の23時などになる場合があるため、
        // ユーザーのタイムゾーンを考慮してDateオブジェクトを作成するのがより安全
        // const [year, month, day] = hokenexpInput.value.split('-').map(Number);
        // const selectedDate = new Date(year, month - 1, day);

        // ★★★ 過去日付のチェックを追加 ★★★
        if (selectedDate < today) {
            hokenexpError.textContent = "有効期限は本日以降の日付を入力してください。";
            hokenexpError.style.display = 'block';
            hokenexpInput.classList.add('input-error');
            return false; // バリデーション失敗
        }
        
        // エラーがなければメッセージを非表示にする
        hokenexpError.style.display = 'none';
        hokenexpInput.classList.remove('input-error');
        return true; // バリデーション成功
    }
    // ▲▲▲ ここまで修正 ▲▲▲

    // イベントリスナーの設定 (onblur: フォーカスが外れた時)
    patidInput.onblur = validatePatId;
    patlnameInput.onblur = validatePatLname;
    patfnameInput.onblur = validatePatFname;
    hokenmeiInput.onblur = validateHokenmei;
    hokenexpInput.onblur = validateHokenexp; // または onChange

    // フォーム送信時の総合バリデーション
    const form = document.getElementById('registerPatientForm');
    if(form) { // form要素が存在する場合のみイベントリスナーを設定
        form.onsubmit = function(event) {
            let isValid = true;
            isValid &= validatePatId();
            isValid &= validatePatLname();
            isValid &= validatePatFname();
            isValid &= validateHokenmei();
            isValid &= validateHokenexp();

            if (!isValid) {
                event.preventDefault(); // バリデーションエラーがあれば送信を中止
                alert("入力内容にエラーがあります。確認してください。"); // 全体的なエラー通知
                return false;
            }
            return true; // バリデーションOKなら送信
        };
    }
</script>
</body>
</html>