<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="users"/>
</jsp:include>

<h3 class="page-title">编辑用户</h3>
<a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary mb-3">返回列表</a>
<form method="post" action="${pageContext.request.contextPath}/admin/users/edit/${user.id}">
    <div class="mb-3">
        <label class="form-label">用户名</label>
        <input type="text" name="username" class="form-control" value="${user.username}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">手机号</label>
        <input type="text" name="phone" class="form-control" value="${user.phone}">
    </div>
    <div class="mb-3">
        <label class="form-label">邮箱</label>
        <input type="email" name="email" class="form-control" value="${user.email}">
    </div>
    <div class="mb-3">
        <label class="form-label">状态</label>
        <select name="status" class="form-select">
            <option value="1" ${user.status == 1 ? 'selected' : ''}>正常</option>
            <option value="0" ${user.status == 0 ? 'selected' : ''}>禁用</option>
        </select>
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
</form>

<jsp:include page="../common/footer.jsp"/>
