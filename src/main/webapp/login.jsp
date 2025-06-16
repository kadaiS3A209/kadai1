<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>リアルタイム共有メモ＆ホワイトボード</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<style>

body {
font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
display: flex;
flex-direction: column;
align-items: center;
background-color: #f0f2f5;
margin: 0;
padding: 20px;
height: 100vh;
box-sizing: border-box;
}
h1 { color: #333; margin-bottom: 20px; }
.container { display: flex; width: 100%; max-width: 1400px; height: calc(100% - 80px); gap: 20px; }
.panel { background-color: #fff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); padding: 20px; display: flex; flex-direction: column; overflow: hidden; }
.memo-container { flex: 1; }
.whiteboard-container { flex: 2; display: flex; flex-direction: column;}
h2 { margin-top: 0; color: #555; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 15px; }
#memo-pad { width: 100%; height: 100%; border: 1px solid #ccc; border-radius: 4px; font-size: 16px; padding: 10px; box-sizing: border-box; resize: none; }
#whiteboard { border: 1px solid #ccc; border-radius: 4px; cursor: crosshair; touch-action: none; flex-grow: 1; /* 親要素の残りの高さを全て使う */}
.controls { display: flex; flex-wrap: wrap; gap: 10px; align-items: center; margin-bottom: 15px; flex-shrink: 0; }
.controls label { font-size: 14px; }
.controls input[type="color"], .controls input[type="range"] { vertical-align: middle; }
.tool-btn { padding: 5px 12px; border: 1px solid #ccc; background-color: #f9f9f9; cursor: pointer; border-radius: 4px; font-size: 14px; transition: all 0.2s ease-in-out; }
.tool-btn.active { background-color: #0d6efd; color: white; border-color: #0d6efd; }
#save-as-btn { background-color: #198754; color: white; border-color: #198754; }
#new-btn { background-color: #ffc107; color: black; border-color: #ffc107; }
#load-list-btn { background-color: #6c757d; color: white; border-color: #6c757d; }
#status { position: fixed; bottom: 10px; left: 10px; background-color: #333; color: white; padding: 5px 10px; border-radius: 4px; font-size: 12px; opacity: 0.8; z-index: 1000; }

/* ★モーダルウィンドウ用のスタイル */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 998; display: none; justify-content: center; align-items: center; }
.modal-content { background: white; padding: 25px; border-radius: 8px; width: 90%; max-width: 600px; max-height: 80vh; overflow-y: auto; position: relative; }
.modal-close { position: absolute; top: 10px; right: 15px; font-size: 24px; cursor: pointer; border: none; background: none; }
#memo-list-table { width: 100%; border-collapse: collapse; margin-top: 15px; }
#memo-list-table th, #memo-list-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
#memo-list-table th { background-color: #f2f2f2; }

</style>
</head>
<body>
    <h1>リアルタイム共有メモ＆ホワイトボード</h1>
    <div class="container">
        <div class="panel memo-container">
            <h2>共有メモ <span id="current-memo-info" style="font-size: 0.7em; color: #555;">(新規)</span></h2>
            <textarea id="memo-pad" placeholder="ここにテキストを入力..."></textarea>
        </div>
        <div class="panel whiteboard-container">
            <h2>共有ホワイトボード</h2>
            <div class="controls">
                <button id="new-btn" class="tool-btn">新規</button>
                <button id="save-btn" class="tool-btn">上書き保存</button> <%-- ★追加 --%>
                <button id="save-as-btn" class="tool-btn">名前を付けて保存</button>
                <button id="load-list-btn" class="tool-btn">一覧読込</button>
                <hr style="width:100%; border:none; border-top:1px solid #eee; margin: 5px 0;">
                <button id="pen-btn" class="tool-btn active">ペン</button>
                <button id="eraser-btn" class="tool-btn">消しゴム</button>
                <label for="colorPicker">色:</label>
                <input type="color" id="colorPicker" value="#000000">
                <label for="lineWidthPicker">太さ:</label>
                <input type="range" id="lineWidthPicker" min="1" max="20" value="3">
                <span id="lineWidthValue">3</span>px
            </div>
            <canvas id="whiteboard"></canvas>
        </div>
    </div>
    <div id="status">接続待機中...</div>

    <div id="load-modal" class="modal-overlay">
        <div class="modal-content">
            <button class="modal-close">&times;</button>
            <h3>保存済みデータ一覧</h3>
            <%-- ↓↓↓ JavaScriptがこのIDを探しています ↓↓↓ --%>
            <table id="memo-list-table">
                <thead>
                    <tr><th>タイトル</th><th>保存日時</th><th>操作</th></tr>
                </thead>
                <tbody>
                    </tbody>
            </table>
        </div>
    </div>

<script>
$(document).ready(function() {
    // --- 変数定義 ---
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    const contextPath = '<%= request.getContextPath() %>';
    const wsUrl = protocol + '//' + host + contextPath + '/whiteboard';
    
    // jQueryオブジェクトのキャッシュ
    const $memoPad = $('#memo-pad'), $status = $('#status'), 
          $whiteboardContainer = $('.whiteboard-container'), $controls = $('.controls'),
          $colorPicker = $('#colorPicker'), $lineWidthPicker = $('#lineWidthPicker'), 
          $lineWidthValue = $('#lineWidthValue'), $penBtn = $('#pen-btn'), 
          $eraserBtn = $('#eraser-btn'), $newBtn = $('#new-btn'),
          $saveBtn = $('#save-btn'), $saveAsBtn = $('#save-as-btn'), 
          $loadListBtn = $('#load-list-btn'), $loadModal = $('#load-modal'),
          $currentMemoInfo = $('#current-memo-info');

    // Canvas関連の変数
    const canvas = document.getElementById('whiteboard');
    const ctx = canvas.getContext('2d');

    // 状態管理用の変数
    let isDrawing = false, lastX = 0, lastY = 0, currentMode = 'pen';
    let currentMemoId = null;
    let currentMemoTitle = '無題のメモ';
    let ws;

    //================================================
    // ★★★ 補助関数 (Helper Functions) ★★★
    //================================================
    const clearAll = () => {
        $memoPad.val('');
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    };

    const applyLoadedData = (data) => {
        clearAll();
        $memoPad.val(data.text);
        if (data.image) {
            const img = new Image();
            img.onload = () => ctx.drawImage(img, 0, 0);
            img.src = data.image;
        }
    };

    const updateMemoListModal = (list) => {
        const $tbody = $('#memo-list-table tbody');
        $tbody.empty();
        if (!list || list.length === 0) {
            $tbody.append('<tr><td colspan="3">保存されたデータはありません。</td></tr>');
            return;
        }
        list.forEach(item => {
            const escapedTitle = $('<div>').text(item.title).html();
            const row = '<tr>' +
                '<td>' + escapedTitle + '</td>' +
                '<td>' + item.createdAt + '</td>' +
                '<td><button class="tool-btn load-item-btn" data-id="' + item.id + '">読込</button></td>' +
                '</tr>';
            $tbody.append(row);
        });
    };

    const resizeCanvas = () => {
        const imageData = canvas.toDataURL();
        const containerPadding = 40; 
        const controlsHeight = $controls.outerHeight(true) || 0;
        const h2Height = $whiteboardContainer.find('h2').outerHeight(true) || 0;
        
        const availableHeight = $whiteboardContainer.height() - h2Height - controlsHeight - containerPadding;
        const availableWidth = $whiteboardContainer.width() - containerPadding;

        console.log("Resizing canvas to: " + availableWidth + " x " + availableHeight);

        if (availableWidth > 0 && availableHeight > 0) {
            canvas.width = availableWidth;
            canvas.height = availableHeight;
        }

        const img = new Image();
        img.onload = () => {
            ctx.drawImage(img, 0, 0);
            console.log("Canvas content restored after resize.");
        };
        img.src = imageData;
    };
    
    const drawOnCanvas = (x1, y1, x2, y2, color, lineWidth, mode) => {
        const originalCompositeOperation = ctx.globalCompositeOperation;
        const originalStrokeStyle = ctx.strokeStyle;
        ctx.beginPath();
        ctx.lineWidth = lineWidth;
        ctx.lineCap = 'round';
        if (mode === 'eraser') {
            ctx.globalCompositeOperation = 'destination-out';
        } else {
            ctx.globalCompositeOperation = 'source-over';
            ctx.strokeStyle = color;
        }
        ctx.moveTo(x1, y1);
        ctx.lineTo(x2, y2);
        ctx.stroke();
        ctx.globalCompositeOperation = originalCompositeOperation;
        ctx.strokeStyle = originalStrokeStyle;
    };
    
    const getMousePos = (e) => {
        const rect = canvas.getBoundingClientRect();
        const event = e.originalEvent.touches ? e.originalEvent.touches[0] : e;
        return {
            x: event.clientX - rect.left,
            y: event.clientY - rect.top
        };
    };

    //================================================
    // ★★★ WebSocketのセットアップとイベントハンドラ ★★★
    //================================================
    try {
        ws = new WebSocket(wsUrl);
    } catch (e) {
        console.error("WebSocketの初期化に失敗しました。", e);
        $status.text('エラー: サーバーに接続できません').css('background-color', 'red');
        return;
    }
    
    ws.onopen = () => { $status.text('接続完了').css('background-color', 'green'); };
    ws.onclose = () => { $status.text('接続が切れました').css('background-color', 'red'); };
    ws.onerror = (error) => { $status.text('エラーが発生しました').css('background-color', 'red'); console.error("WebSocket Error:", error); };
    
    ws.onmessage = (event) => {
        try {
            const msg = JSON.parse(event.data);
            switch (msg.type) {
                case 'text':
                    if (document.activeElement !== $memoPad[0]) $memoPad.val(msg.data);
                    break;
                case 'draw':
                    drawOnCanvas(msg.x1, msg.y1, msg.x2, msg.y2, msg.color, msg.lineWidth, msg.mode);
                    break;
                case 'list_updated':
                    updateMemoListModal(msg.data);
                    break;
                case 'load_memo_success':
                    // msg.data ではなく、msg オブジェクト全体を渡す
                    applyLoadedData(msg); 
                    
                    // 読み込んだメモのIDとタイトルを保持
                    currentMemoId = msg.id;
                    currentMemoTitle = msg.title;
                    $currentMemoInfo.text('(ID: ' + currentMemoId + ' - ' + currentMemoTitle + ')');
                    break;
                case 'save_success':
                    if (msg.newId) {
                        currentMemoId = msg.newId;
                        currentMemoTitle = msg.newTitle;
                    }
                    alert('保存しました。');
                    $currentMemoInfo.text('(ID: ' + currentMemoId + ' - ' + currentMemoTitle + ')');
                    break;
                case 'clear':
                    clearAll();
                    currentMemoId = null;
                    currentMemoTitle = '無題のメモ';
                    $currentMemoInfo.text('(新規)');
                    break;
            }
        } catch(e) {
            // ★重要★ JSON解析に失敗した場合、エラーと受信データをコンソールに出力
            console.error("受信メッセージの解析に失敗しました:", e);
            console.error("失敗したデータ:", event.data);
        }
    };
    
    //================================================
    // ★★★ UIイベントハンドラ (User Interface Event Handlers) ★★★
    //================================================
    $newBtn.on('click', () => {
        if (confirm('現在の内容はクリアされます。よろしいですか？')) {
            ws.send(JSON.stringify({ type: 'clear' }));
        }
    });

    $saveBtn.on('click', () => {
        if (currentMemoId) {
            ws.send(JSON.stringify({ type: 'save_overwrite', id: currentMemoId, title: currentMemoTitle, text: $memoPad.val(), image: canvas.toDataURL() }));
        } else {
            $saveAsBtn.trigger('click');
        }
    });

    $saveAsBtn.on('click', () => {
        const title = prompt('保存する名前を入力してください:', currentMemoTitle === '無題のメモ' ? '' : currentMemoTitle);
        if (title && title.trim() !== '') {
            currentMemoTitle = title.trim();
            ws.send(JSON.stringify({ type: 'save_as', title: currentMemoTitle, text: $memoPad.val(), image: canvas.toDataURL() }));
        }
    });

    // 「一覧読込」ボタンが押されたら、データを要求しモーダルを表示
    $('#load-list-btn').on('click', () => {
        console.log("「一覧読込」ボタンがクリックされました。"); // デバッグログ
        ws.send(JSON.stringify({ type: 'get_list' }));
        $('#load-modal').fadeIn();
    });

    // ▼▼▼【最重要修正箇所】モーダル関連のクリックイベントを、より確実なイベントデリゲーションで設定 ▼▼▼
    $('#load-modal').on('click', function(event) {
        // クリックされた要素を特定
        const target = event.target;
        console.log("モーダル内でクリックが発生しました。ターゲット:", target); // デバッグログ

        // (1) ×ボタン (class="modal-close") が押された場合
        if ($(target).hasClass('modal-close')) {
            console.log("×ボタンがクリックされました。モーダルを閉じます。");
            $('#load-modal').fadeOut();
        }
        
        // (2) モーダルの外側の黒い背景が押された場合
        // event.currentTarget はイベントが設定された要素(#load-modal)を指す
        if (target === event.currentTarget) {
            console.log("モーダルの背景がクリックされました。モーダルを閉じます。");
            $('#load-modal').fadeOut();
        }
        
        // (3) 読込ボタン (class="load-item-btn") が押された場合
        if ($(target).hasClass('load-item-btn')) {
            console.log("「読込」ボタンがクリックされました。"); // ★デバッグログ1
            
            const id = $(target).data('id');
            console.log("読み込むメモのID:", id); // ★デバッグログ2

            if (id) {
                const messageToSend = JSON.stringify({ type: 'load_memo', id: id });
                console.log("サーバーへ送信するメッセージ:", messageToSend); // ★デバッグログ3
                ws.send(messageToSend);
                $('#load-modal').fadeOut();
            } else {
                console.error("読込ボタンからIDが取得できませんでした。");
            }
        }
    });
    // ▲▲▲ ここまで修正 ▲▲▲
    
    $memoPad.on('input', () => ws.send(JSON.stringify({ type: 'text', data: $memoPad.val() })));
    $penBtn.on('click', () => { currentMode = 'pen'; $penBtn.addClass('active'); $eraserBtn.removeClass('active'); });
    $eraserBtn.on('click', () => { currentMode = 'eraser'; $eraserBtn.addClass('active'); $penBtn.removeClass('active'); });
    $lineWidthPicker.on('input', () => $lineWidthValue.text($lineWidthPicker.val()));
    
    // Canvasの描画イベント
    $(canvas).on('mousedown touchstart', (e) => { e.preventDefault(); const pos = getMousePos(e); isDrawing = true; [lastX, lastY] = [pos.x, pos.y]; });
    $(canvas).on('mousemove touchmove', (e) => {
        if (!isDrawing) return;
        e.preventDefault();
        const pos = getMousePos(e);
        drawOnCanvas(lastX, lastY, pos.x, pos.y, $colorPicker.val(), $lineWidthPicker.val(), currentMode);
        ws.send(JSON.stringify({ type: 'draw', x1: lastX, y1: lastY, x2: pos.x, y2: pos.y, color: $colorPicker.val(), lineWidth: $lineWidthPicker.val(), mode: currentMode }));
        [lastX, lastY] = [pos.x, pos.y];
    });
    $(canvas).on('mouseup touchend mouseleave', () => isDrawing = false);
    
    // ウィンドウリサイズイベント
    $(window).on('resize', resizeCanvas);

    // 初期化
    setTimeout(() => resizeCanvas(), 100);
});
</script>
</body>
</html>