package com.att.developer.service.impl;

import java.util.Map;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.att.developer.service.TemplateBasedEmailService;
import com.att.developer.util.Constants;
import com.att.developer.util.MessageUtils;

@Component
public class TemplateBasedEmailServiceImpl implements TemplateBasedEmailService {
	
	private static final String BODY = "body";
	private static final String SUBJECT = "subject";
	private static final String TO = "to";

	@Inject
    private JavaMailSender mailSender;
	
	@Inject
	private MessageUtils messageUtils;

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void setMessageUtils(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }
    
    @Override
	public void sendMail(String templateName, Map<String, Object> messageContentMap) {
    	MimeMessage message = mailSender.createMimeMessage();

    	// use the true flag to indicate you need a multipart message
    	try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(messageUtils.getMessage(buildKey(templateName, Constants.EMAIL_MESSAGE_TYPE, TO), messageContentMap));
			helper.setSubject(messageUtils.getMessage(buildKey(templateName, Constants.EMAIL_MESSAGE_TYPE, SUBJECT), messageContentMap));
			helper.setText(messageUtils.getMessage(buildKey(templateName, Constants.EMAIL_MESSAGE_TYPE, BODY), messageContentMap));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	mailSender.send(message);
    }
    
    private String buildKey(String... keys) {
    	StringBuilder messageKey = new StringBuilder();
    	boolean isFirst = true;
    	for(String each : keys) {
    		if(!isFirst) {
    			messageKey.append(Constants.MESSAGE_SEPARATOR);
    		}
    		messageKey.append(each);
    		isFirst = false;
    	}
    	return messageKey.toString();
    }

}
