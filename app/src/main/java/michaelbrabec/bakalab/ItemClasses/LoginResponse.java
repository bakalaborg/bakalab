package michaelbrabec.bakalab.ItemClasses;


public class LoginResponse {

    private boolean success = false;
    private String errorMessage = null;
    private String[] successResponse = null;

    public LoginResponse() {

    }

    public LoginResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public LoginResponse(boolean success, String[] successResponse) {
        this.success = success;
        this.successResponse = successResponse;
    }

    public boolean wasSuccessfull() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String[] getSuccessResponse() {
        return successResponse;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setSuccessResponse(String[] successResponse) {
        this.successResponse = successResponse;
    }
}
