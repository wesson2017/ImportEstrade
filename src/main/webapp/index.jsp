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
<c:set var="sitedir" value="${sessionScope.GLOBAL_SITE_TYPE_DIR}" />
<c:set var="sitetype" value="${sessionScope.GLOBAL_SITE_TYPE}" />
<!DOCTYPE html>
<html>
<head>
    <script src="${ctx}/jquery-1.9.0.js" type="text/javascript"/>
    <script type="text/javascript">
        $(document).ready(function () {
           $("#memberbtn,#contractbtn").click(function(){
               $(this).disabled();
           });
            $('form').submit(function () {
                if ($(this) && $(this).valid) {
                    var r = $(this).valid();
                    if (r) {
                        $(this).find('input[type=submit]').attr('disabled', true);
                        //$(this).find('input[type=submit]').val('提交中...');
                        $(this).find('input[type=button]').attr('disabled', true);
                        //$(this).find('input[type=button]').val('提交中...');
                    }
                }

            });
        });
    </script>
</head>
<body>
<h2>==============导入会员信息===================</h2>
<h4><a href="${ctx}/MemberImportTemplate.xlsx" target="_blank">点击下载会员导入模块</a></h4>
<form action="${ctx}/upload.html" method="post" enctype="multipart/form-data">
    请选择会员信息文件：<input type="file" name="file"/>
<input type="submit" name="提交" id="memberbtn"/>
</form>
<br/><br/><br/>
<h2>===============导入交易信息=================</h2>
<h4><a href="${ctx}/ContractImportTemplate.xlsx" target="_blank">点击下载会员导入模块</a></h4>
<form action="${ctx}/contract.html" method="post" enctype="multipart/form-data">
    请选择会员信息文件：<input type="file" name="file"/>
    <input type="submit" name="提交" id="contractbtn"/>
</form>
<br/><br/><br/>
<h2>===============能不能带我去修改点数据？ <a href="${ctx}/update.html" target="_blank">走你。。。。</a>=================</h2>
<br/><br/><br/>
<h2>===============能不能带我去<font color="red">删除</font>点数据？ <a href="${ctx}/delete.html" target="_blank">走你。。。。</a>=================</h2>
</body>
</html>
