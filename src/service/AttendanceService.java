package service;

import exception.DuplicateAttendanceException;
import exception.MemberNotFoundException;
import model.Member;
import util.ConsoleUtil;
import util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Service class managing member attendance.
 *.
 * COLLECTIONS USED:
 *   - HashMap<String, List<String>> : maps memberId -> list of attendance dates.
 *   - HashSet<String>               : tracks "memberId|date" keys to detect duplicates.
 */
public class AttendanceService {

    /**
     * Attendance records: memberId -> list of dates attended.
     * (HashMap requirement)
     */
    private final Map<String, List<String>> attendanceRecords = new HashMap<>();

    /**
     * Set of composite keys "memberId|date" used to detect duplicate attendance.
     * (HashSet requirement for duplicate checking)
     */
    private final Set<String> attendanceSet = new HashSet<>();

    /**
     * Marks today's attendance for a member.
     * Throws DuplicateAttendanceException if attendance was already marked today.
     *
     * @param scanner       Console scanner.
     * @param memberService Reference to MemberService for ID validation.
     */
    public void markAttendance(Scanner scanner, MemberService memberService) {
        ConsoleUtil.printHeader("MARK ATTENDANCE");
        System.out.print("  Enter Member ID: ");
        String memberId = scanner.nextLine().trim().toUpperCase();

        try {
            // Validate that the member exists
            Member member = memberService.findMemberById(memberId);

            // Use today's date for attendance
            String date = DateUtil.today();
            String key  = memberId + "|" + date;

            // Check for duplicate using HashSet
            if (attendanceSet.contains(key)) {
                throw new DuplicateAttendanceException(memberId, date);
            }

            // Record attendance
            attendanceSet.add(key);
            attendanceRecords.computeIfAbsent(memberId, k -> new ArrayList<>()).add(date);

            ConsoleUtil.printSuccess("Attendance marked for " + member.getName()
                    + " on " + date + ".");

        } catch (MemberNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (DuplicateAttendanceException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    /**
     * Displays attendance records for all members or a specific member.
     *
     * @param scanner       Console scanner.
     * @param memberService Reference to MemberService.
     */
    public void viewAttendance(Scanner scanner, MemberService memberService) {
        ConsoleUtil.printHeader("ATTENDANCE RECORDS");
        System.out.print("  Enter Member ID (or press Enter to view all): ");
        String input = scanner.nextLine().trim().toUpperCase();

        if (input.isEmpty()) {
            // Show all attendance records
            if (attendanceRecords.isEmpty()) {
                ConsoleUtil.printInfo("No attendance records found.");
                return;
            }
            for (Map.Entry<String, List<String>> entry : attendanceRecords.entrySet()) {
                printMemberAttendance(entry.getKey(), entry.getValue(), memberService);
            }
        } else {
            // Show specific member
            List<String> dates = attendanceRecords.get(input);
            if (dates == null || dates.isEmpty()) {
                ConsoleUtil.printInfo("No attendance records for member: " + input);
            } else {
                printMemberAttendance(input, dates, memberService);
            }
        }
    }

    /**
     * Helper to print attendance dates for a single member.
     */
    private void printMemberAttendance(String memberId,
                                       List<String> dates,
                                       MemberService memberService) {
        String name = memberId;
        try {
            name = memberService.findMemberById(memberId).getName();
        } catch (MemberNotFoundException ignored) {}

        System.out.printf("%n  Member: %-8s | %s%n", memberId, name);
        System.out.printf("  Total Days Present: %d%n", dates.size());
        for (String d : dates) {
            System.out.println("    -> " + d);
        }
    }

    /**
     * Returns the full attendance records map.
     * Used by ReportService for the attendance summary.
     */
    public Map<String, List<String>> getAttendanceRecords() {
        return attendanceRecords;
    }
}
