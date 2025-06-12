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
    .whiteboard-container { flex: 2; }
    h2 { margin-top: 0; color: #555; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 15px; }
    #memo-pad { width: 100%; height: 100%; border: 1px solid #ccc; border-radius: 4px; font-size: 16px; padding: 10px; box-sizing: border-box; resize: none; }
    #whiteboard { border: 1px solid #ccc; border-radius: 4px; cursor: crosshair; touch-action: none; }
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
            <h2>共有メモ</h2>
            <textarea id="memo-pad" placeholder="ここにテキストを入力..."></textarea>
        </div>
        <div class="panel whiteboard-container">
            <h2>共有ホワイトボード</h2>
            <div class="controls">
                <button id="new-btn" class="tool-btn">新規</button>
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

    <!-- ★一覧表示用のモーダルウィンドウ -->
    <div id="load-modal" class="modal-overlay">
        <div class="modal-content">
            <button class="modal-close">&times;</button>
            <h3>保存済みデータ一覧</h3>
            <table id="memo-list-table">
                <thead>
                    <tr><th>タイトル</th><th>保存日時</th><th>操作</th></tr>
                </thead>
                <tbody>
                    <!-- データはここに動的に追加される -->
                </tbody>
            </table>
        </div>
    </div>

<script>
$(document).ready(function() {
    // --- WebSocketのセットアップ ---
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    const contextPath = '<%= request.getContextPath() %>';
    const wsUrl = protocol + '//' + host + contextPath + '/whiteboard';
    console.log('Connecting to WebSocket at: ' + wsUrl);

    let ws;
    try { ws = new WebSocket(wsUrl); } catch (e) {
        console.error("WebSocketの初期化に失敗しました。", e);
        $('#status').text('エラー: サーバーに接続できません').css('background-color', 'red');
        return;
    }
    
    // --- 変数定義 ---
    const $memoPad = $('#memo-pad'), $status = $('#status'), canvas = document.getElementById('whiteboard'), ctx = canvas.getContext('2d'),
          $whiteboardContainer = $('.whiteboard-container'), $controls = $('.controls'), $h2 = $whiteboardContainer.find('h2'),
          $colorPicker = $('#colorPicker'), $lineWidthPicker = $('#lineWidthPicker'), $lineWidthValue = $('#lineWidthValue'),
          $penBtn = $('#pen-btn'), $eraserBtn = $('#eraser-btn'), $newBtn = $('#new-btn'), $saveAsBtn = $('#save-as-btn'),
          $loadListBtn = $('#load-list-btn'), $loadModal = $('#load-modal');

    let isDrawing = false, lastX = 0, lastY = 0, currentMode = 'pen';
    
    // --- WebSocket イベントハンドラ ---
    ws.onopen = () => {
        console.log("WebSocket connection established");
        $status.text('接続完了').css('background-color', 'green');
    };

    ws.onmessage = (event) => {
        try {
            const msg = JSON.parse(event.data);
            switch (msg.type) {
                case 'text':
                    if ($memoPad.val() !== msg.data) $memoPad.val(msg.data);
                    break;
                case 'draw':
                    drawOnCanvas(msg.x1, msg.y1, msg.x2, msg.y2, msg.color, msg.lineWidth, msg.mode);
                    break;
                case 'list_updated': // ★保存リスト受信
                    updateMemoListModal(msg.data);
                    break;
                case 'load_memo_success': // ★データ読み込み成功
                    applyLoadedData(msg.data);
                    break;
                case 'clear': // ★「新規」で画面クリア
                    clearAll();
                    break;
            }
        } catch(e) { console.error("受信メッセージの解析に失敗しました: ", event.data, e); }
    };

    ws.onclose = () => {
        console.log("WebSocket connection closed");
        $status.text('接続が切れました').css('background-color', 'red');
    };
    ws.onerror = (error) => {
        console.error("WebSocket Error: ", error);
        $status.text('エラーが発生しました').css('background-color', 'red');
    };
    
    // --- イベントハンドラ ---
    $newBtn.on('click', () => { // ★新規ボタン
        if (confirm('現在の内容はクリアされます。よろしいですか？')) {
            ws.send(JSON.stringify({ type: 'clear' }));
        }
    });

    $saveAsBtn.on('click', () => { // ★名前を付けて保存ボタン
        const title = prompt('保存する名前を入力してください:', '無題のメモ');
        if (title) {
            ws.send(JSON.stringify({
                type: 'save_as',
                title: title,
                text: $memoPad.val(),
                image: canvas.toDataURL()
            }));
            alert('「' + title + '」という名前で保存しました。');
        }
    });

    $loadListBtn.on('click', () => { // ★一覧読込ボタン
        ws.send(JSON.stringify({ type: 'get_list' }));
        $loadModal.fadeIn();
    });

    $('.modal-close, .modal-overlay').on('click', (e) => { // ★モーダルを閉じる
        if (e.target === e.currentTarget) $loadModal.fadeOut();
    });
    
    // ★動的に生成される「読込」ボタンのためのイベント委譲
    $(document).on('click', '.load-item-btn', function() {
        const id = $(this).data('id');
        ws.send(JSON.stringify({ type: 'load_memo', id: id }));
        $loadModal.fadeOut();
    });
    
    // --- 既存のイベントハンドラ ---
    $memoPad.on('input', () => ws.send(JSON.stringify({ type: 'text', data: $memoPad.val() })));
    $penBtn.on('click', () => { currentMode = 'pen'; $penBtn.addClass('active'); $eraserBtn.removeClass('active'); });
    $eraserBtn.on('click', () => { currentMode = 'eraser'; $eraserBtn.addClass('active'); $penBtn.removeClass('active'); });
    $lineWidthPicker.on('input', () => $lineWidthValue.text($lineWidthPicker.val()));
    $(canvas).on('mousedown touchstart', (e) => { e.preventDefault(); const pos = getMousePos(e); isDrawing = true; [lastX, lastY] = [pos.x, pos.y]; });
    $(canvas).on('mousemove touchmove', (e) => {
        if (!isDrawing) return; e.preventDefault(); const pos = getMousePos(e);
        drawOnCanvas(lastX, lastY, pos.x, pos.y, $colorPicker.val(), $lineWidthPicker.val(), currentMode);
        ws.send(JSON.stringify({ type: 'draw', x1: lastX, y1: lastY, x2: pos.x, y2: pos.y, color: $colorPicker.val(), lineWidth: $lineWidthPicker.val(), mode: currentMode }));
        [lastX, lastY] = [pos.x, pos.y];
    });
    $(canvas).on('mouseup touchend mouseleave', () => isDrawing = false);
    $(window).on('resize', resizeCanvas);

    // --- 補助関数 ---
    const clearAll = () => { // ★クリア処理
        $memoPad.val('');
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    };

    const applyLoadedData = (data) => { // ★読込データ適用
        clearAll();
        $memoPad.val(data.text);
        const img = new Image();
        img.onload = () => ctx.drawImage(img, 0, 0);
        img.src = data.image;
    };

    const updateMemoListModal = (list) => { // ★モーダル内容更新
        const $tbody = $('#memo-list-table tbody');
        $tbody.empty();
        if (list.length === 0) {
            $tbody.append('<tr><td colspan="3">保存されたデータはありません。</td></tr>');
            return;
        }
        list.forEach(item => {
            const row = `<tr>
                <td>${$('<div>').text(item.title).html()}</td>
                <td>${item.createdAt}</td>
                <td><button class="tool-btn load-item-btn" data-id="${item.id}">読込</button></td>
            </tr>`;
            $tbody.append(row);
        });
    };
    
    const resizeCanvas = () => { /* ... 変更なし ... */ };
    const drawOnCanvas = (x1, y1, x2, y2, color, lineWidth, mode) => { /* ... 変更なし ... */ };
    const getMousePos = (e) => { /* ... 変更なし ... */ };

    setTimeout(() => resizeCanvas(), 100);
});
</script>
</body>
</html>