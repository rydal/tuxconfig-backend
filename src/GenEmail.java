import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GenEmail {
	public GenEmail(String to, PrintWriter out, String subject, String message_body) {
		
	try {
		String from = "noreply@linuxconf.feedthepenguin.org";
		// Recipient's email ID needs to be mentioned.
		String title = "<a href=\"https://linuxconf.feedthepenguin.org/hehe/index.jsp\"> <img src=\"https://linuxconf.feedthepenguin.org/hehe/img/linuxconf.png\"></a><br>";
		// Sender's email ID needs to be mentioned
		
		// Assuming you are sending email from localhost
		String host = "localhost";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(from));

		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

		// Set Subject: header field
		message.setSubject(subject);

		// Send the actual HTML message, as big as you like
		message.setContent(title + message_body, "text/html");

		// Send message
		Transport.send(message);
		System.out.println("Sent message successfully....");

	} catch (AddressException e) {
			out.write("Error" + e);
			;
		} catch (MessagingException e) {
			out.write("Error" + e);
		}
	}

	public static String randomString(int len) {
		String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random RANDOM = new Random();

		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
		}

		return sb.toString();
	}

}
