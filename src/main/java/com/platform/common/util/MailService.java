package com.platform.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {

	@Value("#{environment['mail.host.name']}")
	private String hostName;
	@Value("#{environment['mail.smtp.port']}")
	private String port;
	@Value("#{environment['mail.username']}")
	private String userName;
	@Value("#{environment['mail.password']}")
	private String password;

//	@Async
	public void sendMail(String sendTo, String subject, String text, SendMailCallback sendMailCallback) throws Exception{
		try {
			sendMailCallback.execute();
			Email email = getEmail();
			email.setSubject(subject);
			email.setMsg(text);
			email.addTo(sendTo);
			String messageId = email.send();
			log.info("send success. message id:{}, target email:{}, subject:{}, content:{}", messageId, sendTo, subject, text);
		}catch (EmailException e){
			log.error("send email failed. {}", e);
			throw e;
		}catch (Exception e){
			log.error("save failed. {}", e.getMessage());
			throw e;
		}
	}

	private Email getEmail() throws EmailException{
		Email email = new SimpleEmail();
		email.setHostName(hostName);
		email.setSmtpPort(Integer.valueOf(port));
		email.setAuthentication(userName, password);
		email.setSSL(true);
		email.setFrom(userName);
		email.setCharset("UTF-8");
		return email;
	}
}
