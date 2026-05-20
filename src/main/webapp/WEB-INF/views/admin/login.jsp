<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>攻略后台管理 - 登录</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f5f5f5; }
        .login-card { max-width: 400px; margin: 100px auto; }
    </style>
</head>
<body>
<div class="container">
    <div class="login-card">
        <div class="card shadow">
            <div class="card-body p-5">
                <h3 class="text-center mb-4">攻略后台管理</h3>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger">${error}</div>
                <% } %>
                <form method="post" action="${pageContext.request.contextPath}/admin/login">
                    <div class="mb-3">
                        <label class="form-label">用户名</label>
                        <input type="text" name="username" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">密码</label>
                        <input type="password" name="password" class="form-control" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">登录</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
