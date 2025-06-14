<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.EmployeeBean" %>

<%
    // セッションからログインユーザー情報を取得
    EmployeeBean user = (EmployeeBean) session.getAttribute("loggedInUser");
    // 未ログイン、または受付ロール(ID:1と仮定)でない場合はログインページにリダイレクト
    if (user == null || user.getRole() != 1) {
        response.sendRedirect("LoginServlet");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>受付メニュー</title>
    <link rel="stylesheet" href="css/menu_style.css">
</head>
<body class="role-reception">

    <div class="menu-container">
        <header class="menu-header">
            <h1>受付メニュー</h1>
            <div class="user-info">
                ようこそ、
                <span class="user-name">
                    <c:out value="${sessionScope.loggedInUser.emplname} ${sessionScope.loggedInUser.empfname}"/>
                </span> さん
                <a href="LogoutServlet" class="logout-button">ログアウト</a>
            </div>
        </header>

        <nav class="menu-nav">
            <ul>
                <li><a href="ReceptionRegisterPatientServlet">新規患者登録</a></li>
                <li><a href="ReceptionListPatientsServlet">患者一覧・検索・保険証変更</a></li>
                <li><a href="EmployeeChangeOwnPasswordServlet">自身のパスワード変更</a></li>
            </ul>
        </nav>

        <main class="menu-content">
            <p>受付業務用メニューです。上のメニューから操作を選択してください。</p>
        </main>

        <footer class="menu-footer">
            <p>&copy; 2025 医療機関向け医師・受付・患者管理システム</p>
        </footer>
    </div>

</body>
</html>