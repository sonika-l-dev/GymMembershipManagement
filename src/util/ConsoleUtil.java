package util;

/**
 * Utility class for printing formatted console banners and separators.
 * Keeps the UI code clean and consistent across all service classes..
 */
public final class ConsoleUtil {

    private static final int   WIDTH     = 70;
    private static final char  BORDER    = '=';
    private static final char  LINE      = '-';

    /** Private constructor to prevent instantiation. */
    private ConsoleUtil() {}

    /**
     * Prints a full-width border line using '=' characters.
     */
    public static void printBorder() {
        System.out.println("=".repeat(WIDTH));
    }

    /**
     * Prints a full-width separator line using '-' characters.
     */
    public static void printLine() {
        System.out.println("-".repeat(WIDTH));
    }

    /**
     * Prints a centred title inside a bordered box.
     *
     * @param title The title text to display.
     */
    public static void printTitle(String title) {
        printBorder();
        int padding = (WIDTH - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(0, padding)) + title);
        printBorder();
    }

    /**
     * Prints a section header with dashes.
     *
     * @param header The header text.
     */
    public static void printHeader(String header) {
        System.out.println();
        printLine();
        System.out.println("  " + header);
        printLine();
    }

    /**
     * Prints a success message prefixed with [OK].
     *
     * @param message The success message.
     */
    public static void printSuccess(String message) {
        System.out.println("[OK]  " + message);
    }

    /**
     * Prints an error message prefixed with [ERR].
     *
     * @param message The error message.
     */
    public static void printError(String message) {
        System.out.println("[ERR] " + message);
    }

    /**
     * Prints an informational message prefixed with [i].
     *
     * @param message The info message.
     */
    public static void printInfo(String message) {
        System.out.println("[i]   " + message);
    }
}
