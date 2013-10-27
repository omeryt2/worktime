<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	<head>
		<style type="text/css">
			.form {
				width: 100%; 
				border: 0px solid;
				font-size: 14px;
			}
			
			.form input[type=text] {
				width: 100%;
			}
			
			.form .label {
				width: 200px;
				padding-bottom: 5px;
			}
		</style>
	</head>
	<body>
		<fmt:setBundle basename="eu.vranckaert.worktime.ui.i18n.uimessages" var="bundle"/>
		<form action="/user/profile" method="post">
			<table class="form">
				<tr>
					<td class="label"><fmt:message key="profile.label.firstName" bundle="${bundle}"/> *:</td>
					<td><input type="text" name="firstName" id="firstName" value="${pageFlow.firstName}"/></td>
				</tr>
				<tr>
					<td class="label"><fmt:message key="profile.label.lastName" bundle="${bundle}"/> *:</td>
					<td><input type="text" name="lastName" id="lastName" value="${pageFlow.lastName}"/></td>
				</tr>
				<tr>
					<td class="label"><fmt:message key="profile.label.email" bundle="${bundle}"/> *:</td>
					<td>${pageFlow.user.email}</td>
				</tr>
				<tr>
					<td class="label"><fmt:message key="profile.label.avatarUrl" bundle="${bundle}"/>:</td>
					<td><input type="text" name="profileImageUrl" id="profileImageUrl" value="${pageFlow.profileImageUrl}"/></td>
				</tr>
				<tr>
					<td class="label"></td>
					<td><fmt:message key="profile.msg.avatarUrl" bundle="${bundle}"/></td>
				</tr>
				<tr>
					<td class="label"><fmt:message key="profile.label.registeredSince" bundle="${bundle}"/>:</td>
					<td><fmt:formatDate type="DATE" dateStyle="SHORT" value="${pageFlow.user.registrationDate}"/> <fmt:formatDate type="TIME" dateStyle="SHORT" value="${pageFlow.user.registrationDate}"/></td>
				</tr>
				<tr>
					<td colspan="2">
						<fmt:message key="profile.msg.forgotPassword" bundle="${bundle}">
							<fmt:param value="/resetPasswordRequest"/>
						</fmt:message>
					</td>
				</tr>
				<tr>
					<td>
						<input type="submit" value="<fmt:message key="profile.label.submit" bundle="${bundle}"/>"/>
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>