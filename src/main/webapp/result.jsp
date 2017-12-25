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
<html>
<body>
<h2><mw:msg/></h2>
</body>
</html>
