<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="destinations"/>
</jsp:include>

<h3 class="page-title">编辑目的地</h3>
<a href="${pageContext.request.contextPath}/admin/destinations" class="btn btn-secondary mb-3">返回列表</a>
<form method="post" action="${pageContext.request.contextPath}/admin/destinations/edit/${destination.id}">
    <div class="mb-3">
        <label class="form-label">名称</label>
        <input type="text" name="name" class="form-control" value="${destination.name}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">国家</label>
        <input type="text" name="country" class="form-control" value="${destination.country}">
    </div>
    <div class="mb-3">
        <label class="form-label">城市</label>
        <input type="text" name="city" class="form-control" value="${destination.city}">
    </div>
    <div class="mb-3">
        <label class="form-label">描述</label>
        <textarea name="description" class="form-control" rows="5">${destination.description}</textarea>
    </div>
    <div class="mb-3">
        <label class="form-label">封面图片URL</label>
        <input type="text" name="coverImageUrl" class="form-control" value="${destination.coverImageUrl}">
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
</form>

<jsp:include page="../common/footer.jsp"/>
