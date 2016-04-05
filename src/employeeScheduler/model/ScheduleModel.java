package employeeScheduler.model;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * Model representing the characteristics of schedule.
 */
public class ScheduleModel {
    private ArrayList<Shift> shifts;
    private ArrayList<EmployeeModel> employeePreferences;
    private DayOfWeek firstDayOfPeriod;
    private Integer scheduleDaysNumber;

    public ScheduleModel(ArrayList<Shift> shifts, ArrayList<EmployeeModel> employeePreferences, DayOfWeek firstDayOfPeriod, Integer scheduleDaysNumber) {
        this.shifts = shifts;
        this.employeePreferences = employeePreferences;
        this.firstDayOfPeriod = firstDayOfPeriod;
        this.scheduleDaysNumber = scheduleDaysNumber;
    }

    public ArrayList<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(ArrayList<Shift> shifts) {
        this.shifts = shifts;
    }

    public ArrayList<EmployeeModel> getEmployeePreferences() {
        return employeePreferences;
    }

    public void setEmployeePreferences(ArrayList<EmployeeModel> employeePreferences) {
        this.employeePreferences = employeePreferences;
    }

    public DayOfWeek getFirstDayOfPeriod() {
        return firstDayOfPeriod;
    }

    public void setFirstDayOfPeriod(DayOfWeek firstDayOfPeriod) {
        this.firstDayOfPeriod = firstDayOfPeriod;
    }

    public Integer getScheduleDaysNumber() {
        return scheduleDaysNumber;
    }

    public void setScheduleDaysNumber(Integer scheduleDaysNumber) {
        this.scheduleDaysNumber = scheduleDaysNumber;
    }
}
