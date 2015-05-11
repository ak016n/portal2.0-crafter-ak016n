$(document).ready(function() {

	// mobile menu toggle button
	$('#mobile-menu').click(function() {
		if($('#section_nav').hasClass('hidden')){
			$("#section_content").css("position","fixed");
			$("#content-area").css("height","100%");
			$('#section_nav').removeClass('hidden');
		}
		else {
			$("#section_content").css("position","static");
			$("#content-area").css("height","auto");
			$('#section_nav').addClass('hidden');
		}
		
		// function to bind plus/minus events
		$('#section_nav').find('a.expands').click(function() {
			var	animationTime = 0;

			if($(this).parents('li').find('.plus_minus').text() === '-') {
				$(this).parents('li').find('.menuDepth_2').slideUp(animationTime, function() {
					$(this).parents('li').find('.plus_minus').text('+');
				});
			}
			else {
				$(this).parents('li').find('.menuDepth_2').slideDown(animationTime, function() {
					$(this).parents('li').find('.plus_minus').text('-');
				});
			}

		});
	});

	// updateDisplay: location for event handling when screen is updated to a new type of display
	function updateDisplay() {
		// if display is for a mobile device and the mobile menu is open set main content to be fixed, else let it be free.
		if($(window).width() > 480 && $('#section_content').prop("style").length > 0) {
			$("#section_content").css("position","static");
			$("#content-area").css("height","auto");
			$('#section_nav').addClass('hidden');
		}
	}

	// resizing events
	$(window).resize(function(){
		updateDisplay();
	});

	// this is when there is no resizing - such as opening in phone or tablet
	updateDisplay();

});