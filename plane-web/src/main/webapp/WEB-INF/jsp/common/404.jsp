<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/9/13
  Time: 11:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>您找的页面丢失</title>
    <link rel="stylesheet" type="text/css" href="../../../res/css/404style.css" />
</head>

<body>
<div class="layout">
    <div class="main">
        <div class="err">
            <p class="err_text">非常抱歉，您访<br>问的页面不存在</p>
            <a href="${s.base}/home.action" class="err_back">返回首页</a>
        </div>
    </div>
</div>
</body>
</html>
