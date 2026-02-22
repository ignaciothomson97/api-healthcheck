package cl.apihealthcheck.helper;

import cl.apihealthcheck.entity.ApiRequest;
import cl.apihealthcheck.repository.RequestRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class CsvExport {

    private static final Logger LOGGER = Logger.getLogger(CsvExport.class.getName());
    private final RequestRepository requestRepository = new RequestRepository();

    public void exportAndCleanRecords() {
        LOGGER.info("Iniciando volcado de BD a CSV...");
        List<ApiRequest> requests = requestRepository.findAll();

        if (requests.isEmpty()) {
            LOGGER.info("La BD está vacía. Nada que exportar");
            return;
        }

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fileName = "api_monitor_backup_" + currentDate + ".csv";
        Path filePath = Paths.get(fileName);

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("API Name,Status Code,Is UP,Error Message\n");

        for (ApiRequest req : requests) {
            csvContent.append(req.getApiName()).append(",")
                    .append(req.getStatus()).append(",")
                    .append(req.getIsUp()).append(",")
                    .append(req.getErrorMessage()).append(",");
        }

        try {
            Files.writeString(filePath, csvContent.toString());
            LOGGER.info("Respaldo CSV Generado exitosamente en: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            LOGGER.severe("Error crítico al intentar escribir el archivo CSV: " + e.getMessage());
        }
    }

}
