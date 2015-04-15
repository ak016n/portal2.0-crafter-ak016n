package com.att.developer.service.impl;

import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import com.att.developer.exception.MessageProcessingException;
import com.att.developer.util.MessageUtils;

public class TemplateBasedEmailServiceImplTest {

	@Mock
    private JavaMailSender mockMailSender;
	
	@Mock
	private MessageUtils mockMessageUtils;
	
	private  TemplateBasedEmailServiceImpl templateBasedEmailService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		templateBasedEmailService = new TemplateBasedEmailServiceImpl();
		templateBasedEmailService.setMailSender(mockMailSender);
		templateBasedEmailService.setMessageUtils(mockMessageUtils);
	}
	
	
	@Test
	@SuppressWarnings("unchecked")
	public void testSendMail_happyPath() throws MessageProcessingException {
		Mockito.when(mockMailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->to", null)).thenReturn("leonard@tbs.com");
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->subject", null)).thenReturn("comic book store");
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->body", null)).thenReturn("need copies of hulk");
		
		templateBasedEmailService.sendMail("hello_world", null);
		
		Mockito.verify(mockMailSender, Mockito.times(1)).send(Mockito.any(MimeMessage.class));
		Mockito.verify(mockMessageUtils, Mockito.times(3)).getMessage(Mockito.anyString(), Mockito.anyMap());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSendMail_emptyTo() throws MessageProcessingException {
		Mockito.when(mockMailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->subject", null)).thenReturn("comic book store");
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->body", null)).thenReturn("need copies of hulk");
		
		templateBasedEmailService.sendMail("hello_world", null);
		
		Mockito.verify(mockMailSender, Mockito.never()).send(Mockito.any(MimeMessage.class));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSendMail_emptySubject() throws MessageProcessingException {
		Mockito.when(mockMailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->to", null)).thenReturn("leonard@tbs.com");
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->body", null)).thenReturn("need copies of hulk");
		
		templateBasedEmailService.sendMail("hello_world", null);
		
		Mockito.verify(mockMailSender, Mockito.never()).send(Mockito.any(MimeMessage.class));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSendMail_emptyBody() throws MessageProcessingException {
		Mockito.when(mockMailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->to", null)).thenReturn("leonard@tbs.com");
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->subject", null)).thenReturn("comic book store");
		
		templateBasedEmailService.sendMail("hello_world", null);
		
		Mockito.verify(mockMailSender, Mockito.never()).send(Mockito.any(MimeMessage.class));
	}
	
	@Test(expected=MessageProcessingException.class)
	public void testSendMail_exception() throws MessageProcessingException {
		Mockito.when(mockMailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));
		Mockito.when(mockMessageUtils.getMessage("hello_world->email->to", null)).thenThrow(new MessageProcessingException("no email"));
		
		templateBasedEmailService.sendMail("hello_world", null);
		Mockito.verify(mockMailSender, Mockito.never()).send(Mockito.any(MimeMessage.class));
	}

}
