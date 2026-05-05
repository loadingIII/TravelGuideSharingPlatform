<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="destinations"/>
</jsp:include>

<h3 class="page-title">目的地管理</h3>
<c:if test="${not empty msg}">
    <div class="alert alert-success">${msg}</div>
</c:if>
<table class="table table-striped">
    <thead>
        <tr><th>ID</th><th>名称</th><th>国家</th><th>城市</th><th>攻略数</th><th>操作</th></tr>
    </thead>
    <tbody>
        <c:forEach var="d" items="${pageResult.list}">
            <tr>
                <td>${d.id}</td>
                <td>${d.name}</td>
                <td>${d.country}</td>
                <td>${d.city}</td>
                <td>${d.guidesCount}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/destinations/edit/${d.id}" class="btn btn-sm btn-primary">编辑</a>
                    <form method="post" action="${pageContext.request.contextPath}/admin/destinations/delete/${d.id}" style="display:inline" onsubmit="return confirm('确认删除？')">
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
                <a class="page-link" href="${pageContext.request.contextPath}/admin/destinations?page=${i}">${i}</a>
            </li>
        </c:forEach>
    </ul>
</nav>

<jsp:include page="../common/footer.jsp"/>
