package nl.xpagesandbeer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.UidGenerator;
import nl.defrog.JSFUtil;
import nl.defrog.Utils;

public class CalendarUtils {

	public static String getIcalForEvent(String eventDateStr) {
		String retVal = "";

		try {
			Database cmsDb = JSFUtil.getCurrentSession().getDatabase(JSFUtil.getCurrentDatabase().getServer(), Utils.getResource("settings", "cms_db"));
			View events = cmsDb.getView("events");
			Document event = events.getDocumentByKey(eventDateStr);
			retVal = getIcalForEvent(event);
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retVal;
	}

	@SuppressWarnings("deprecation")
	public static String getIcalForEvent(Document event) {
		String retVal = "";

		try {
			// Create a TimeZone
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			TimeZone timezone = registry.getTimeZone("Europe/Amsterdam");
			VTimeZone tz = timezone.getVTimeZone();

			// Start Date is on: April 1, 2008, 9:00 am
			Date eventDate = ((lotus.domino.DateTime) event.getItemValueDateTimeArray("date").get(0)).toJavaDate();
			Date startTime = ((lotus.domino.DateTime) event.getItemValueDateTimeArray("startTime").get(0)).toJavaDate();
			Date endTime = ((lotus.domino.DateTime) event.getItemValueDateTimeArray("endTime").get(0)).toJavaDate();

			Calendar startDate = Calendar.getInstance();
			startDate.setTimeZone(timezone);
			startDate.setTime(eventDate);
			startDate.set(java.util.Calendar.HOUR_OF_DAY, startTime.getHours());
			startDate.set(java.util.Calendar.MINUTE, startTime.getMinutes());
			startDate.set(java.util.Calendar.SECOND, 0);

			Calendar endDate = Calendar.getInstance();
			endDate.setTimeZone(timezone);
			endDate.setTime(eventDate);
			endDate.set(java.util.Calendar.HOUR_OF_DAY, endTime.getHours());
			endDate.set(java.util.Calendar.MINUTE, endTime.getMinutes());
			endDate.set(java.util.Calendar.SECOND, 0);

			// Create the event
			String eventName = JSFUtil.getCurrentDatabase().getTitle();
			DateTime start = new DateTime(startDate.getTime());
			DateTime end = new DateTime(endDate.getTime());
			VEvent meeting = new VEvent(start, end, eventName);

			// add timezone info..
			meeting.getProperties().add(tz.getTimeZoneId());

			// generate unique identifier..
			UidGenerator ug = new UidGenerator("uidGen");
			Uid uid = ug.generateUid();
			meeting.getProperties().add(uid);
			if (event.getItemValueString("companyId").length() > 0) {
				Database cmsDb = JSFUtil.getCurrentSession().getDatabase(JSFUtil.getCurrentDatabase().getServer(), Utils.getResource("settings", "cms_db"));
				Document company = cmsDb.getDocumentByUNID(event.getItemValueString("companyId"));
				String location = company.getItemValueString("name") + "\n" + company.getItemValueString("address") + "\n" + company.getItemValueString("zipcode") + "  " + company.getItemValueString("city");
				meeting.getProperties().add(new Location(location));
			}
			// add attendees..
			// Attendee dev = new Attendee(URI.create("mailto:dev1@mycompany.com"));
			// dev.getParameters().add(Role.REQ_PARTICIPANT);
			// dev.getParameters().add(new Cn("Developer 1"));
			// meeting.getProperties().add(dev);

			// Create a calendar
			net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
			icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
			icsCalendar.getProperties().add(CalScale.GREGORIAN);

			// Add the event and print
			icsCalendar.getComponents().add(meeting);
			retVal = icsCalendar.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return retVal;
	}

	public static String dateFormatter(Vector<lotus.domino.DateTime> dates, String format) throws NotesException {
		return dateFormatter(dates.get(0).toJavaDate(), format);
	}

	public static String dateFormatter(lotus.domino.DateTime date, String format) throws NotesException {
		return dateFormatter(date.toJavaDate(), format);
	}

	public static String dateFormatter(Date date, String format) {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(format, new Locale("nl", "NL"));

		return DATE_FORMAT.format(date);
	}
}
