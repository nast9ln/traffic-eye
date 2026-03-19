package by.vsu.mapproject;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleDocsService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final GoogleOAuth2Service oAuth2Service;

    private Docs getDocsService(String userId) throws Exception {
        Credential credential = oAuth2Service.authorize(userId);
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Docs.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("Duty Scheduler")
                .build();
    }

    public String createReport(String title, String period, List<DutyDTO> duties,
                               String managerEmail) throws Exception {
        Docs service = getDocsService(managerEmail != null ? managerEmail : "user");

        // Создание документа
        Document document = new Document().setTitle(title);
        document = service.documents().create(document).execute();

        String documentId = document.getDocumentId();

        // Формирование содержимого отчета
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("Отчет по дежурствам\n\n");
        reportContent.append("Период: ").append(period).append("\n\n");
        reportContent.append("Дата и время составления: ")
                .append(java.time.LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n\n");

        reportContent.append("Список дежурств:\n");
        reportContent.append("=".repeat(50)).append("\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (DutyDTO duty : duties) {
            reportContent.append(String.format("\nДата: %s\n",
                    duty.getDate().format(dateFormatter)));
            reportContent.append(String.format("Время: %s - %s\n",
                    duty.getStartTime().format(timeFormatter),
                    duty.getEndTime().format(timeFormatter)));
            reportContent.append(String.format("Сотрудник: %s\n", duty.getEmployeeName()));
            reportContent.append(String.format("Зона: %s\n", duty.getZoneName()));
            reportContent.append(String.format("Статус: %s\n", duty.getStatus()));
            if (duty.getResultLink() != null) {
                reportContent.append(String.format("Результат: %s\n", duty.getResultLink()));
            }
            reportContent.append("-".repeat(40)).append("\n");
        }

        // Вставка текста в документ
        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setInsertText(new InsertTextRequest()
                .setText(reportContent.toString())
                .setLocation(new Location().setIndex(1))));

        BatchUpdateDocumentRequest batchRequest = new BatchUpdateDocumentRequest()
                .setRequests(requests);

        service.documents().batchUpdate(documentId, batchRequest).execute();

        return "https://docs.google.com/document/d/" + documentId;
    }
}