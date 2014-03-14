<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	<body>
		<fmt:setBundle basename="eu.vranckaert.worktime.ui.i18n.uimessages" var="bundle"/>
		
		<form action="/user/login" method="post">
			<div class="formRow">
				<div class="formItem">
			        <label for="name"><fmt:message key="login.label.email" bundle="${bundle}"/></label>
			        <input id="name" type="text" name="email" value="${pageFlow.email}">
			    </div>
			</div>
			
			<div class="formRow">
			    <div class="formItem">
			        <label for="name"><fmt:message key="login.label.password" bundle="${bundle}"/></label>
			        <input id="name" type="password" name="password">
			    </div>
			</div>
			
			<div class="formRow loginForgotPassword">
			    <a href="/resetPasswordRequest"><fmt:message key="login.label.forgot.password" bundle="${bundle}"/></a>
			</div>
			
			<div class="formRow">
				<input type="hidden" value="${pageFlow.refererUrl}" name="refererUrl"/>
				<input type="submit" value="<fmt:message key="login.label.submit" bundle="${bundle}"/>"/>
			</div>
		</form>
	</body>
</html>