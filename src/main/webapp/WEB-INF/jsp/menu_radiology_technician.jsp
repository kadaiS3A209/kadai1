<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.EmployeeBean" %>

<%
    // セッションからログインユーザー情報を取得
    EmployeeBean user = (EmployeeBean) session.getAttribute("loggedInUser");
    // 未ログイン、またはレントゲン技師ロール(ID:4と仮定)でない場合はログインページにリダイレクト
    if (user == null || user.getRole() != 4) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>レントゲン技師メニュー</title>
    <%-- 共通CSSファイルを読み込みます --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu_style.css">
</head>
<%-- bodyタグに専用のクラスを指定して、CSSで色分けなどができるようにします --%>
<body class="role-radiology">

    <div class="menu-container">
        <header class="menu-header">
            <h1>レントゲン技師メニュー</h1>
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
                <li><a href="RadiologyOrderListServlet">レントゲン指示一覧・写真登録</a></li>
                <li><a href="EmployeeChangeOwnPasswordServlet">自身のパスワード変更</a></li>
            </ul>
        </nav>

        <main class="menu-content">
            <p>レントゲン技師用のメニューです。上のメニューから操作を選択してください。</p>
        </main>

        <footer class="menu-footer">
            <p>&copy; 2025 医療機関向け医師・受付・患者管理システム</p>
        </footer>
    </div>

</body>
</html>
