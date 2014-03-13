<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	<body>		
		<fmt:setBundle basename="eu.vranckaert.worktime.ui.i18n.uimessages" var="bundle"/>
		<c:if test="${pageFlow.allowPasswordReset}">
			<form action="/resetPassword/${pageFlow.resetKey}" method="post">
				<div class="formRow">
					<div class="formItem">
				        <label for="name"><fmt:message key="resetPassword.label.newPassword" bundle="${bundle}"/></label>
				        <input id="name" type="password" name="newPassword">
				    </div>
				    
				    <div class="formItem">
				        <label for="name"><fmt:message key="resetPassword.label.confirmPassword" bundle="${bundle}"/></label>
				        <input id="name" type="password" name="repeatPassword">
				    </div>
				</div>
				
				<div class="formRow">
					<input type="submit" value="<fmt:message key="resetPassword.label.submit" bundle="${bundle}"/>"/>
				</div>
			</form>
		</c:if>
	</body>
</html>
