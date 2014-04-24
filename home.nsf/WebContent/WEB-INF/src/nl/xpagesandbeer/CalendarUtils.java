package nl.xpagesandbeer;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import org.openntf.domino.*;
import org.openntf.domino.utils.Factory;

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

		Database cmsDb = Factory.getSession().getDatabase(Factory.getSession().getCurrentDatabase().getServer(), Utils.getResource("settings", "cms_db"));
		View events = cmsDb.getView("events");
		Document event = events.getDocumentByKey(eventDateStr);
		retVal = getIcalForEvent(event);

		return retVal;
	}

	@SuppressWarnings("deprecation")
	public static String getIcalForEvent(Document event) {
		String retVal = "";

		// Create a TimeZone
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone("Europe/Amsterdam");
		VTimeZone tz = timezone.getVTimeZone();

		// Start Date is on: April 1, 2008, 9:00 am
		// Date eventDate = ((DateTime) event.getItemValueDateTimeArray("date").get(0)).toJavaDate();
		// Date startTime = ((DateTime) event.getItemValueDateTimeArray("startTime").get(0)).toJavaDate();
		// Date endTime = ((DateTime) event.getItemValueDateTimeArray("endTime").get(0)).toJavaDate();
		Date eventDate = event.getItemValue("date", java.util.Date.class);
		Date startTime = event.getItemValue("startTime", java.util.Date.class);
		Date endTime = event.getItemValue("endTime", java.util.Date.class);

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
		String eventName = Factory.getSession().getCurrentDatabase().getTitle();
		DateTime start = new DateTime(startDate.getTime());
		DateTime end = new DateTime(endDate.getTime());
		VEvent meeting = new VEvent(start, end, eventName);

		// add timezone info..
		meeting.getProperties().add(tz.getTimeZoneId());

		// generate unique identifier..
		UidGenerator ug = null;
		try {
			ug = new UidGenerator("uidGen");
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Uid uid = ug.generateUid();
		meeting.getProperties().add(uid);
		if (event.getItemValueString("companyId").length() > 0) {
			Database cmsDb = Factory.getSession().getDatabase(Factory.getSession().getCurrentDatabase().getServer(), Utils.getResource("settings", "cms_db"));
			Document company = cmsDb.getDocumentByUNID(event.getItemValueString("companyId"));
			String location = company.getItemValueString("name") + "\n" + company.getItemValueString("address") + "\n" + company.getItemValueString("zipcode")
					+ "  " + company.getItemValueString("city");
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

		return retVal;
	}

//	public static String dateFormatter(Vector<lotus.domino.DateTime> dates, String format) {
//		return dateFormatter(dates.get(0).toJavaDate(), format);
//	}

//	public static String dateFormatter(lotus.domino.DateTime date, String format) {
//		return dateFormatter(date.toJavaDate(), format);
//	}

	public static String dateFormatter(Date date, String format) {
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(format, new Locale("nl", "NL"));

		return DATE_FORMAT.format(date);
	}
}
