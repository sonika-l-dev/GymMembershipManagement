package service;

import exception.InvalidMemberException;
import exception.MemberNotFoundException;
import model.Member;
import model.MembershipPlan;
import util.ConsoleUtil;
import util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Service class managing all member-related operations..
 *
 * COLLECTIONS USED:
 *   - ArrayList<Member>              : stores all registered members.
 *   - HashMap<String, MembershipPlan>: maps plan code -> MembershipPlan object.
 */
public class MemberService {

    // ─── Collections ──────────────────────────────────────────────────────

    /** Stores all registered members. (ArrayList requirement) */
    private final List<Member> members = new ArrayList<>();

    /**
     * Pre-defined membership plans stored in a HashMap.
     * Key: plan code ("1","2","3","4")
     * Value: MembershipPlan object.
     * (HashMap requirement)
     */
    private final Map<String, MembershipPlan> plans = new HashMap<>();

    private int memberCounter = 1; // Counter for auto-generating member IDs

    /**
     * Constructor: initialises the four pre-defined membership plans.
     */
    public MemberService() {
        plans.put("1", new MembershipPlan("Monthly",    1,  999.00));
        plans.put("2", new MembershipPlan("Quarterly",  3, 2499.00));
        plans.put("3", new MembershipPlan("Half-Yearly",6, 4499.00));
        plans.put("4", new MembershipPlan("Yearly",    12, 7999.00));
    }

    // ─── Getters for use by other services ────────────────────────────────

    public List<Member> getAllMembers()              { return members; }
    public Map<String, MembershipPlan> getAllPlans() { return plans;   }

    // ─── Auto-generate unique member ID ───────────────────────────────────

    /**
     * Generates the next unique member ID in the format GYM001, GYM002 …
     */
    private String generateMemberId() {
        return String.format("GYM%03d", memberCounter++);
    }

    // ─── Feature 1: Member Registration ───────────────────────────────────

    /**
     * Interactively registers a new gym member.
     * Validates all inputs; throws InvalidMemberException on bad data.
     *
     * @param scanner Console input scanner.
     */
    public void registerMember(Scanner scanner) {
        ConsoleUtil.printHeader("REGISTER NEW MEMBER");
        try {
            // --- Name ---
            System.out.print("  Enter name           : ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                throw new InvalidMemberException("name", "Name cannot be empty.");
            }

            // --- Age ---
            System.out.print("  Enter age            : ");
            String ageInput = scanner.nextLine().trim();
            int age;
            try {
                age = Integer.parseInt(ageInput);
            } catch (NumberFormatException e) {
                throw new InvalidMemberException("age", "Age must be a valid number.");
            }
            if (age < 15 || age > 90) {
                throw new InvalidMemberException("age", "Age must be between 15 and 90.");
            }

            // --- Phone ---
            System.out.print("  Enter phone (10 digits): ");
            String phone = scanner.nextLine().trim();
            if (!DateUtil.isValidPhone(phone)) {
                throw new InvalidMemberException("phone", "Phone must be exactly 10 digits.");
            }

            // --- Joining Date ---
            System.out.print("  Enter joining date (yyyy-MM-dd) [leave blank for today]: ");
            String joiningDate = scanner.nextLine().trim();
            if (joiningDate.isEmpty()) {
                joiningDate = DateUtil.today();
            } else if (!DateUtil.isValidDate(joiningDate)) {
                throw new InvalidMemberException("joiningDate", "Date must be in yyyy-MM-dd format.");
            }

            // --- Plan Selection ---
            displayPlans();
            System.out.print("  Select plan (1-4)    : ");
            String planChoice = scanner.nextLine().trim();
            MembershipPlan selectedPlan = plans.get(planChoice);
            if (selectedPlan == null) {
                throw new InvalidMemberException("plan", "Invalid plan selection. Choose 1-4.");
            }

            // --- Build Member ---
            String memberId   = generateMemberId();
            String expiryDate = DateUtil.addMonths(joiningDate, selectedPlan.getDurationMonths());

            Member member = new Member(memberId, name, age, phone,
                                       joiningDate, selectedPlan.getPlanName(), expiryDate);
            members.add(member);

            ConsoleUtil.printSuccess("Member registered successfully!");
            System.out.printf("  Member ID  : %s%n", memberId);
            System.out.printf("  Expiry Date: %s%n", expiryDate);
            System.out.printf("  Plan       : %s%n", selectedPlan.getPlanName());
            System.out.printf("  Fee Paid   : Rs. %.2f%n", selectedPlan.getFee());

        } catch (InvalidMemberException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    // ─── Feature 2: Display All Members ───────────────────────────────────

    /**
     * Displays all registered members in a formatted table.
     */
    public void displayAllMembers() {
        ConsoleUtil.printHeader("ALL MEMBERS");
        if (members.isEmpty()) {
            ConsoleUtil.printInfo("No members registered yet.");
            return;
        }
        System.out.printf("  %-8s | %-20s | %3s | %-12s | %-12s | %-10s | %-10s | %-7s%n",
                "ID", "Name", "Age", "Phone", "Plan", "Joined", "Expiry", "Status");
        ConsoleUtil.printLine();
        for (Member m : members) {
            System.out.printf("  %-8s | %-20s | %3d | %-12s | %-12s | %-10s | %-10s | %-7s%n",
                    m.getMemberId(), m.getName(), m.getAge(), m.getPhone(),
                    m.getPlanName(), m.getJoiningDate(), m.getExpiryDate(),
                    m.isActive() ? "ACTIVE" : "EXPIRED");
        }
        System.out.printf("%n  Total members: %d%n", members.size());
    }

    // ─── Feature 3: Display Plans ──────────────────────────────────────────

    /**
     * Displays all available membership plans.
     */
    public void displayPlans() {
        ConsoleUtil.printHeader("AVAILABLE MEMBERSHIP PLANS");
        for (Map.Entry<String, MembershipPlan> entry : plans.entrySet()) {
            System.out.printf("  [%s] %s%n", entry.getKey(), entry.getValue());
        }
    }

    // ─── Feature 4: Membership Renewal ────────────────────────────────────

    /**
     * Renews a membership for an existing member.
     * Finds the member by ID, then allows plan re-selection and updates expiry.
     *
     * @param scanner Console input scanner.
     */
    public void renewMembership(Scanner scanner) {
        ConsoleUtil.printHeader("RENEW MEMBERSHIP");
        System.out.print("  Enter Member ID to renew: ");
        String memberId = scanner.nextLine().trim().toUpperCase();
        try {
            Member member = findMemberById(memberId);
            System.out.printf("  Found: %s (Current Expiry: %s)%n",
                    member.getName(), member.getExpiryDate());

            displayPlans();
            System.out.print("  Select new plan (1-4): ");
            String planChoice = scanner.nextLine().trim();
            MembershipPlan selectedPlan = plans.get(planChoice);
            if (selectedPlan == null) {
                ConsoleUtil.printError("Invalid plan selection.");
                return;
            }

            // Renewal extends from today or from current expiry (whichever is later)
            String base = member.isActive() ? member.getExpiryDate() : DateUtil.today();
            String newExpiry = DateUtil.addMonths(base, selectedPlan.getDurationMonths());

            member.setExpiryDate(newExpiry);
            member.setPlanName(selectedPlan.getPlanName());

            ConsoleUtil.printSuccess("Membership renewed successfully!");
            System.out.printf("  New Plan   : %s%n", selectedPlan.getPlanName());
            System.out.printf("  New Expiry : %s%n", newExpiry);
            System.out.printf("  Fee Paid   : Rs. %.2f%n", selectedPlan.getFee());

        } catch (MemberNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    // ─── Feature 6: Search Member ──────────────────────────────────────────

    /**
     * Searches for a member by ID and displays their details.
     *
     * @param scanner Console input scanner.
     */
    public void searchMember(Scanner scanner) {
        ConsoleUtil.printHeader("SEARCH MEMBER");
        System.out.print("  Enter Member ID: ");
        String memberId = scanner.nextLine().trim().toUpperCase();
        try {
            Member member = findMemberById(memberId);
            System.out.println();
            System.out.println("  " + member);
        } catch (MemberNotFoundException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    // ─── Internal helper ──────────────────────────────────────────────────

    /**
     * Finds a member by their unique ID.
     *
     * @param memberId The ID to search for.
     * @return The matching Member object.
     * @throws MemberNotFoundException if no member with that ID exists.
     */
    public Member findMemberById(String memberId) throws MemberNotFoundException {
        for (Member m : members) {
            if (m.getMemberId().equalsIgnoreCase(memberId)) {
                return m;
            }
        }
        throw new MemberNotFoundException(memberId);
    }
}
