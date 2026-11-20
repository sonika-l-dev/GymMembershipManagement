package exception;

/**
 * Custom exception thrown when member data is invalid.
 * For example: negative age, empty name, invalid phone number.
 * Demonstrates custom exception handling in Java.java
 */
public class InvalidMemberException extends Exception {

    private final String fieldName;

    /**
     * Constructor with a descriptive error message.
     *
     * @param message Description of the validation failure.
     */
    public InvalidMemberException(String message) {
        super(message);
        this.fieldName = "unknown";
    }

    /**
     * Constructor with field name and error message.
     *
     * @param fieldName The field that failed validation.
     * @param message   Description of the validation failure.
     */
    public InvalidMemberException(String fieldName, String message) {
        super("Validation failed for [" + fieldName + "]: " + message);
        this.fieldName = fieldName;
    }

    /**
     * Returns the name of the field that caused the exception.
     */
    public String getFieldName() {
        return fieldName;
    }
}
