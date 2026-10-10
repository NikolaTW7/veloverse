package com.veloverse.controller;

import java.math.BigDecimal;
import java.util.List;

import com.veloverse.model.Bicycle;

/**
 * One row of the comparison table: a specification label and the value for each bicycle.
 * Missing values are shown honestly as "Not specified" (or "Not confirmed" for the frame
 * material), and a row only counts as different when both values are known and not equal.
 *
 * <p>This is a plain Java class, not a database entity: CompareController creates the rows
 * and the compare.html template reads them through the getters.
 * The class is final (it cannot be extended) and all fields are final, so a row never changes once made.
 */
public final class CompareRow {

    // Texts shown in place of a missing value
    static final String NOT_SPECIFIED = "Not specified";
    static final String NOT_CONFIRMED = "Not confirmed";

    private final String label;
    private final String valueA;
    private final String valueB;
    // Remembers whether the original value was missing, because valueA/valueB then hold the "Not ..." text
    private final boolean missingA;
    private final boolean missingB;

    // A null value means "missing": it is recorded as missing and replaced by missingText for display
    CompareRow(String label, String valueA, String valueB, String missingText) {
        this.label = label;
        this.missingA = valueA == null;
        this.missingB = valueB == null;
        this.valueA = missingA ? missingText : valueA;
        this.valueB = missingB ? missingText : valueB;
    }

    /**
     * The specification rows for two bicycles, in the order they appear on the page.
     * List.of creates a fixed list that cannot be changed afterwards.
     * The bicycles must already have their brand, type and frame material loaded
     * (BicycleService.findById does this), because the database session is closed by now.
     */
    static List<CompareRow> forBicycles(Bicycle a, Bicycle b) {
        return List.of(
                new CompareRow("Brand", a.getBrand().getName(), b.getBrand().getName(), NOT_SPECIFIED),
                new CompareRow("Model", a.getName(), b.getName(), NOT_SPECIFIED),
                new CompareRow("Year", text(a.getYear()), text(b.getYear()), NOT_SPECIFIED),
                new CompareRow("Bicycle type", a.getType().getName(), b.getType().getName(), NOT_SPECIFIED),
                // The frame material may be null when it could not be confirmed
                new CompareRow("Frame material",
                        a.getFrameMaterial() == null ? null : a.getFrameMaterial().getName(),
                        b.getFrameMaterial() == null ? null : b.getFrameMaterial().getName(), NOT_CONFIRMED),
                new CompareRow("Weight", weight(a.getWeightKg()), weight(b.getWeightKg()), NOT_SPECIFIED),
                new CompareRow("Wheel size", blankToNull(a.getWheelSize()), blankToNull(b.getWheelSize()), NOT_SPECIFIED),
                new CompareRow("Brake system", blankToNull(a.getBrakeSystem()), blankToNull(b.getBrakeSystem()), NOT_SPECIFIED),
                new CompareRow("Drivetrain", blankToNull(a.getDrivetrain()), blankToNull(b.getDrivetrain()), NOT_SPECIFIED),
                new CompareRow("Number of gears", text(a.getGears()), text(b.getGears()), NOT_SPECIFIED));
    }

    // Number to text, keeping null as null (missing)
    private static String text(Integer number) {
        return number == null ? null : number.toString();
    }

    // 8.40 is shown as "8.4 kg"
    private static String weight(BigDecimal kg) {
        return kg == null ? null : kg.stripTrailingZeros().toPlainString() + " kg";
    }

    // An empty or blank text counts as missing, just like null
    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    // Getters used by the template, e.g. ${row.label} calls getLabel() and ${row.missingA} calls isMissingA()
    public String getLabel() {
        return label;
    }

    public String getValueA() {
        return valueA;
    }

    public String getValueB() {
        return valueB;
    }

    public boolean isMissingA() {
        return missingA;
    }

    public boolean isMissingB() {
        return missingB;
    }

    /** At least one bicycle has no value, so the two cannot be compared on this point. */
    public boolean isMissingInformation() {
        return missingA || missingB;
    }

    /** Both values are known and they are not the same (ignoring capitals and extra spaces). */
    public boolean isDifferent() {
        return !isMissingInformation() && !valueA.strip().equalsIgnoreCase(valueB.strip());
    }
}
