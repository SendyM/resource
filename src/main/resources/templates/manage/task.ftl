<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>数据源管理后台</title>
<#include "../header.ftl"/>
</head>

<body>
<div class="container-fluid">
    <div class="row">
        <table id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
            <thead>
            <tr>
                <th>来源</th>
                <th>域名</th>
                <th>批次</th>
                <th>录入时间</th>
                <th>更新次数</th>
            </tr>
            </thead>
            <tfoot>
            <th>来源</th>
            <th>域名</th>
            <th>批次</th>
            <th>录入时间</th>
            <th>更新次数</th>
            </tfoot>
        </table>
    </div>
</div>
<script>
    $(document).ready(function () {
        $('#example').DataTable({
            "ajax": "/manage/api/task",
            "pageLength": 50
        });
    });
</script>
</body>
</html>
