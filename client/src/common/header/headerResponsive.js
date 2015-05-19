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
			var animationTime = 0;

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
		// checks if resolution is for desktop and the mobile menu was left visible at some point.
		if($(window).width() > 480 && typeof $('#section_content').prop("style") !== "undefined") {
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