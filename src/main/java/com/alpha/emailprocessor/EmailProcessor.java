package com.alpha.emailprocessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class EmailProcessor {

	@Value("${spring.mail.properties.mail.transport.protocol}")
	private String protocol = "imap";
	@Value("${spring.mail.host}")
	private String host = "imap.gmail.com";
    @Value("${spring.mail.port}")
	private String port = "993";

	// your email id
	@Value("${spring.mail.username}")
	private String username;// Put here Gmail Username without @ sign
	// Your Email Password For Account
	@Value("${spring.mail.password}")
	private String password;
	
	@Value("${savepath}")
	private String saveDirectory;
	
	@Value("${searchSubject}")
	private String searchSubject;
	
	public void readMails() throws MessagingException, IOException
	{
		final Properties properties = new Properties();

		// server setting
		properties.put(String.format("mail.%s.host", protocol), host);
		properties.put(String.format("mail.%s.port", protocol), port);

		// SSL setting
		properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
		properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), port);
		
		//Get session object
		
		final Session session=Session.getInstance(properties,null);
		
		final Store store=session.getStore(protocol);
		// connect
		store.connect(host, username, password);
		
		session.setDebug(true);
		
		// opens the inbox folder
		Folder inbox=store.getFolder("INBOX");
		
		inbox.open(Folder.READ_WRITE);
 
		//Get unread messages
		Message[]  messages=inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
				
		for(Message message:messages)
		{
			//Get messages only if subject contains
			if (message.getSubject().contains(searchSubject))
			{
				System.out.println(downloadAttachments(message));
			}
		}
		
		
		 inbox.close(true);
		 store.close();

	}
	
	public List<String> downloadAttachments(Message message) throws IOException, MessagingException
	{
		
		List<String> attachments=new ArrayList<String>();
		
				Multipart multipart	=(Multipart) message.getContent();
				
				int partCount=multipart.getCount();
				
				for(int i=0;i<partCount;i++)
				{
					    MimeBodyPart mimeBodyPart=(MimeBodyPart) multipart.getBodyPart(i);
					    
					    if(mimeBodyPart.ATTACHMENT.equalsIgnoreCase(mimeBodyPart.getDisposition()))
					    {
					    	String file=mimeBodyPart.getFileName();
					    	mimeBodyPart.saveFile(saveDirectory+file);
					    	attachments.add(file);
					    }
					
				}
		

		return attachments;
		
	}
}
