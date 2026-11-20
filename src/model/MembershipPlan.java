package model;

/**
 * Represents a Gym Membership Plan.
 *
 * ENCAPSULATION: All fields are private with public getters.
 *.
 * Four plan types are pre-defined as static constants for easy reuse
 * across the application via MembershipPlan.PLANS map.
 */
public class MembershipPlan {

    private final String planName;
    private final int    durationMonths;
    private final double fee;

    /**
     * Constructor for a membership plan.
     *
     * @param planName       Name of the plan (e.g., "Monthly").
     * @param durationMonths Duration in months.
     * @param fee            Cost of the plan in INR.
     */
    public MembershipPlan(String planName, int durationMonths, double fee) {
        this.planName       = planName;
        this.durationMonths = durationMonths;
        this.fee            = fee;
    }

    // ─── Getters ───────────────────────────────────────────────────────────

    public String getPlanName()       { return planName;       }
    public int    getDurationMonths() { return durationMonths; }
    public double getFee()            { return fee;            }

    @Override
    public String toString() {
        return String.format("%-15s | Duration: %2d month(s) | Fee: Rs. %.2f",
                planName, durationMonths, fee);
    }
}
