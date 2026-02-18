package cl.apihealthcheck.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import cl.apihealthcheck.config.DatabaseConnection;
import cl.apihealthcheck.entity.ApiRequest;

public class ApiRequestRepository {

    private static final Logger LOGGER = Logger.getLogger(ApiRequestRepository.class.getName());
    private static final String UPSERT_QUERY = 
        "INSERT INTO public.api_status (api_name, last_status, last_check, is_up, last_error_message) " +
        "VALUES (?, ?, ?, ?, ?) " +
        "ON CONFLICT (api_name) " +
        "DO UPDATE SET " +
        "    last_status = EXCLUDED.last_status, " +
        "    last_check = EXCLUDED.last_check, " +
        "    is_up = EXCLUDED.is_up, " +
        "    last_error_message = EXCLUDED.last_error_message";

    public static void upsertStatus(ApiRequest apiRequest) {
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ptsmt = conn.prepareStatement(UPSERT_QUERY)) {
                ptsmt.setString(1, apiRequest.getApiName());
                ptsmt.setInt(2, apiRequest.getLastStatus());
                ptsmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ptsmt.setBoolean(4, apiRequest.getIsUp());
                ptsmt.setString(5, apiRequest.getLastErrorMessage());
                ptsmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.severe("Ha ocurrido un error en UPSERT");
        }
    }
}
