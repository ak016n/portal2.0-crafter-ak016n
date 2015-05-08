$(document).ready(function() {
	// Function-level strict mode syntax
	"use strict";
	/////////////////////////////////////////////////////////////////////////
	// mobile menu toggle button
	$mobileMenu = $('#section_nav');
	$mobileMenuTrigger = $('#mobile-menu');
	$mobileMenuTrigger.click(function(){
		if($mobileMenu.hasClass('hidden')){
			$("#section_content").css("position","fixed");
			$("#content-area").css("height","100%");
			$mobileMenu.removeClass('hidden');
		}
		else {
			$("#section_content").css("position","static");
			$("#content-area").css("height","auto");
			$mobileMenu.addClass('hidden');
		}
	});
	
	/////////////////////////////////////////////////////////////////////////
	// updateDisplay: location for event handling when screen is updated to a new type of display
	var updateDisplay = (function() {
		// if display is for a mobile device and the mobile menu is open set main content to be fixed, else let it be free.
		if($(window).width() > 480 && $('#section_content').prop("style").length > 0) {
			$("#section_content").css("position","static");
			$("#content-area").css("height","auto");
			$('#section_nav').addClass('hidden');
		}
	})();

	/////////////////////////////////////////////////////////////////////////
	// resizing events
	$(window).resize(function(){
		updateDisplay();
	});


	/////////////////////////////////////////////////////////////////////////
	// mobile menu events
	function getCookie(cname)
	{
		var name = cname + "=";
		var ca = document.cookie.split(';');
		for(var i=0; i<ca.length; i++) 
		{
		  var c = ca[i].trim();
		  if (c.indexOf(name) ===0) return c.substring(name.length,c.length);
		}
		return "";
	}

	var resCookie = 'isResponsive';
	try {
		if(getCookie(resCookie) !== 'no' && $('.non_responsive_page_js').length === 0) {
			generateMenu();
		}
	}
	catch(ex){
		generateMenu();
	}
	// function to bind plus/minus events
	function generateMenu() {
	var $responsiveMenu = $('#section_nav'),
	$expandLinks = $responsiveMenu.find('a.expands');

		(function() {
			$expandLinks.attr('href', null).unbind('click').bind('click', function() {
				
				var $current = $(this),
					$parent = $current.parents('li'),
					$currentMenu = $parent.find('.menuDepth_2'),
					$currentPlus_minus = $parent.find('.plus_minus'),
					cssName = 'exxxxx',
					animationTime = 0;

				if($currentPlus_minus.text() === '-') {
					$currentMenu.slideUp(animationTime, function() {
						$currentPlus_minus.text('+');
						$currentMenu.removeClass(cssName);	
					});
				}
				else {
					$currentMenu.slideDown(animationTime, function() {
						$currentPlus_minus.text('-');
					});
				}

			});

		})();
	
	}
	
	/////////////////////////////////////////////////////////////////////////
	// this is when there is no resizing - such as opening in phone or tablet
	updateDisplay();
	
	/* to be removed later if not needed 
	$('.js-api-header').click(function(){
		var $collapse = $(this).find('.collapse'),
			openCss = 'open',
			$subMenu = $('.sidr ul ul.sub-menu');

		if($collapse.hasClass(openCss)){
			$collapse.removeClass(openCss);
			$subMenu.hide();
		}
		else {
			$collapse.addClass(openCss);
			$subMenu.show();
		}
	});
	
	$('.remove-responsive').click(function(){
		try{
			document.cookie = resCookie + '=no';
		}
		catch(ex){
		}
		window.location.href = window.location.href;
	});*/

});