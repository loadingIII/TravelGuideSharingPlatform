<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../common/header.jsp">
    <jsp:param name="active" value="stories"/>
</jsp:include>

<h3 class="page-title">编辑故事</h3>
<a href="${pageContext.request.contextPath}/admin/stories" class="btn btn-secondary mb-3">返回列表</a>
<form method="post" action="${pageContext.request.contextPath}/admin/stories/edit/${story.id}">
    <div class="mb-3">
        <label class="form-label">作者</label>
        <input type="text" class="form-control" value="${story.authorName}" disabled>
    </div>
    <div class="mb-3">
        <label class="form-label">内容</label>
        <textarea name="content" class="form-control" rows="10">${story.content}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">保存</button>
</form>

<jsp:include page="../common/footer.jsp"/>
