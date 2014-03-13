<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	<body>
		<fmt:setBundle basename="eu.vranckaert.worktime.ui.i18n.uimessages" var="bundle"/>
		
		<fmt:message key="contact.label.contactDirect" bundle="${bundle}">
			<fmt:param value="worktime@vranckaert.eu"/>
		</fmt:message>
		<br/>
	
		<form action="/contact" method="post">
			<div class="formRow">
				<div class="formItem">
			        <label for="firstName"><fmt:message key="contact.label.firstName" bundle="${bundle}"/></label>
			        <input id="firstName" type="text" name="firstName" value="${pageFlow.firstName}">
			    </div>
			    
			    <div class="formItem">
			        <label for="lastName"><fmt:message key="contact.label.lastName" bundle="${bundle}"/></label>
			        <input id="lastName" type="text" name="lastName" value="${pageFlow.lastName}">
			    </div>
			</div>
			
			<div class="formRow">
				<div class="formItem">
			        <label for="email"><fmt:message key="contact.label.email" bundle="${bundle}"/></label>
			        <input id="email" type="text" name="email" value="${pageFlow.email}" size="50">
			    </div>
			</div>
			
			<div class="formRow">
				<div class="formItem">
			        <label for="reason"><fmt:message key="contact.label.reason" bundle="${bundle}"/></label>
			        <select name="reason" id="reason">
					    <c:forEach items="${pageFlow.contactReasons}" var="option">
					    	<option value="${option.value}" <c:if test="${option.selected}">selected="selected"</c:if> >${option.text}</option>
					    </c:forEach>
					</select>
			    </div>
			</div>
			
			<div class="formRow">
				<div class="formItem">
			        <label for="message"><fmt:message key="contact.label.message" bundle="${bundle}"/></label>
			        <textarea rows="10" cols="120" name="message" id="message">${pageFlow.message}</textarea>
			    </div>
			</div>
			
			<div class="formRow">
				<input type="submit" value="<fmt:message key='contact.label.submit' bundle='${bundle}'/>"/>
				<a href="${pageUrl}"><input type="button" value="<fmt:message key='contact.label.clear' bundle='${bundle}'/>"/></a>
			</div>
		</form>
	</body>
</html>
