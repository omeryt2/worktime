<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/jsp;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<fmt:setBundle basename="eu.vranckaert.worktime.ui.i18n.uimessages" var="bundle"/>
	<head>
		<title>${pageFlow.title}</title>
		
		<!-- CSS -->
		<link rel="stylesheet" type="text/css" href="/webapp/stylesheet/jMenu.jquery.css">
		<link rel="stylesheet" type="text/css" href="http://code.jquery.com/ui/1.10.2/themes/smoothness/jquery-ui.css">
		<link rel="stylesheet" type="text/css" href="/webapp/stylesheet/base.css">
		
		<!-- JS Imports -->
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.2/jquery-ui.min.js"></script>
		<script src="/webapp/js/jMenu.jquery.js"></script>
		
		<!-- JS -->
		<script type="text/javascript" src="/webapp/js/app.js"></script>
		<script type="text/javascript">
			APP.constants.sessionKey='${loggedInSessionKey}';
			APP.constants.user='${loggedInUser}';
			
			$(document).ready(function() {
				APP.renderMenu();
			});

			APP.constants.yes='<fmt:message key="yes" bundle="${bundle}"/>';
			APP.constants.no='<fmt:message key="no" bundle="${bundle}"/>';
		</script>
		
		<decorator:head />
	</head>
	<body>
		<div id="dialog-logout-confirmation" title='<fmt:message key="logout.dialog.sure.title" bundle="${bundle}"/>'>
		  <p>
		  	<fmt:message key="logout.dialog.sure.msg" bundle="${bundle}"/>
		  </p>
		</div>
	
		<div class="bodyFooter">
			<div class="headerFullScreenContainer">
				<div class="content">
					<c:if test="${not pageFlow.isHomePage}">
						<div class="clickableLogo">
							<a href="/">
								<img src="/webapp/img/logo.png" class="logo"/>
							</a>
						</div>
					</c:if>
					<c:if test="${pageFlow.isHomePage}">
						<div class="nonClickableLogo">
							<img src="/webapp/img/logo.png" class="logo"/>
						</div>
					</c:if>
					
					<div class="title"><fmt:message key="title" bundle="${bundle}"/></div>
					
					<c:if test="${pageFlow.showUserHeader}">
						<div class="userArea hide">
							<c:if test="${not pageFlow.loggedIn}">
								<div style="text-align:right;">
									<div><fmt:message key="template.not.logged.in" bundle="${bundle}"/></div>
									<div><a href="/user/login"><fmt:message key="template.user.login" bundle="${bundle}"/></a> <fmt:message key="template.user.or" bundle="${bundle}"/> <a href="/user/register"><fmt:message key="template.user.register" bundle="${bundle}"/></a></div>
								</div>
							</c:if>
							
							<c:if test="${pageFlow.loggedIn}">
								<div style="text-align:right;">
									<a href="/user/profile">
										<c:if test="${not empty pageFlow.user.profileImageUrl}">
											<img src="${pageFlow.user.profileImageUrl}" style="width: 40px; height: 40px; float:left;"/>
										</c:if>
										<c:if test="${empty pageFlow.user.profileImageUrl}">
											<img src="/webapp/img/no-avatar.png" style="width: 40px; height: 40px; float:left;"/>
										</c:if>
									</a>
									<div style="text-align: left; padding-left: 50px;">
										<fmt:message key="template.user.welcome" bundle="${bundle}">
											<fmt:param value="${pageFlow.user.fullName}"/>
										</fmt:message>
									</div>
									<div style="text-align: left; padding-left: 50px;">No ongoing time registrations</div>
								</div>
							</c:if>
						</div>	
					</c:if>				
				</div>
			</div>
			
			<c:if test="${pageFlow.loggedIn}">
				<div class="headerFullScreenContainer menu hide">
					<div class="content">
						<ul id="jMenu">
							<li style="border-right: 1px solid rgb(182, 182, 182);">
								<a class="fNiv"><fmt:message key="template.menu.projectsAndTasks" bundle="${bundle}"/></a>
								<ul>
									<li class="arrow"></li>
									<li><a><fmt:message key="template.menu.projectsAndTasks.projects" bundle="${bundle}"/></a></li>
									<li><a><fmt:message key="template.menu.projectsAndTasks.tasks" bundle="${bundle}"/></a></li>
								</ul>
							</li>
							<li style="border-right: 1px solid rgb(182, 182, 182);">
								<a class="fNiv"><fmt:message key="template.menu.timeRegistrations" bundle="${bundle}"/></a>
							</li>
							<li>
								<a class="fNiv"><fmt:message key="template.menu.account" bundle="${bundle}"/></a>
								<ul>
									<li class="arrow"></li>
									<li><a href="/user/profile"><fmt:message key="template.menu.account.profile" bundle="${bundle}"/></a></li>
									<li><a href="javascript:APP.logoutConfirmation()"><fmt:message key="template.menu.account.logout" bundle="${bundle}"/></a></li>
								</ul>
							</li>
						</ul>
					</div>
				</div>
			</c:if>
			
			<div class="body">
				<div class="content">
					<c:if test="${not empty pageFlow.pageTitle}">
						<div class="pageTitle">
							${pageFlow.pageTitle}
						</div>
					</c:if>
				
						<c:if test="${not empty pageFlow.infoMessages}">
							<div>
								<div class="messages">
									<c:forEach var="message" items="${pageFlow.infoMessages}">
										<div class="info">
											<div class="messageIcon"><img src="/webapp/img/info.png"/></div>
											<div class="messageText">${message}</div>
										</div>
									</c:forEach>
								</div>
							</div>
						</c:if>
						
						<c:if test="${not empty pageFlow.validationMessages}">
							<div>
								<div class="messages">
									<c:forEach var="message" items="${pageFlow.validationMessages}">
										<div class="validation">
											<div class="messageIcon"><img src="/webapp/img/warning.png"/></div>
											<div class="messageText">${message}</div>
										</div>
									</c:forEach>
								</div>
							</div>
						</c:if>
						
						<c:if test="${not empty pageFlow.warningMessages}">
							<div>
								<div class="messages">
									<c:forEach var="message" items="${pageFlow.warningMessages}">
										<div class="warning">
											<div class="messageIcon"><img src="/webapp/img/warning.png"/></div>
											<div class="messageText">${message}</div>
										</div>
									</c:forEach>
								</div>
							</div>
						</c:if>
						
						<c:if test="${not empty pageFlow.errorMessages}">
							<div>
								<div class="messages">
									<c:forEach var="message" items="${pageFlow.errorMessages}">
										<div class="error">
											<div class="messageIcon"><img src="/webapp/img/error.png"/></div>
											<div class="messageText">${message}</div>
										</div>
									</c:forEach>
								</div>
							</div>
						</c:if>
						
					
					<div class="page">
						<decorator:body />
					</div>
				</div>
			</div>
			<footer>
				<div class="content">
					<div class="menu">
						<a href="/terms-of-service"><fmt:message key="template.footer.termsOfService" bundle="${bundle}"/></a> | <a href="/privacy"><fmt:message key="template.footer.privacyPolicy" bundle="${bundle}"/></a> | <a href="/contact"><fmt:message key="template.footer.contact" bundle="${bundle}"/></a> | <a href="/contact?reason=BUG_REPORT"><fmt:message key="template.footer.reportBug" bundle="${bundle}"/></a>
					</div>
					<div class="copyright">
						&copy; Dirk Vranckaert - ${pageFlow.copyrightYear}
					</div>
				</div>
			</footer>
		</div>
	</body>
</html>
