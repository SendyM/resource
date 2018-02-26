<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>数据源管理后台</title>
</head>

<body>
<div class="container-fluid">
    <div class="row">
        <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
            <ul class="nav nav-pills flex-column">
                <li class="nav-item">
                    <a class="nav-link active" href="#">录入批次管理 <span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/manage/task" target="content">批次</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/resource" target="content">数据源统计</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/resource" target="content">数据源发现</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/resource" target="content">数据源分类</a>
                </li>
            </ul>
        </nav>


        <iframe role="main" id="content" class="col-sm-9 ml-sm-auto col-md-10 pt-3" src="/manage/task"
                style="border: none; height: 1315px;" name="content" class="thin-scroll"></iframe>

        </iframe>
    </div>
</div>
</body>
</html>
