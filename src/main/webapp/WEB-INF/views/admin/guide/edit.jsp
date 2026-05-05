<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="guides"/>
</jsp:include>

<h3 class="page-title">编辑攻略</h3>
<a href="${pageContext.request.contextPath}/admin/guides" class="btn btn-secondary mb-3">返回列表</a>
<form method="post" action="${pageContext.request.contextPath}/admin/guides/edit/${guide.id}">
    <div class="mb-3">
        <label class="form-label">标题</label>
        <input type="text" name="title" class="form-control" value="${guide.title}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">摘要</label>
        <textarea name="summary" class="form-control" rows="5">${guide.summary}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
</form>

<jsp:include page="../common/footer.jsp"/>
