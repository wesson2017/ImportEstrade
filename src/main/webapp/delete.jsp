<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://www.frogsing.com/tags/shiro" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="consts" uri="http://www.frogsing.com/tags/consts" %>
<%@ taglib prefix="mw" uri="http://www.frogsing.com/tags/frogsing" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="g_title" value="先贸网" />
<!DOCTYPE html>
<html>
<head>
    <script src="${ctx}/jquery-1.9.0.js" type="text/javascript"></script>
    <script src="${ctx}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {

//            $("#memberbtn").click(function () {
//                if (confirm("确认要删除吗？数据删除后将不可恢复")){
//                    $('form').submit();
//                }
//            });

            $('form').submit(function () {
                if (confirm("确认要删除吗？数据删除后将不可恢复")){
                    $(this).find('input[type=submit]').attr('disabled', true);
                    //$(this).find('input[type=submit]').val('提交中...');
                    $(this).find('input[type=button]').attr('disabled', true);
                    //$(this).find('input[type=button]').val('提交中...');
                }else {
                    return false;
                }


            });
        });


    </script>
    <style type="text/css">
        body{text-align: center;}
        table td{text-align: center;height: 30px;}
        td.right{text-align: right;}
        td.left{text-align: left;}
        input{width: 200px;height: 22px;}
    </style>
</head>
<body>
<h2>==============输入合同编号进行修改===================</h2>
<form action="${ctx}/dodelete.html" method="post" enctype="multipart/form-data">
    <table cellspacing="0" border="0" width="100%" align="center">
        <tr>
            <td width="40%" class="right">请输入合同编号：</td>
            <td width="60%" class="left"><input type="text" name="scontractno"/></td>
        </tr>
        <tr>
            <td></td>
            <td class="left"><input type="submit" name="提交" id="memberbtn" style="width: 100px;height: 40px; font-size: 16px;"/></td>
        </tr>
    </table>
</form>
<br/>
<h2>===============能不能带我去导入点数据？ <a href="${ctx}/index.html" target="_blank">走你。。。。</a>=================</h2>
</body>
</html>
