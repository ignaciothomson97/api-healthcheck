package cl.apihealthcheck.entity;

public class ApiRequest {
    private final String apiName;
    private final Integer lastStatus;
    private final boolean isUp;
    private final String lastErrorMessage;

    private ApiRequest(Builder builder) {
        this.apiName = builder.apiName;
        this.lastStatus = builder.lastStatus;
        this.isUp = builder.isUp;
        this.lastErrorMessage = builder.lastErrorMessage;
    }

    public String getApiName() { return apiName; }
    public Integer getLastStatus() { return lastStatus; }
    public boolean getIsUp() { return isUp; }
    public String getLastErrorMessage() { return lastErrorMessage; }

    public static class Builder {
        private String apiName;
        private Integer lastStatus;
        private boolean isUp;
        private String lastErrorMessage;

        public Builder apiName(String apiName) {
            this.apiName = apiName;
            return this;
        }

        public Builder lastStatus(Integer lastStatus) {
            this.lastStatus = lastStatus;
            return this;
        }

        public Builder isUp(boolean isUp) {
            this.isUp = isUp;
            return this;
        }

        public Builder lastErrorMessage(String lastErrorMessage) {
            this.lastErrorMessage = lastErrorMessage;
            return this;
        }

        public ApiRequest build() {
            return new ApiRequest(this);
        }
    }
}
