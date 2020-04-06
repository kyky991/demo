package connector;

/**
 * @author Zing
 * @date 2020-04-03
 */
public enum HttpStatus {

    SC_OK(200, "OK"),
    SC_NOT_FOUND(404, "Not Found"),
    ;

    private int statusCode;
    private String reason;

    HttpStatus(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReason() {
        return reason;
    }
}
