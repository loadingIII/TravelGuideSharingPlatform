<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="comments"/>
</jsp:include>

<h3 class="page-title">评论管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>攻略ID</th><th>评论者</th><th>内容</th><th>时间</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="c" items="${pageResult.list}">
            <tr>
                <td>${c.id}</td>
                <td>${c.guideId}</td>
                <td>${c.authorName}</td>
                <td>${c.content.length() > 50 ? c.content.substring(0, 50).concat('...') : c.content}</td>
                <td>${c.createdAt}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/admin/comments/delete/${c.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
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
                <a class="page-link" href="${pageContext.request.contextPath}/admin/comments?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
