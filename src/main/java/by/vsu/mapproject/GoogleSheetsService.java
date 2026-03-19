package by.vsu.mapproject;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleSheetsService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final GoogleOAuth2Service oAuth2Service;
    private final DutyResultRepository dutyResultRepository;

    private Sheets getSheetsService(String userId) throws Exception {
        Credential credential = oAuth2Service.authorize(userId);
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("Duty Scheduler")
                .build();
    }

    public DutyResult importDutyResult(String spreadsheetId, String range, Duty duty) throws Exception {
        User manager = duty.getEmployee(); // или получить менеджера
        Sheets service = getSheetsService(manager.getEmail() != null ?
                manager.getEmail() : "user");

        // Чтение данных из таблицы
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            throw new RuntimeException("No data found in sheet");
        }

        // Создание результата дежурства
        DutyResult result = new DutyResult();
        result.setDuty(duty);
        result.setSpreadsheetId(spreadsheetId);
        result.setSheetName(range.split("!")[0]);
        result.setData(values.toString());
        result.setImportedAt(LocalDateTime.now());

        return dutyResultRepository.save(result);
    }

    public List<List<Object>> readSheet(String spreadsheetId, String range, String userEmail) throws Exception {
        Sheets service = getSheetsService(userEmail != null ? userEmail : "user");

        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        return response.getValues();
    }
}