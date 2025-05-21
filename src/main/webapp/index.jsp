<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ログイン</title>
<style>
    body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f4f4f4; margin: 0;}
    .login-container { background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); width: 320px; }
    h1 { text-align: center; color: #333; margin-bottom: 20px; }
    .form-group { margin-bottom: 15px; }
    label { display: block; margin-bottom: 5px; color: #555; }
    input[type="text"], input[type="password"] {
        width: calc(100% - 20px); /* padding考慮 */
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }
    .button { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
    .button:hover { background-color: #0056b3; }
    .error-message { color: red; text-align: center; margin-top:10px; font-size: 0.9em; }
</style>
</head>
<body>
    <div class="login-container">
        <h1>ログイン</h1>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
            <p class="error-message"><%= errorMessage %></p>
        <%
            }
        %>
        <form action="LoginServlet" method="post">
            <div class="form-group">
                <label for="userId">ユーザーID:</label>
                <input type="text" id="userId" name="userId" value="${not empty param.userId ? param.userId : ''}" required maxlength="8">
            </div>
            <div class="form-group">
                <label for="password">パスワード:</label>
                <%-- [span_0](start_span)--%><input type="password" id="password" name="password" required> <%-- type="password" でシークレット表示[span_0](end_span) --%>
            </div>
            <button type="submit" class="button">ログイン</button>
        </form>
    </div>
    

    
    
    
</body>
</html>
