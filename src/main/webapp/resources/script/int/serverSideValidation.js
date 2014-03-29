/* Server side validator.js - Loops through server side error messages obtained as JSON object and perform JQuery like Styling */

/*
 // ---------------------------
 // Following line needs to be added to the form that needs this validation
 // replace '#join_form' with appropriate form id - refer join.jsp for more information
 // on how to use it.
 //-----------------------------

 $('#join_form').submit(function() {
	 debug_message('Handler for .submit() called.');
	 removeServerErrorLabels();
	 removeHighlight();
	 return true;
	});
 */

/*
 //-----------------------------
 // JSON Message format from server - {'id':'element_id','message':'error_message'}
 //-----------------------------
 e.g.
  	<ul style="display: none;" id="serverErrorMessage">
 	 <li>{'id':'text_captcha','message':'Captcha code does not match.'}</li>
 	 <li>{'id':'text_select_username','message':'Username is not available or valid.'}</li>
    </ul>
 */

var debug = false;

$(document).ready( function() {
	processServerErrorMessage();
});

function processServerErrorMessage() {
	debug_message("getting invoked");

	try{
		isI18NAware();
	}catch(error){
		// Providing default implementation
		isI18NAware = function(){
			return false;
		};
	}
	
	for ( var i = 0; i < $('#serverErrorMessage li').length; i++) {
		getJSONServerErrorMsg($('#serverErrorMessage li')[i], i);
	}
	
	try{
		init();
	}catch(error){
		// hook method, if there is no override then no issues
	}
	
	try{
		$('.field_error:first').children().focus();
	}catch(error){
		// try_catch to handle old code implementation relying on it
	}
	
}

function getJSONServerErrorMsg(element, i) {
	debug_message($(element).html());
	var processResponse = eval('(' + $(element).html() + ')');
	var liElement = document.createElement('label');
	var idValue;
	var messageValue;
	
	if(isI18NAware()){
		//does a lookup to the resource bundle for the ID of the DOM element
		idValue = jQuery.i18n.prop(processResponse.id);
		//if we 'miss' the idValue, it gets wrapped into a 
		//open and closing brackets.  Replace with the original value
		var missedIDValueMatcher = '[' + processResponse.id +']';
		if(idValue === missedIDValueMatcher){
			idValue = processResponse.id;
		}
		
		messageValue = jQuery.i18n.prop(processResponse.message); 
		//if we 'miss' the messageValue, it gets wrapped into a 
		//open and closing brackets.  Replace with the original value
		var missedmessageValueMatcher = '[' + processResponse.message +']';
		if(messageValue === missedmessageValueMatcher){
			messageValue = processResponse.message;
		}
	}else{
		idValue = processResponse.id;
		messageValue = processResponse.message;
	}
	 
	// Special id value 'popup_alert' can be used instead of a field name.
	if (idValue === "popup_alert") {
		// Display a dialog:
		$('#dialog_serverside_popup_alert').html(messageValue).dialog('open');
	} else if (idValue === "dialog_join_request") {
		$('#dialog_organization_join_request').dialog('open');
	} else {
		liElement.appendChild(document.createTextNode(messageValue));
		liElement.className = 'field_error';
		liElement.setAttribute('id', "label" + i);
		liElement.setAttribute('name', "serverMessage");
		insertErrorMessage(liElement, $("#" + idValue));
		$("#" + idValue).change(function() {
			unHighlight("#" + idValue);
			removeLabel("#label" + i);
		});
		highlight(idValue, "");
	}
}

function insertErrorMessage(error, element) {
	$(error).insertAfter(element.parents('.formElementRow').find('label').first());
}

function highlight(element, validClass) {
	$("#" + element).parents('.formElementRow').addClass("field_error")
			.removeClass(validClass);
}

function unHighlight(element) {
	$(element).parents('.formElementRow').removeClass("field_error").addClass("");
}

function removeLabel(element) {
	$(element).remove();
}

function removeHighlight() {
	for ( var i = 0; i < $('#serverErrorMessage li').length; i++) {
		removeHighlightWithElement($('#serverErrorMessage li')[i], i);
	}
}

function removeHighlightWithElement(element, i) {
	var processResponse = eval('(' + $(element).html() + ')');
	unHighlight($("#" + processResponse.id));
}

function removeServerErrorLabels() {
	debug_message("In remove");
	$("label[name=serverMessage]").remove();
}

function debug_message(message) {
	if (debug) {
		alert(message);
	}
}

/* TODO - FIXME
Commented out till header is added

//Modal Trigger.  DIV defined in header.jsp
$(function(){
	// Dialog			
	$('#dialog_serverside_popup_alert').dialog({
		autoOpen: false,
		buttons: {
			"OK": function() { 
				$(this).dialog("close");
			} 
		},
		modal: true, 
		width: 600
	});

});*/
