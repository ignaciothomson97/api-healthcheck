package cl.apihealthcheck.records;

public record RestResponse(int statusCode, String body, String errorMessage) {
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode <= 300;
    }
}
