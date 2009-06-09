/**
 *  Created by Isocron Sistemas Computacionales S.A. de C.V.
 *  <br><br/>
 *  This file is free software: you can redistribute it and/or modify<br/>
 *  it under the terms of the GNU General Public License as published by<br/>
 *  the Free Software Foundation, either version 3 of the License, or<br/>
 *  (at your option) any later version.<br/>
 *  <br/>
 *  This file is distributed in the hope that it will be useful,<br/>
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of<br/>
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br/>
 *  GNU General Public License for more details.<br/>
 *  <br/>
 *  You should have received a copy of the GNU General Public License<br/>
 *  along with this file.  If not, see <http://www.gnu.org/licenses/>.<br/>
 *  
 * @author JuanRodriguez
 * @author MiguelGarcia
 **/
package org.pokenet.server.network;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SMTPManager {
	
	String host = "smtp.gmail.com";
	String account = "email@gmail.com";
	String password = "PlainTextPassword";
	String display = "Display Name";
	String mbox = "INBOX";
	String protocol = "smtp";
	String port = "465";
	URLName url = new URLName(this.protocol, this.host, -1, this.mbox, this.account, this.password);
	Properties props = System.getProperties();
	Session session = null;
	
	public SMTPManager() {
		super();
		try {
			this.props.put("mail.smtp.auth", "true");
			this.props.put("mail.smtp.starttls.enable","true");
			this.props.put("mail.smtp.socketFactory.port", "465");
			this.props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			this.props.put("mail.smtp.socketFactory.fallback", "false");
			this.props.put("mail.smtp.host", this.host);
			this.props.put("mail.smtp.port", this.port);
			this.props.put("mail.smtp.auth", "true");
			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(account, password); } };
			this.session = Session.getInstance(this.props, auth);
        	this.session.setDebug(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int sendMail(List<String> toRecipients, List<String> ccRecipients,List<String> bccRecipients, String subject, String body) throws UnsupportedEncodingException, MessagingException {
		Message msg = new MimeMessage(this.session);
		InternetAddress address = new InternetAddress(this.account, this.display);
		msg.setFrom(address);
		ListIterator<String> iterator = toRecipients.listIterator();
		if(toRecipients != null) {
			while(iterator.hasNext()){
				try {
					InternetAddress toAddress = new InternetAddress(iterator.next());
					msg.addRecipient(Message.RecipientType.TO,toAddress);
				} catch(Exception e) {}
			}
		}
		if(ccRecipients != null) {
			iterator = ccRecipients.listIterator();
			while(iterator.hasNext()){
				try {
					InternetAddress ccAddress = new InternetAddress(iterator.next());
					msg.addRecipient(Message.RecipientType.CC,ccAddress);
				} catch(Exception e) {}
			}
		}
		if(bccRecipients != null) {
			iterator = bccRecipients.listIterator();
			while(iterator.hasNext()){
				try {
					InternetAddress bccAddress = new InternetAddress(iterator.next());
					msg.addRecipient(Message.RecipientType.BCC,bccAddress);
				} catch(Exception e) {}
			}
		}
		msg.setSubject(subject);
		msg.setContent(body, "text/html");
		Transport.send(msg);
		return 1;
	}
}