<head>
    <jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf"/>
    <title>Register</title>
    <style>
        .bd-placeholder-img {
            font-size: 1.125rem;
            text-anchor: middle;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }

        @media (min-width: 768px) {
            .bd-placeholder-img-lg {
                font-size: 3.5rem;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <form class="" method="post" action="/do-register">
        <h1 class="h3 mb-3 font-weight-normal">注册</h1>
        <label for="name" class="sr-only">用户名</label>
        <input type="text" id="name" name="name" class="form-control"
               placeholder="用户名" required autofocus>
        <label for="password" class="sr-only">Password</label>
        <input type="password" id="password" name="password" class="form-control"
               placeholder="请输入密码" required>

        <label for="email" class="sr-only">Email</label>
        <input type="email" id="email" name="email" class="form-control"
               placeholder="请输入邮箱" required>

        <label for="phoneNumber" class="sr-only">phoneNumber</label>
        <input type="number" id="phoneNumber" name="phoneNumber" class="form-control"
               placeholder="请输入手机号" required>

        <p style="color: red">${error}</p>
        <input class="btn btn-lg btn-primary btn-block" value="Register" type="submit">
        <p class="mt-5 mb-3 text-muted">&copy; 2017-2021</p>
    </form>
</div>
<script type="text/javascript">
    var msg = "${requestScope.message}";
    if (msg.length > 0) {
        alert(msg);
    }
</script>
</body>