<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>旅游管理后台</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { margin: 0; padding: 0; background-color: #f8f9fa; }
        .sidebar {
            position: fixed; top: 0; left: 0; bottom: 0;
            width: 220px; background-color: #343a40; color: #fff;
            padding-top: 20px; z-index: 100;
        }
        .sidebar h5 { padding: 0 20px 20px; margin: 0; font-size: 18px; border-bottom: 1px solid #495057; }
        .sidebar a {
            display: block; padding: 10px 20px; color: #adb5bd;
            text-decoration: none; font-size: 14px;
        }
        .sidebar a:hover, .sidebar a.active { color: #fff; background-color: #495057; }
        .content { margin-left: 220px; padding: 20px 30px; }
        .page-title { margin-bottom: 20px; padding-bottom: 10px; border-bottom: 1px solid #dee2e6; }
    </style>
</head>
<body>
<div class="sidebar">
    <h5>旅游管理后台</h5>
    <a href="${pageContext.request.contextPath}/admin" class="${empty param.active ? 'active' : ''}">仪表盘</a>
    <a href="${pageContext.request.contextPath}/admin/users" ${param.active == 'users' ? 'class="active"' : ''}>用户管理</a>
    <a href="${pageContext.request.contextPath}/admin/guides" ${param.active == 'guides' ? 'class="active"' : ''}>攻略管理</a>
    <a href="${pageContext.request.contextPath}/admin/stories" ${param.active == 'stories' ? 'class="active"' : ''}>故事管理</a>
    <a href="${pageContext.request.contextPath}/admin/destinations" ${param.active == 'destinations' ? 'class="active"' : ''}>目的地管理</a>
    <a href="${pageContext.request.contextPath}/admin/comments" ${param.active == 'comments' ? 'class="active"' : ''}>评论管理</a>
    <a href="${pageContext.request.contextPath}/admin/logout" style="position:absolute;bottom:20px;left:0;right:0;">退出登录</a>
</div>
<div class="content">
