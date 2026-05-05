<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="users"/>
</jsp:include>

<h3 class="page-title">用户管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>用户名</th><th>手机号</th><th>邮箱</th><th>状态</th><th>注册时间</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="u" items="${pageResult.list}">
            <tr>
                <td>${u.id}</td>
                <td>${u.username}</td>
                <td>${u.phone}</td>
                <td>${u.email}</td>
                <td>${u.status == 1 ? '正常' : '禁用'}</td>
                <td>${u.createdAt}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/users/edit/${u.id}" class="btn btn-sm btn-primary">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/users/delete/${u.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
                        <button type="submit" class="btn btn-sm btn-danger">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<nav>
    <ul class="pagination">
        <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
            <li class="page-item ${i == pageResult.page ? 'active' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/users?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
