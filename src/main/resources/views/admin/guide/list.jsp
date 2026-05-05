<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="guides"/>
</jsp:include>

<h3 class="page-title">攻略管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>标题</th><th>作者</th><th>目的地</th><th>点赞数</th><th>发布时间</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="g" items="${pageResult.list}">
            <tr>
                <td>${g.id}</td>
                <td>${g.title}</td>
                <td>${g.authorName}</td>
                <td>${g.destinationName}</td>
                <td>${g.likesCount}</td>
                <td>${g.publishedAt}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/guides/edit/${g.id}" class="btn btn-sm btn-primary">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/guides/delete/${g.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
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
                <a class="page-link" href="${pageContext.request.contextPath}/admin/guides?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
