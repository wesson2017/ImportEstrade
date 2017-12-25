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

            $('form').submit(function () {
                        $(this).find('input[type=submit]').attr('disabled', true);
                        //$(this).find('input[type=submit]').val('提交中...');
                        $(this).find('input[type=button]').attr('disabled', true);
                        //$(this).find('input[type=button]').val('提交中...');

            });
        });


        function addMore(type) {
            var tr = $("tbody." + type).find("tr:first");
            var objFile = tr.clone();
            objFile.find("input").val("");
            $("tbody." + type).append(objFile);
        }
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
<form action="${ctx}/doupdate.html" method="post" enctype="multipart/form-data">
    <table cellspacing="0" border="0" width="100%" align="center">
        <tr>
            <td width="40%" class="right">请输入合同编号：</td>
            <td width="60%" class="left"><input type="text" name="scontractno"/></td>
        </tr>
            <tr><td colspan="2">========修改信息开始=======</td></tr>
        <tr>
            <td  class="right">挂牌日期：</td>
            <td class="left"><input type="text" name="dgpdate" readonly="readonly" onclick="WdatePicker({maxDate:'2050-10-01'})"/></td>
        </tr>
        <tr>
            <td class="right">合同日期：</td>
            <td class="left"><input type="text" name="dcontractdate" readonly="readonly" onclick="WdatePicker({maxDate:'2050-10-01'})"/></td>
        </tr>
        <tr>
            <td class="right">支付日期：</td>
            <td class="left"><input type="text" name="dpaydate" readonly="readonly" onclick="WdatePicker({maxDate:'2050-10-01'})"/></td>
        </tr>
        <tr>
            <td class="right">发货日期：</td>
            <td class="left"><input type="text" name="dsenddate" readonly="readonly" onclick="WdatePicker({maxDate:'2050-10-01'})"/></td>
        </tr>
        <tr>
            <td class="right">收货日期：</td>
            <td class="left"><input type="text" name="dacceptdate" readonly="readonly" onclick="WdatePicker({maxDate:'2050-10-01'})"/></td>
        </tr>
        <tr>
            <td class="right">开票日期：</td>
            <td class="left"><input type="text" name="dinvoicedate" readonly="readonly" onclick="WdatePicker({maxDate:'2050-10-01'})"/></td>
        </tr>
        <tr>
            <td class="right">收票日期：</td>
            <td class="left"><input type="text" name="dacceptinvoicedate" readonly="readonly" onclick="WdatePicker({maxDate:'2050-10-01'})"/></td>
        </tr>

        <tr><td colspan="2">========合同附件开始=======【<button type="button" class="addfile" onclick="addMore('scontractfile');" >点这里添加多个合同附件</button>】</td></tr>
        <tbody class="scontractfile">
        <tr>
            <td class="right">合同附件：</td>
            <td class="left"><input type="file" name="scontractfile"/></td>
        </tr>
        </tbody>
        <tr><td colspan="2">========支付附件开始=======【<button type="button" class="addfile" onclick="addMore('payfile');" >点这里添加多个支付附件</button>】</td></tr>
        <tbody class="payfile">
        <tr>
            <td class="right">支付附件：</td>
            <td class="left"><input type="file" name="payfile"/></td>
        </tr>
        </tbody>
        <tr><td colspan="2">========发货附件开始=======【<button type="button" class="addfile" onclick="addMore('sendgoodfile');" >点这里添加多个发货附件</button>】</td></tr>
        <tbody class="sendgoodfile">
        <tr>
            <td class="right">发货附件：</td>
            <td class="left"><input type="file" name="sendgoodfile"/></td>
        </tr>
        </tbody>
        <tr><td colspan="2">========收货附件开始=======【<button type="button" class="addfile" onclick="addMore('acceptgoodfile');" >点这里添加多个收货附件</button>】</td></tr>
        <tbody class="acceptgoodfile">
        <tr>
            <td class="right">收货附件：</td>
            <td class="left"><input type="file" name="acceptgoodfile"/></td>
        </tr>
        </tbody>
        <tr><td colspan="2">========发票附件开始=======【<button type="button" class="addfile" onclick="addMore('invoicefile');" >点这里添加多个发票附件</button>】</td></tr>
        <tbody class="invoicefile">
        <tr>
            <td class="right">发票附件：</td>
            <td class="left"><input type="file" name="invoicefile"/></td>
        </tr>
        </tbody>
        <tr><td colspan="2">&nbsp;</td></tr>
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
