package employeeScheduler.model;

/**
 * Employee preferences about specific work shift
 */

public class EmployeePreferences{
    private Integer dayNumber;
    private Integer shiftNumber;
    private Preferences preference;

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Integer getShiftNumber() {
        return shiftNumber;
    }

    public void setShiftNumber(Integer shiftNumber) {
        this.shiftNumber = shiftNumber;
    }

    public Preferences getPreference() {
        return preference;
    }

    public void setPreference(Preferences preference) {
        this.preference = preference;
    }
}