package nl.xpagesandbeer;

import java.util.Date;

import org.openntf.domino.*;
import org.openntf.domino.utils.*;

import nl.defrog.JSFUtil;

public class Mail {
	public static void sendRegistrationThankyou(Document registrationDoc, Document eventDoc) {
		try {
			Session session = Factory.getSession();
			session.setConvertMIME(false);

			Document doc = registrationDoc.getParentDatabase().createDocument();
			doc.replaceItemValue("Form", "Memo");
			doc.replaceItemValue("SendTo", registrationDoc.getItemValueString("email"));
			doc.replaceItemValue("Principal", Helper.getSetting("email_info") + " ");
			doc.replaceItemValue("From", Helper.getSetting("email_info"));
			doc.replaceItemValue("BlindCopyTo", Helper.getSetting("email_info"));

			Date eventDate = eventDoc.getItemValue("date", java.util.Date.class);
			
			MIMEEntity mixed = doc.createMIMEEntity();
			mixed.createHeader("Subject").setHeaderVal(Helper.getSetting("email_registration_subject"));
			//body.createHeader("Content-Type").setHeaderVal("multipart/alternative");
			mixed.createHeader("Content-Type").setHeaderVal("multipart/mixed");

			MIMEEntity multipart = mixed.createChildEntity();
			multipart.createHeader("Content-Type").setHeaderVal("multipart/alternative");

			String firstname = Helper.getFirstName(registrationDoc.getItemValueString("name"));

			Stream htmlStream = session.createStream();
			htmlStream.writeText(Helper.getSetting("email_registration_body")
					.replace("[name]", firstname)
					.replace("[date]", CalendarUtils.dateFormatter(eventDate, "EEEE d MMMM")));
			Stream plainStream = session.createStream();
			htmlStream.setPosition(0);
			plainStream.writeText(htmlStream.readText().replaceAll("\\<.*?\\>", ""));

			MIMEEntity plaintext = multipart.createChildEntity();
			plaintext.setContentFromText(plainStream, "text/plain;charset=UTF-8", MIMEEntity.ENC_IDENTITY_7BIT);
			MIMEEntity richtext = multipart.createChildEntity();
			richtext.setContentFromText(htmlStream, "text/html;charset=UTF-8", MIMEEntity.ENC_IDENTITY_8BIT);

			htmlStream.close();

			// Add ical attachment for event
			MIMEEntity mimeAttachment = mixed.createChildEntity();

			String filename = JSFUtil.getCurrentDatabase().getTitle() + " " + CalendarUtils.dateFormatter(eventDate, "dd-MM-yyyy") + ".ics";
			MIMEHeader mimeHeader = mimeAttachment.createHeader("Content-Disposition");
			mimeHeader.setHeaderVal("attachment; filename=\"" + filename + "\"");

			Stream icalStream = session.createStream();
			icalStream.writeText(CalendarUtils.getIcalForEvent(eventDoc));
			mimeAttachment.setContentFromText(icalStream, "text/calendar; filename=\"" + filename + "\"", MIMEEntity.ENC_IDENTITY_8BIT);
			icalStream.close();

			doc.send();
			doc.save();

			session.setConvertMIME(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
