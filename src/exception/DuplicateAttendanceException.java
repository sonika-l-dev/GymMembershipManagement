package exception;

/**
 * Custom exception thrown when attendance is marked twice
 * for the same member on the same date.date.
 
 * Prevents duplicate attendance records in the system.
 */
public class DuplicateAttendanceException extends Exception {

    private final String memberId;
    private final String date;

    /**
     * Constructor with member ID and date.
     *
     * @param memberId The ID of the member with duplicate attendance.
     * @param date     The date on which the duplicate was detected.
     */
    public DuplicateAttendanceException(String memberId, String date) {
        super("Attendance already marked for member [" + memberId + "] on date [" + date + "].");
        this.memberId = memberId;
        this.date = date;
    }

    /**
     * Returns the member ID associated with the duplicate attendance.
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * Returns the date on which the duplicate was detected.
     */
    public String getDate() {
        return date;
    }
}
