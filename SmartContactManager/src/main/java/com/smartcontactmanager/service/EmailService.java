package com.smartcontactmanager.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	//this is responsible to send only text email
		public boolean sendEmailForText(String message, String subject, String to, String from) {
			//variable for gmail host
			boolean f=false;
			String host="smtp.gmail.com"; 
			
			//get the system properties
			Properties properties = System.getProperties();
			System.out.println("System properties "+properties);
			
			//setting important information to properties object
			
			//setting host
			properties.setProperty("mail.smtp.host",host);
			properties.setProperty("mail.smtp.port","465");
			properties.setProperty("mail.smtp.ssl.enable", "true");
			properties.setProperty("mail.smtp.auth", "true");
			
			//Step 1: To get the session object
			
			Session session = Session.getInstance(properties, new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// TODO Auto-generated method stub
					return new PasswordAuthentication("pradhanmanish9502@gmail.com", "9570@Tin9502");
				}
				
				
			});
			
			//To debug the session
			session.setDebug(true);
			//Step 2: Compose the message [text,multimedia]
			
			MimeMessage mimeMessage = new MimeMessage(session);
			try {
				
				//from mail
				mimeMessage.setFrom(from);
				//adding recipient to message
				mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
				//adding subject to message
				mimeMessage.setSubject(subject);
				//adding text to message
				//mimeMessage.setText(message);
				mimeMessage.setContent(message,"text/html");
				
				//send
				//Step 3: Send the message using Transport class
				Transport.send(mimeMessage);
				
				System.out.println("Mail sent successfully..");
				f=true;
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				f=false;
			}
			
			
			return f;
			
		}
	    
	

}
