var APP = APP || {};
APP.constants = [];
APP.globalvar = {};

$(function() {
	APP.initDialogs();
	APP.init();
});

APP.init = function() {
}

APP.initDialogs = function() {
	$("#dialog-logout-confirmation").dialog({
		resizable: false,
		modal: true,
		buttons: [
		  {
			text : APP.constants.yes,
			click: function() {
	          $( this ).dialog( "close" );
	          APP.logout();
	        }
		  },
		  {
			text: APP.constants.no,
	        click: function() {
	          $( this ).dialog( "close" );
	        }
	      }
		]
	});
	$("#dialog-logout-confirmation").dialog("close");
}

APP.redirectNoWarning = function redirectNoWarning(url) {
	 // temporary clear the onbeforeunload event
	var onbeforeunloadCallback = window.onbeforeunload;
	window.onbeforeunload = null;
	// do the redirect
	window.location.replace(url);
	// restore the onbeforeunload event
	window.onbeforeunload = onbeforeunloadCallback;
}

APP.logoutConfirmation = function() {
	$("#dialog-logout-confirmation").dialog("open");
}

APP.logout = function() {
	$.post('/user/logout', function(data) {
		window.setTimeout(function(){APP.redirectNoWarning(data)}, 100);
	});
}

APP.renderMenu = function() {
	$("#jMenu").jMenu({
		openClick : false,
		ulWidth : 'auto',
		effects : {
			effectSpeedOpen : 200,
			effectSpeedClose : 200,
			effectTypeOpen : 'slide',
			effectTypeClose : 'hide',
			effectOpen : 'linear',
			effectClose : 'linear'
		},
		TimeBeforeOpening : 100,
		TimeBeforeClosing : 100,
		animatedText : false,
		paddingLeft: 1
	});
}