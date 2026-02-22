package cl.apihealthcheck.entity;

import java.sql.Timestamp;

public class ApiRequest {
    private final String apiName;
    private final Integer status;
    private final Timestamp checked;
    private final boolean isUp;
    private final String errorMessage;

    private ApiRequest(Builder builder) {
        this.apiName = builder.apiName;
        this.status = builder.status;
        this.checked = builder.checked;
        this.isUp = builder.isUp;
        this.errorMessage = builder.errorMessage;
    }

    public String getApiName() { return apiName; }
    public Integer getStatus() { return status; }
    public Timestamp getChecked() { return checked; }
    public boolean getIsUp() { return isUp; }
    public String getErrorMessage() { return errorMessage; }

    public static class Builder {
        private String apiName;
        private Integer status;
        private Timestamp checked;
        private boolean isUp;
        private String errorMessage;

        public Builder apiName(String apiName) {
            this.apiName = apiName;
            return this;
        }

        public Builder status(Integer status) {
            this.status = status;
            return this;
        }

        public Builder checked(Timestamp checked) {
            this.checked = checked;
            return this;
        }

        public Builder isUp(boolean isUp) {
            this.isUp = isUp;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public ApiRequest build() {
            return new ApiRequest(this);
        }
    }
}
