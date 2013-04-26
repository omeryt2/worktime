<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	<body>
		<fmt:setBundle basename="eu.vranckaert.worktime.ui.i18n.uimessages" var="bundle"/>
		
		<form action="/user/register" method="post">
			<div class="formRow">
				<div class="formItem">
			        <label for="firstName"><fmt:message key="register.label.firstName" bundle="${bundle}"/></label>
			        <input id="firstName" type="text" name="firstName" value="${pageFlow.firstName}">
			    </div>
			    
			    <div class="formItem">
			        <label for="lastName"><fmt:message key="register.label.lastName" bundle="${bundle}"/></label>
			        <input id="lastName" type="text" name="lastName" value="${pageFlow.lastName}">
			    </div>
			</div>
			
			<div class="formRow">
			    <div class="formItem">
			        <label for="email"><fmt:message key="register.label.email" bundle="${bundle}"/></label>
			        <input id="email" type="text" name="email" value="${pageFlow.email}">
			    </div>
			</div>
			
			<div class="formRow">
				<div class="formItem">
			        <label for="password"><fmt:message key="register.label.password" bundle="${bundle}"/></label>
			        <input id="password" type="password" name="password" value="${pageFlow.password}">
			    </div>
			    
			    <div class="formItem">
			        <label for="passwordConfirmation"><fmt:message key="register.label.confirmPassword" bundle="${bundle}"/></label>
			        <input id="passwordConfirmation" type="password" name="passwordConfirmation" value="${pageFlow.passwordConfirmation}">
			    </div>
			</div>

			<div class="formRow">
				<input id="agree" type="checkbox" name="agree" value="${pageFlow.agree}"/> I confirm that I have read and agreed with the <a href="/terms-of-service" target="_blank">Terms of Service</a> and the <a href="/privacy" target="_blank">Privacy Policy</a>
			</div>

			<div class="formRow">
				<input type="hidden" value="${pageFlow.refererUrl}" name="refererUrl"/>
				<input type="submit" value="<fmt:message key='register.label.submit' bundle='${bundle}'/>"/>
			</div>
		</form>
	</body>
</html>