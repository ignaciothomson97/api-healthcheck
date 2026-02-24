package cl.apihealthcheck.helper;

import cl.apihealthcheck.entity.ApiRequest;
import cl.apihealthcheck.repository.RequestRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class DataHandler {
    private static final Logger LOGGER = Logger.getLogger(DataHandler.class.getName());
    private static final String BACKUP_DIRECTORY = "db-backup/";
    private final RequestRepository requestRepository;

    public DataHandler(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public void exportRecords() {
        LOGGER.info("Respaldo CSV en proceso...");

        List<ApiRequest> requests = requestRepository.findAll();
        if (requests.isEmpty()) {
            LOGGER.info("La BD está vacía. Nada que exportar");
            return;
        }

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Path directoryPath = Paths.get(BACKUP_DIRECTORY);
        Path filePath = directoryPath.resolve("api_monitor_backup_" + currentDate + ".csv");

        try {
            Files.createDirectories(directoryPath);
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write("\"API Name\",\"Status Code\",Checked,\"Is UP\",\"Error Message\"\n");
                for (ApiRequest apiRequest : requests) { writer.write(generateCsvLine(apiRequest)); }
                LOGGER.info("Respaldo CSV Generado exitosamente en: " + filePath.toAbsolutePath());
            }
            requestRepository.cleanTable();
            LOGGER.info("Registros de la Base de Datos limpiados exitosamente.");
        } catch (IOException e) {
            LOGGER.severe("Error crítico al intentar escribir el archivo CSV: " + e.getMessage());
        }
    }

    private String generateCsvLine(ApiRequest apiRequest) {
        return String.format("\"%s\",%s,%s,%s,\"%s\"\n",
                escapeCsv(apiRequest.getApiName()),
                apiRequest.getStatus(),
                apiRequest.getChecked(),
                apiRequest.getIsUp(),
                escapeCsv(apiRequest.getErrorMessage()));
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
