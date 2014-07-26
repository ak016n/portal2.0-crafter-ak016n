package com.att.developer.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FaultUtils {
    
    private static final String REGEX_REMOV_SQR_BRACKET = "\\[\\d*\\]";
	private static final String PERIOD = ".";
	private static final String MESSAGE_SUFFIX = ".message";
	private static final String ID_SUFFIX = ".id";

	public static boolean checkForViolations(boolean isViolationPresent, BindingResult bindingResult, ServerSideErrors errorColl) {

        if (bindingResult.hasErrors()) {
            processBindingError(bindingResult, errorColl);
            isViolationPresent = true;
        }

        return isViolationPresent;
    }

    public static void processBindingError(BindingResult bindingResult, ServerSideErrors errorColl) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
        	String fullyQualifiedFieldName = error.getObjectName() + PERIOD + error.getField();
        	String message = getFilteredText(fullyQualifiedFieldName) + PERIOD + convertFirstLetterToCaps(error.getCode()) + MESSAGE_SUFFIX;
        	ServerSideError serverSideError = new ServerSideError.Builder().id(getFilteredText(fullyQualifiedFieldName) + ID_SUFFIX).message(message).build();
            
            errorColl.getErrorColl().add(serverSideError);
        }
    }
    
    public static String convertToJson(ServerSideErrors errorColl) throws JsonProcessingException {
    	ObjectMapper jsonMapper = new ObjectMapper();
    	return jsonMapper.writeValueAsString(errorColl);
    }
    
    /** 
     * To keep the generated full path Binding ID consistent with other JSR 303 IDs
     */
    public static String convertFirstLetterToCaps(String str) {
        return StringUtils.isNotBlank(str) ? StringUtils.overlay(str, StringUtils.upperCase(str.charAt(0) + StringUtils.EMPTY), 0, 1) : null;
    }

    /**
     * Stripping out [0] from Contact[0] information
     */
    public static String getFilteredText(String text) {
        String filteredText = null;

        if (text != null) {
            filteredText = text.replaceAll(REGEX_REMOV_SQR_BRACKET, StringUtils.EMPTY);
        }

        return filteredText;
    }

}
