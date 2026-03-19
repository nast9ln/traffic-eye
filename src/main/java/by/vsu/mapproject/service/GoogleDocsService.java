package by.vsu.mapproject.service;


import by.vsu.mapproject.entity.DailyTrafficSummary;
import by.vsu.mapproject.dto.TrafficReportResponse;
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

    public String createReport(String title, String content, String userEmail) throws Exception {
        Docs service = getDocsService(userEmail != null ? userEmail : "user");

        Document document = new Document().setTitle(title);
        document = service.documents().create(document).execute();

        String documentId = document.getDocumentId();

        List<Request> requests = new ArrayList<>();

        requests.add(new Request().setInsertText(new InsertTextRequest()
                .setText(content)
                .setLocation(new Location().setIndex(1))));


        Range range = new Range();
        range.setStartIndex(1);
        range.setEndIndex(title.length() + 1);

        TextStyle textStyle = new TextStyle();
        textStyle.setBold(true);
        Dimension fontSize = new Dimension();
        fontSize.setMagnitude(16.0);
        fontSize.setUnit("PT");
        textStyle.setFontSize(fontSize);

        UpdateTextStyleRequest updateTextStyleRequest = new UpdateTextStyleRequest();
        updateTextStyleRequest.setRange(range);
        updateTextStyleRequest.setTextStyle(textStyle);
        updateTextStyleRequest.setFields("bold,fontSize");

        requests.add(new Request().setUpdateTextStyle(updateTextStyleRequest));

        BatchUpdateDocumentRequest batchRequest = new BatchUpdateDocumentRequest();
        batchRequest.setRequests(requests);

        service.documents().batchUpdate(documentId, batchRequest).execute();

        return "https://docs.google.com/document/d/" + documentId;
    }

    public String createTrafficReport(TrafficReportResponse report, String userEmail) throws Exception {
        StringBuilder content = new StringBuilder();

        content.append("ОТЧЁТ ПО ЗАГРУЖЕННОСТИ ПЕРЕКРЁСТКА\n\n");
        content.append("Перекрёсток: ").append(report.getIntersectionName()).append("\n");
        content.append("Период: ").append(report.getPeriod()).append("\n");
        content.append("Дата формирования: ").append(java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n\n");

        content.append("Дата\t06:00-09:00\t\t09:00-12:00\t\t12:00-15:00\t\t15:00-18:00\t\t18:00-21:00\n");
        content.append("\tНапр.\tК-во\tНапр.\tК-во\tНапр.\tК-во\tНапр.\tК-во\tНапр.\tК-во\n");
        content.append("-".repeat(100)).append("\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (DailyTrafficSummary day : report.getDailyData()) {
            content.append(day.getDate().format(dateFormatter)).append("\t")
                    .append(formatValue(day.getPeriod1MaxDirection())).append("\t")
                    .append(day.getPeriod1MaxCount()).append("\t")
                    .append(formatValue(day.getPeriod2MaxDirection())).append("\t")
                    .append(day.getPeriod2MaxCount()).append("\t")
                    .append(formatValue(day.getPeriod3MaxDirection())).append("\t")
                    .append(day.getPeriod3MaxCount()).append("\t")
                    .append(formatValue(day.getPeriod4MaxDirection())).append("\t")
                    .append(day.getPeriod4MaxCount()).append("\t")
                    .append(formatValue(day.getPeriod5MaxDirection())).append("\t")
                    .append(day.getPeriod5MaxCount()).append("\n");
        }

        content.append("-".repeat(100)).append("\n\n");

        content.append("Примечание:\n");
        content.append("• В столбце 'Напр.' указано условное обозначение направления движения,\n");
        content.append("  для которого суммарное количество ожидающих автомобилей за период максимально.\n");
        content.append("• В столбце 'К-во' — значение этого максимума.\n");
        content.append("• Прочерк '-' означает отсутствие данных за период.\n");

        return createReport(
                "Отчёт по перекрёстку " + report.getIntersectionName() + " за " + report.getPeriod(),
                content.toString(),
                userEmail
        );
    }

    private String formatValue(String value) {
        return value != null && !value.equals("-") ? value : "-";
    }
}