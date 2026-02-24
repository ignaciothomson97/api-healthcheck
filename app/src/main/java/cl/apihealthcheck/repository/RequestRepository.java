package cl.apihealthcheck.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import cl.apihealthcheck.config.DatabaseConnection;
import cl.apihealthcheck.entity.ApiRequest;

public class RequestRepository {

    private static final Logger LOGGER = Logger.getLogger(RequestRepository.class.getName());
    private static final String INSERT_QUERY =
            "INSERT INTO public.api_status(api_name, status, checked, is_up, error_message) VALUES(?, ?, ?, ?, ?)";
    private static final String SELECT_QUERY =
            "SELECT * FROM public.api_status";
    private static final String TRUNCATE_QUERY =
            "TRUNCATE TABLE public.api_status";

    public void save(ApiRequest apiRequest) {
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement preparedStatement = Objects.requireNonNull(conn).prepareStatement(INSERT_QUERY)) {
                preparedStatement.setString(1, apiRequest.getApiName());
                preparedStatement.setInt(2, apiRequest.getStatus());
                preparedStatement.setTimestamp(3, apiRequest.getChecked());
                preparedStatement.setBoolean(4, apiRequest.getIsUp());
                preparedStatement.setString(5, apiRequest.getErrorMessage());
                preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.severe(() -> "Ha ocurrido un error al hacer INSERT en Base de Datos: " + e.getMessage());
        }
    }

    public List<ApiRequest> findAll() {
        List<ApiRequest> requests = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement preparedStatement = Objects.requireNonNull(conn).prepareStatement(SELECT_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String apiName = resultSet.getString("api_name");
                int status = resultSet.getInt("status");
                Timestamp lastCheck = resultSet.getTimestamp("checked");
                boolean isUp = resultSet.getBoolean("is_up");
                String errorMessage = resultSet.getString("error_message");

                ApiRequest apiRequest = new ApiRequest.Builder()
                        .apiName(apiName)
                        .status(status)
                        .checked(lastCheck)
                        .isUp(isUp)
                        .errorMessage(errorMessage)
                        .build();
                requests.add(apiRequest);
            }
        } catch (SQLException e) {
            LOGGER.severe(() -> "Ha ocurrido un error al hacer SELECT en Base de Datos: " + e.getMessage());
        }
        return requests;
    }

    public void cleanTable() {
        try (Connection conn = DatabaseConnection.connect();
             Statement statement = Objects.requireNonNull(conn).createStatement()) {
            statement.executeUpdate(TRUNCATE_QUERY);
        } catch (SQLException e) {
            LOGGER.severe(() -> "Ha ocurrido un error al hacer TRUNCATE en Base de Datos: " + e.getMessage());
        }
    }
}
