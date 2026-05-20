<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="stories"/>
</jsp:include>

<h3 class="page-title">故事管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>作者</th><th>内容摘要</th><th>点赞数</th><th>发布时间</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="s" items="${pageResult.list}">
            <tr>
                <td>${s.id}</td>
                <td>${s.authorName}</td>
                <td>${s.content.length() > 50 ? s.content.substring(0, 50).concat('...') : s.content}</td>
                <td>${s.likesCount}</td>
                <td>${s.publishedAt}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/admin/stories/delete/${s.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
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
                <a class="page-link" href="${pageContext.request.contextPath}/admin/stories?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
