// Modal Trigger
$(document).ready(
		function() {
			$("#refresh_submit_id").click(
					function() {
						var jqxhr = $.ajax("admin/" + $("#itemKey_input_id").val() + "/" + $("#fieldKey_input_id").val(),
								function(data) {
									alert("success");
								}).done(function(data) {
							alert("second success");
						}).fail(function(data) {
							alert("error");
							if(data.responseText != null) {
								var jsonErrors = $.parseJSON(data.responseText);
								$.each(jsonErrors.errors, function(idx, obj) {
									$('#serverErrorMessage').append("<li>" + JSON.stringify(obj) + "</li>");
								});
							}
						}).always(function(data) {
							alert("complete");
						});
					})

			$("#adminForm").submit(function(event) {
				alert("Handler for .submit() called.");
				event.preventDefault();
			})
		});

isI18NAware = function() {
	return true;
};

//				$.getJSON("/" + $("#itemKey_input_id").val() + "/" + $("#fieldKey_input_id").val(), function(data) {
//});