<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	<body>
		<fmt:setBundle basename="eu.vranckaert.worktime.ui.i18n.uimessages" var="bundle"/>
		
		<div style=" width: 500px; margin: 0 auto; text-align:center;">
			<img src="/webapp/img/promo/android/framed_screenshot.png" style="height: 498px; widht: 287px;"/><br/>
 			<fmt:message key="home.download" bundle="${bundle}"/><br/>
			<a href="https://play.google.com/store/apps/details?id=eu.vranckaert.worktime" target="_blank">
			  <img alt="Get it on Google Play" src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
			</a>
		</div>
	</body>
</html>