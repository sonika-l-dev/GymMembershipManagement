package service;

import model.Member;
import model.Trainer;
import util.ConsoleUtil;

import java.util.List;
import java.util.Map;

/**
 * Service class that generates various system reports...
 * Aggregates data from MemberService, AttendanceService, and TrainerService.
 */
public class ReportService {

    private final MemberService     memberService;
    private final AttendanceService attendanceService;
    private final TrainerService    trainerService;

    /**
     * Constructor: injects the three service dependencies.
     */
    public ReportService(MemberService memberService,
                         AttendanceService attendanceService,
                         TrainerService trainerService) {
        this.memberService     = memberService;
        this.attendanceService = attendanceService;
        this.trainerService    = trainerService;
    }

    /**
     * Generates and prints the full system report:
     *   1. Total / Active / Expired members
     *   2. Attendance summary
     *   3. Trainer assignment summary
     */
    public void generateReport() {
        ConsoleUtil.printTitle("===== SYSTEM REPORT =====");

        reportMemberStats();
        reportAttendanceSummary();
        reportTrainerSummary();

        ConsoleUtil.printBorder();
    }

    // ─── 1. Member Statistics ──────────────────────────────────────────────

    private void reportMemberStats() {
        List<Member> allMembers = memberService.getAllMembers();
        long active  = allMembers.stream().filter(Member::isActive).count();
        long expired = allMembers.size() - active;

        ConsoleUtil.printHeader("MEMBER STATISTICS");
        System.out.printf("  Total Members   : %d%n", allMembers.size());
        System.out.printf("  Active Members  : %d%n", active);
        System.out.printf("  Expired Members : %d%n", expired);

        if (!allMembers.isEmpty()) {
            System.out.println();
            System.out.println("  Active Members:");
            allMembers.stream()
                    .filter(Member::isActive)
                    .forEach(m -> System.out.printf("    -> %-8s | %s | Expiry: %s%n",
                            m.getMemberId(), m.getName(), m.getExpiryDate()));

            System.out.println();
            System.out.println("  Expired Members:");
            long expiredCount = allMembers.stream()
                    .filter(m -> !m.isActive())
                    .peek(m -> System.out.printf("    -> %-8s | %s | Expired: %s%n",
                            m.getMemberId(), m.getName(), m.getExpiryDate()))
                    .count();
            if (expiredCount == 0) {
                System.out.println("    (None)");
            }
        }
    }

    // ─── 2. Attendance Summary ─────────────────────────────────────────────

    private void reportAttendanceSummary() {
        Map<String, List<String>> records = attendanceService.getAttendanceRecords();

        ConsoleUtil.printHeader("ATTENDANCE SUMMARY");
        if (records.isEmpty()) {
            ConsoleUtil.printInfo("No attendance records available.");
            return;
        }

        int totalEntries = 0;
        for (Map.Entry<String, List<String>> entry : records.entrySet()) {
            String memberName = entry.getKey();
            try {
                memberName = memberService.findMemberById(entry.getKey()).getName();
            } catch (exception.MemberNotFoundException ignored) {}

            int days = entry.getValue().size();
            totalEntries += days;
            System.out.printf("  %-8s | %-20s | Days Present: %d%n",
                    entry.getKey(), memberName, days);
        }
        System.out.printf("%n  Total Attendance Entries: %d%n", totalEntries);
    }

    // ─── 3. Trainer Assignment Summary ─────────────────────────────────────

    private void reportTrainerSummary() {
        Map<String, Trainer>       allTrainers   = trainerService.getAllTrainers();
        Map<String, List<String>>  assignments   = trainerService.getTrainerAssignments();

        ConsoleUtil.printHeader("TRAINER ASSIGNMENT SUMMARY");
        if (allTrainers.isEmpty()) {
            ConsoleUtil.printInfo("No trainers registered.");
            return;
        }

        for (Map.Entry<String, Trainer> entry : allTrainers.entrySet()) {
            String    tid     = entry.getKey();
            Trainer   trainer = entry.getValue();
            List<String> mems = assignments.getOrDefault(tid, List.of());
            System.out.printf("  %-6s | %-20s | Members Assigned: %d%n",
                    tid, trainer.getName(), mems.size());
        }

        System.out.printf("%n  Total Trainers  : %d%n", allTrainers.size());
        System.out.printf("  Waiting List    : %d member(s)%n",
                trainerService.getWaitingList().size());
    }
}
