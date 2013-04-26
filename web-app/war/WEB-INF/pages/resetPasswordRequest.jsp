<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	<body>		
		<fmt:setBundle basename="eu.vranckaert.worktime.ui.i18n.uimessages" var="bundle"/>
		
		<form action="/resetPasswordRequest" method="post">
			<fmt:message key="resetPasswordRequet.label.email" bundle="${bundle}"/>: <input type="text" name="email"/>
			<br/><input type="submit" value="<fmt:message key='resetPasswordRequet.label.submit' bundle='${bundle}'/>"/>
		</form>
	</body>
</html>
