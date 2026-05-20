<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="active" value=""/>
</jsp:include>

<h3 class="page-title">仪表盘</h3>
<div class="row">
    <div class="col-md-3 mb-3">
        <div class="card text-bg-primary">
            <div class="card-body text-center">
                <h2>${userCount}</h2>
                <p class="mb-0">用户总数</p>
            </div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="card text-bg-success">
            <div class="card-body text-center">
                <h2>${guideCount}</h2>
                <p class="mb-0">攻略总数</p>
            </div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="card text-bg-info">
            <div class="card-body text-center">
                <h2>${storyCount}</h2>
                <p class="mb-0">故事总数</p>
            </div>
        </div>
    </div>
    <div class="col-md-3 mb-3">
        <div class="card text-bg-warning">
            <div class="card-body text-center">
                <h2>${destinationCount}</h2>
                <p class="mb-0">目的地总数</p>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <h2>${commentCount}</h2>
                <p class="mb-0">评论总数</p>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card text-bg-danger">
            <div class="card-body text-center">
                <h2>${onlineUserCount}</h2>
                <p class="mb-0">当前在线</p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp"/>
