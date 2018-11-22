package com.platform.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {

	@Autowired
	private Environment environment;

	@Value("#{environment['mail.host.name']}")
	private String hostName;
	@Value("#{environment['mail.smtp.port']}")
	private String port;
	@Value("#{environment['mail.username']}")
	private String userName;
	@Value("#{environment['mail.password']}")
	private String password;

	@Async
	public void sendMail(String sendTo, String subject, String text){
		try {
			Email email = getEmail();
			email.setSubject(subject);
			email.setMsg(text);
			email.addTo(sendTo);
			email.send();
		}catch (EmailException e){
			log.error("send email failed. {}", e);
		}
	}

	private Email getEmail() throws EmailException{
		Email email = new SimpleEmail();
		email.setHostName(environment.getProperty(hostName));
		email.setSmtpPort(Integer.valueOf(environment.getProperty(port)));
		email.setAuthentication(environment.getProperty(userName), environment.getProperty(password));
		email.setSSL(true);
		email.setFrom(environment.getProperty(userName));
		return email;
	}
}
