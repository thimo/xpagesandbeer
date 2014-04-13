package nl.xpagesandbeer;

import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.Session;
import lotus.domino.Stream;

import nl.defrog.JSFUtil;

public class Mail {
	public static void sendRegistrationThankyou(Document registrationDoc, Document eventDoc) {
		try {
			Session session = JSFUtil.getCurrentSession();
			session.setConvertMIME(false);

			Document doc = registrationDoc.getParentDatabase().createDocument();
			doc.replaceItemValue("Form", "Memo");
			doc.replaceItemValue("SendTo", registrationDoc.getItemValueString("email"));
			doc.replaceItemValue("Principal", Helper.getSetting("email_info") + " ");
			doc.replaceItemValue("From", Helper.getSetting("email_info"));
			doc.replaceItemValue("BlindCopyTo", Helper.getSetting("email_info"));

			MIMEEntity body = doc.createMIMEEntity();
			body.createHeader("Subject").setHeaderVal(Helper.getSetting("email_registration_subject"));
			body.createHeader("Content-Type").setHeaderVal("multipart/alternative");

			Stream stream = session.createStream();

			DateTime eventDate = (DateTime) eventDoc.getItemValueDateTimeArray("date").get(0);

			String firstname = Helper.getFirstName(registrationDoc.getItemValueString("name"));
			stream.writeText(Helper.getSetting("email_registration_body")
					.replace("[name]", firstname)
					.replace("[date]", CalendarUtils.dateFormatter(eventDate, "EEEE d MMMM")));

			MIMEEntity plaintext = body.createChildEntity();
			plaintext.setContentFromText(stream, "text/plain;charset=UTF-8", MIMEEntity.ENC_IDENTITY_7BIT);
			MIMEEntity richtext = body.createChildEntity();
			richtext.setContentFromText(stream, "text/html;charset=UTF-8", MIMEEntity.ENC_IDENTITY_8BIT);

			stream.close();

			// Add ical attachment for event
			MIMEEntity mimeAttachment = body.createChildEntity();

			String filename = JSFUtil.getCurrentDatabase().getTitle() + " " + CalendarUtils.dateFormatter(eventDate, "dd-MM-yyyy") + ".ics";
			MIMEHeader mimeHeader = mimeAttachment.createHeader("Content-Disposition");
			mimeHeader.setHeaderVal("attachment; filename=\"" + filename + "\"");

			Stream icalStream = session.createStream();
			icalStream.writeText(CalendarUtils.getIcalForEvent(eventDoc));
			mimeAttachment.setContentFromText(icalStream, "text/calendar; filename=\"" + filename + "\"", MIMEEntity.ENC_IDENTITY_8BIT);
			icalStream.close();

			doc.send();
			doc.save();

			doc.recycle();
			session.setConvertMIME(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
