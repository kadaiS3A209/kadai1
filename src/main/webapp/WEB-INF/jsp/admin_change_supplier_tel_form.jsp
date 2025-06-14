<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>仕入先電話番号変更</title>
<style>
    body { font-family: sans-serif; }
    .container { width: 500px; margin: 20px auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;}
    .form-group { margin-bottom: 15px; } label { display: block; margin-bottom: 5px; }
    input[type="text"], input[type="tel"] { width: calc(100% - 18px); padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
    .button { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; margin-right:10px;}
    .message { margin:10px 0; padding:10px; border-radius:4px; }
    .error-message { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb;}
    .info-bar { padding: 10px; background-color: #f0f0f0; margin-bottom:15px; border-radius:4px; }
</style>
</head>
<body>
    <div class="container">
        <h1>仕入先電話番号変更</h1>

        <c:if test="${not empty supplierToChange}">
            <div class="info-bar">
                <strong>仕入先ID:</strong> <c:out value="${supplierToChange.shiireId}"/><br>
                <strong>仕入先名:</strong> <c:out value="${supplierToChange.shiireMei}"/><br>
                <strong>現在の電話番号:</strong> <c:out value="${supplierToChange.shiireTel}"/>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage_telChange}"><div class="message error-message"><c:out value="${errorMessage_telChange}"/></div></c:if>
        <%-- 成功メッセージは一覧画面に表示するため、ここでは通常不要 --%>

        <c:if test="${not empty supplierToChange}">
            <form action="AdminListSuppliersServlet" method="post"> <%-- 送信先を一覧サーブレットに --%>
            	<input type="hidden" name="csrf_token" value="<c:out value='${csrf_token}'/>">
                <input type="hidden" name="action" value="updateTel">
                <input type="hidden" name="shiireIdToChange" value="<c:out value='${supplierToChange.shiireId}'/>">
                <input type="hidden" name="sourceList" value="<c:out value='${sourceList}'/>"> <%-- 遷移元情報を引き継ぐ --%>

                <div class="form-group">
                    <label for="newTel">新しい電話番号:</label>
                    <input type="tel" id="newTel" name="newTel" value="<c:out value='${param.newTel != null ? param.newTel : supplierToChange.shiireTel}'/>" required title="電話番号はハイフンを含む正しい形式か、数字のみ(10桁以上、先頭0)で入力してください。">
                    <span id="newTelError" class="error-message"></span>
                </div>
                <button type="submit" class="button">電話番号を変更する</button>
                <%-- 戻り先を動的にするためのc:chooseブロック --%>
                <c:choose>
                    <c:when test="${not empty sourceList}">
                         <%-- 具体的なURLをsourceListの値によって変えるのはサーブレットの役割。
                              ここでは、単純にAdminListSuppliersServletに戻り、
                              sourceListの値（や他のセッションに保存した検索条件）を元に
                              サーブレットが元の表示を復元することを期待する。
                              または、単純に全件表示に戻るリンクでも良い。
                         --%>
                        <a href="AdminListSuppliersServlet" class="button" style="background-color:#6c757d;">一覧へ戻る</a>
                    </c:when>
                    <c:otherwise>
                        <a href="AdminListSuppliersServlet" class="button" style="background-color:#6c757d;">仕入先一覧へ戻る</a>
                    </c:otherwise>
                </c:choose>
            </form>
        </c:if>
        <c:if test="${empty supplierToChange && empty errorMessage_telChange}">
             <p>変更対象の仕入先が選択されていません。</p>
             <p><a href="AdminListSuppliersServlet">仕入先一覧へ戻る</a></p>
        </c:if>
    </div>
    <script type="text/javascript">
 // JSPの <script> タグ内に記述する関数
 // この関数を、電話番号入力フィールドの onblur イベントと、フォームの onsubmit イベントから呼び出します。

 function validatePhoneNumber() {
     // JSP内の電話番号入力フィールドとエラーメッセージ表示用のspan要素のIDに合わせてください
     const inputElement = document.getElementById('newTel'); // 例: admin_change_supplier_tel_form.jsp の場合
     const errorElement = document.getElementById('newTelError'); // 対応するエラーspan

     if (!inputElement || !errorElement) {
         // 要素が見つからない場合は何もしない（コンソールにエラーを出すと良い）
         console.error("電話番号の入力フィールドまたはエラー表示用の要素が見つかりません。");
         return false;
     }

     const telValue = inputElement.value;
     let errorMessage = null; // エラーメッセージを格納する変数

     // 1. 入力値のチェック（nullまたは空文字）
     if (!telValue || !telValue.trim()) {
         errorMessage = "電話番号が入力されていません。";
     } else {
         const trimmedInput = telValue.trim();

         // 2. 許可されていない文字が含まれていないかチェック
         //    Javaの正規表現 `[^\\d()\\-]` は、JavaScriptでは `/[^0-9()-]/` とほぼ同等です。
         if (trimmedInput.match(/[^0-9()-]/)) {
             errorMessage = "電話番号には数字、ハイフン、括弧以外の文字は使用できません。";
         } else {
             // 3. 数字のみを抽出し、桁数と先頭文字をチェック
             const digitsOnly = trimmedInput.replace(/[^0-9]/g, ""); // 数字以外の文字をすべて除去

             if (digitsOnly.length < 10) {
                 errorMessage = "電話番号の桁数が不足しています。数字のみで10桁以上必要です。";
             } else if (digitsOnly.length > 11){
            	 errorMessage = "電話番号の桁数が超過しています。数字のみで11桁以下です。";
             }else if (!digitsOnly.startsWith("0")) {
                 errorMessage = "電話番号は0から始まる必要があります。";
             } else {
                  // 4. 書式のチェック（サーバーサイドのロジックを簡易的に再現）
                  //    数字のみで入力された場合、サーバー側でフォーマットされるため、
                  //    クライアント側では「ハイフンや括弧が正しく使われているか」をチェックします。
                  //    ユーザーがハイフン付きで入力した場合に、大まかな形式が合っているかを確認します。
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
     }


     // 5. エラー判定とメッセージ表示
     if (errorMessage) {
         errorElement.textContent = errorMessage;
         errorElement.style.display = 'block';
         inputElement.classList.add('input-error'); // エラー時に枠線を赤くするスタイル用
         return false; // バリデーション失敗
     } else {
         errorElement.style.display = 'none';
         inputElement.classList.remove('input-error');
         return true; // バリデーション成功
     }
 }
 const phoneField = document.getElementById('newTel'); // または 'tabyouinTel' など

 if(phoneField) {
     // フィールドからフォーカスが外れた時にバリデーションを実行
     phoneField.onblur = validatePhoneNumber;

     // フォーム全体の送信時にもバリデーションを実行
     const form = phoneField.closest('form');
     if(form) {
         form.onsubmit = function(event) {
             if (!validatePhoneNumber()) {
                 event.preventDefault(); // バリデーションエラーがあれば送信を中止
                 return false;
             }
             return true;
         }
     }
 }
      
    </script>
</body>
</html>