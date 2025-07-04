<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%-- ここに認証・認可フィルタのためのチェックを入れる --%>

<!DOCTYPE html>
<html>
<head>
    <title>レントゲン技師メニュー</title>
    <link rel="stylesheet" href="css/menu_style.css">
</head>
<body class="role-radiology"> <%-- CSSで色分けするためのクラス --%>
    <div class="menu-container">
        <header class="menu-header">
            <h1>レントゲン技師メニュー</h1>
            <%-- ユーザー情報とログアウトボタン --%>
        </header>
        <nav class="menu-nav">
            <ul>
                <li><a href="RadiologyOrderListServlet">レントゲン指示一覧</a></li>
                <%-- 他のメニュー項目 --%>
                <li><a href="EmployeeChangeOwnPasswordServlet">自身のパスワード変更</a></li>
            </ul>
        </nav>
        <main class="menu-content">
            <p>操作を選択してください。</p>
        </main>
        <%-- フッター --%>
    </div>
</body>
</html>
