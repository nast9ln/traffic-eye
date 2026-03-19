package by.vsu.mapproject.service;

import by.vsu.mapproject.entity.Duty;
import by.vsu.mapproject.entity.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final GoogleOAuth2Service oAuth2Service;

    private Calendar getCalendarService(String userId) throws Exception {
        Credential credential = oAuth2Service.authorize(userId);
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("Duty Scheduler")
                .build();
    }

    public String createEvent(Duty duty) throws Exception {
        User employee = duty.getEmployee();
        if (employee.getCalendarId() == null) {
            employee.setCalendarId("primary");
        }

        Calendar service = getCalendarService(employee.getEmail() != null ?
                employee.getEmail() : "user");

        Event event = new Event()
                .setSummary("Дежурство в зоне: " + duty.getZone().getName())
                .setDescription(String.format("Дежурство сотрудника %s в зоне %s",
                        employee.getUsername(), duty.getZone().getName()));

        LocalDateTime startDateTime = duty.getDate().atTime(duty.getStartTime());
        LocalDateTime endDateTime = duty.getDate().atTime(duty.getEndTime());

        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

        event.setStart(new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDate)));
        event.setEnd(new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDate)));

        EventReminder reminder1Day = new EventReminder()
                .setMethod("popup")
                .setMinutes(24 * 60);

        EventReminder reminder1Hour = new EventReminder()
                .setMethod("popup")
                .setMinutes(60);

        List<EventReminder> reminderOverrides = Arrays.asList(reminder1Day, reminder1Hour);

        event.setReminders(new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(reminderOverrides));

        event.setColorId("7");

        event = service.events().insert(employee.getCalendarId(), event).execute();

        return event.getId();
    }

    public void updateEvent(Duty duty) throws Exception {
        if (duty.getGoogleEventId() == null) {
            return;
        }

        User employee = duty.getEmployee();
        Calendar service = getCalendarService(employee.getEmail() != null ?
                employee.getEmail() : "user");

        Event event = service.events().get(employee.getCalendarId(), duty.getGoogleEventId()).execute();

        LocalDateTime startDateTime = duty.getDate().atTime(duty.getStartTime());
        LocalDateTime endDateTime = duty.getDate().atTime(duty.getEndTime());

        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

        event.setStart(new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDate)));
        event.setEnd(new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDate)));

        service.events().update(employee.getCalendarId(), event.getId(), event).execute();
    }

    public void deleteEvent(Duty duty) throws Exception {
        if (duty.getGoogleEventId() == null) {
            return;
        }

        User employee = duty.getEmployee();
        Calendar service = getCalendarService(employee.getEmail() != null ?
                employee.getEmail() : "user");

        service.events().delete(employee.getCalendarId(), duty.getGoogleEventId()).execute();
    }
}