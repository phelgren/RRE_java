package com.valadd.util;


import javax.mail.*;
import javax.mail.internet.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 *  E-mailer for sending messages to a SMTP mail server.
 *
 *  <p> <b> To Send a Simple Text Message </b> </p>
 *
 *  Usage Example:
 *
 *  <pre>
 *
 *      String smtpHost = "smtp.server.ltree";
 *      String from = "subscription-dept@server.ltree";
 *      String to = "ltree8@server.ltree";
 *      String subject = "Subscription Confirmation";
 *      String text = "Thanks for subscribing to our magazine!";
 *
 *      try {
 *          EzMailer.sendMessage(smtpHost, from, to, subject, text);
 *
 *          System.out.println("Message successfully sent to: " + to);
 *      }
 *      catch (javax.mail.MessagingException exc) {
 *          System.out.println("Couldn't send e-mail: " + exc.toString());
 *      }
 *
 *  </pre>
 *
 *	<hr>
 *  <p> <b> Servlet/JSP Example:  Sending an Attachment </b> </p>
 *
 *  Usage Example:
 *
 *  <pre>
 *
 *      String smtpHost = "smtp.server.ltree";
 *      String from = "subscription-dept@server.ltree";
 *      String to = "ltree8@server.ltree";
 *      String subject = "Subscription Confirmation";
 *      String text = "Thanks for subscribing to our magazine!";
 *
 *      String[] fileNamesList = new String[2];
 *      ServletContext context = getServletContext();      // inherited from GenericServlet
 *
 *      // Attach two files stored in our web app directory "/pubs/"
 *      //
 *      //  Need to retrieve the complete filesystem path for the files
 *      //  (ie c:\dev\demoapp\website\pubs\java_courses.pdf)
 *      //  Accomplished with context.getRealPath(...).
 *      //
 *      //  The method context.getRealPath(...) will return the root directory for
 *      //  your web application, for example "c:\crs570\website".
 *      //  It will also append the String parameter that is passed in.
 *      //
 *      fileNamesList[0] = context.getRealPath("/pubs/java_courses.pdf");
 *      fileNamesList[1] = context.getRealPath("/pubs/xml_courses.pdf");
 *
 *      try {
 *            EzMailer.sendMessageAttach(smtpHost, from, to, subject, text, fileNamesList);
 *            log("Sent message to: " + to);
 *      }
 *      catch (javax.mail.MessagingException exc) {
 *            log("Problems sending e-mail: " + exc.toString());
 *      }
 *
 *  </pre>
 *
 *  @author 570 Development Team
 */
public class EzMailer {


	/**
	 *  Sends a simple text e-mail message.
	 *
	 *  @param smtpHost the SMTP mail server
	 *  @param fromEmail address of the sender (ie maxwell@grandpuba.com)
	 *  @param toEmail e-mail address of the recipient (ie eric@eazye.com)
	 *  @param subject the subject
	 *  @param messageText the message text
	 *
	 *  @throws javax.mail.MessagingException errors sending message
	 */
	public static void sendMessage(String smtpHost,
								   String fromEmail, String toEmail,
								   String subject, String messageText)
	  throws javax.mail.MessagingException {

		String[] toEmailList = {toEmail};

		sendMessage(smtpHost, fromEmail, toEmailList, subject, messageText);
	}

	/**
	 *  Sends a simple text e-mail message to a list of recipients
	 *
	 *  @param smtpHost the SMTP mail server
	 *  @param fromEmail address of the sender (ie maxwell@grandpuba.com)
	 *  @param toEmailList e-mail addresses of the recipients
	 *  @param subject the subject
	 *  @param messageText the message text
	 *
	 *  @throws javax.mail.MessagingException errors sending message
	 */
	public static void sendMessage(String smtpHost,
								   String fromEmail, String[] toEmailList,
								   String subject, String messageText)
	  throws javax.mail.MessagingException {


		// start a session with given properties
		java.util.Properties props = new java.util.Properties();
		props.setProperty("mail.smtp.host", smtpHost);
		Session mailSession = Session.getDefaultInstance(props);

		InternetAddress fromAddress = new InternetAddress(fromEmail);

		// construct a message
		MimeMessage myMessage = new MimeMessage(mailSession);
		myMessage.setFrom(fromAddress);

		for (int i=0; i < toEmailList.length; i++) {
			myMessage.addRecipient(javax.mail.Message.RecipientType.TO,
									new InternetAddress(toEmailList[i]));
		}

	    myMessage.setSentDate(new java.util.Date());
		myMessage.setSubject(subject);
		myMessage.setText(messageText);

		// now send the message!
		Transport.send(myMessage);
	}

	/**
	 *  Sends an e-mail message to a list of recipients with attachment(s)
	 *
	 *  @param smtpHost name of the SMTP mail server
	 *  @param from e-mail address of the sender
	 *  @param to e-mailList addresses of the recipients
	 *  @param subject subject of the e-mail message
	 *  @param messageText the text of the message
	 *  @param fileNameList the names of the files to attach. Each file name is the complete filesystem path of the file to attach (ie c:\dev\demoapp\website\pubs\atari.pdf).  If you are developing a servlet/JSP then see the usage example at the beginning of this file
	 *
	 *  @throws javax.mail.MessagingFormatException problems sending message
	 */
	public static void sendMessageAttach(String smtpHost,
								   		 String from, String[] toEmailList,
								   		 String subject, String messageText,
								   		 String[] fileNameList)
	  throws MessagingException {

		// Configure the mail session
		java.util.Properties props = new java.util.Properties();
		props.setProperty("mail.smtp.host", smtpHost);
		Session mailSession = Session.getDefaultInstance(props);

		// Construct the message
		InternetAddress fromAddress = new InternetAddress(from);

		MimeMessage testMessage = new MimeMessage(mailSession);
		testMessage.setFrom(fromAddress);

		for (int i=0; i < toEmailList.length; i++) {
			testMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toEmailList[i]));
		}

	    testMessage.setSentDate(new java.util.Date());
		testMessage.setSubject(subject);

	    //  Create a body part to hold the "text" portion of the message
	    MimeBodyPart textBodyPart = new MimeBodyPart();
	    textBodyPart.setText(messageText);

	    // Create a Multipart/container and add the text parts
	    Multipart container = new MimeMultipart();
	    container.addBodyPart(textBodyPart);

	    //  Loop thru the file names
	    //  Create a body part to hold the "file" portion of the message
	    MimeBodyPart fileBodyPart;
	    FileDataSource fds;

		for (int j=0; j < fileNameList.length; j++) {
			fileBodyPart = new MimeBodyPart();
			fds = new FileDataSource(fileNameList[j]);
			fileBodyPart.setDataHandler(new DataHandler(fds));
			fileBodyPart.setFileName(fds.getName());
		    container.addBodyPart(fileBodyPart);
		}

	    // Add the Multipart to the actual message
	    testMessage.setContent(container);

		// Now send the message
		Transport.send(testMessage);
	}

	/**
	 *  Sends an e-mail message with attachment(s)
	 *
	 *  @param smtpHost name of the SMTP mail server
	 *  @param from e-mail address of the sender
	 *  @param toEmail e-mail address of the recipient
	 *  @param subject subject of the e-mail message
	 *  @param messageText the text of the message
	 *  @param fileNameList the names of the files to attach. Each file name is the complete filesystem path of the file to attach (ie c:\dev\demoapp\website\pubs\atari.pdf).  If you are developing a servlet/JSP then see the usage example at the beginning of this file.
	 *
	 *  @throws javax.mail.MessagingFormatException problems sending message
	 */
	public static void sendMessageAttach(String smtpHost,
								   		 String from, String toEmail,
								   		 String subject, String messageText,
								   		 String[] fileNameList)
	  throws MessagingException {

		String[] toEmailList = {toEmail};
		sendMessageAttach(smtpHost, from, toEmailList, subject, messageText, fileNameList);
	}

	/**
	 *  Sends an e-mail message with an attachment
	 *
	 *  @param smtpHost name of the SMTP mail server
	 *  @param from e-mail address of the sender
	 *  @param toEmail e-mail address of the recipient
	 *  @param subject subject of the e-mail message
	 *  @param messageText the text of the message
	 *  @param fileName the complete filesystem path of the file to attach (ie c:\dev\demoapp\website\pubs\atari.pdf).
	 *
	 *  @throws javax.mail.MessagingFormatException problems sending message
	 */
	public static void sendMessageAttach(String smtpHost,
								   		 String from, String toEmail,
								   		 String subject, String messageText,
								   		 String fileName)
	  throws MessagingException {

		String[] toEmailList = {toEmail};
		String[] fileNameList = {fileName};
		sendMessageAttach(smtpHost, from, toEmailList, subject, messageText, fileNameList);
	}

}