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
                <th>日期</th>
                <th>域名</th>
                <th>URL</th>
                <th>域名类别</th>
                <th>操作</th>
            </tr>
            </thead>
            <tfoot>
            <th>日期</th>
            <th>域名</th>
            <th>URL</th>
            <th>域名类别</th>
            <th>操作</th>
            </tfoot>
        </table>
    </div>
</div>
<script>
    $(document).ready(function () {
        $('#example').DataTable({
            "ajax": "/manage/api/domain",
            "pageLength": 50
        });

        var table = $('#example').DataTable();
        table.on( 'draw', function () {
            $('#example tbody .btn-primary').click(function () {
                console.log($(this).data('resourceid'));
                console.log($(this).data('index'));
                var index = $(this).data('index');
                $.ajax({
                    url:"/manage/api/domain/publish",
                    data:{resourceId:$(this).data('resourceid')},
                    success:function (data) {
                        console.log(data);
                        var rdata = table.row(index).data();
                        rdata[4] = "<button type=\"button\" class=\"btn btn-success\">已审核</button>";
                        table.row(index).data( rdata ).draw();
                    }
                })
            })
        } );

    });
</script>
</body>
</html>
