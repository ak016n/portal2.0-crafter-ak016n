var debug = true;

// Modal Trigger
$(document).ready(
		function() {
			$("#refresh_submit_id").click(
					function() {
						var jqxhr = $.ajax("admin/" + $("#itemKey_input_id").val() + "/" + $("#fieldKey_input_id").val(),
								function(data) {
									
								}).done(function(data) {
									$('#serverErrorMessage').append("<li>{\"id\":\"ssGeneralError\",\"message\":\"Refresh Complete\"}</li>");
						}).fail(function(data) {
							debug_message("error");
							if(data.responseText != null) {
								$('#serverErrorMessage').empty();
								var jsonErrors = $.parseJSON(data.responseText);
								$.each(jsonErrors.errors, function(idx, obj) {
									$('#serverErrorMessage').append("<li>" + JSON.stringify(obj) + "</li>");
								});
							}
						}).always(function(data) {
							processServerErrorMessage();
						});
						
					})
					
			$("#create_submit_id").click(
					function() {
						var jqxhr = $.post("admin", $( "#adminForm").serialize())
						.done(function(data) {
							debug_message("second success");})
						.fail(function(data) {
							debug_message("error");
							if(data.responseText != null) {
								$('#serverErrorMessage').empty();
								var jsonErrors = $.parseJSON(data.responseText);
								$.each(jsonErrors.errors, function(idx, obj) {
									$('#serverErrorMessage').append("<li>" + JSON.stringify(obj) + "</li>");
								});
							}
							processServerErrorMessage();})
						.always(function(data) {
							debug_message("complete");
						});
					})					

			$("#adminForm").submit(function(event) {
				removeServerErrorLabels();
				removeHighlight();
				debug_message("Handler for .submit() called.");
				event.preventDefault();
			})
		});

isI18NAware = function() {
	return true;
};

function debug_message(message) {
	if (debug) {
		alert(message);
	}
}