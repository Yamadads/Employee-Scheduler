package employeeScheduler.model;

import java.util.ArrayList;

/**
 * Model representing the characteristics and preferences of the employee.
 */
public class EmployeeModel {
    private ArrayList<EmployeePreferences> preferences;
    private Integer dailyWorkTime;
    private Integer minWorkingDaysNumber;
    private Integer maxWorkingDaysNumber;
    private Integer minDayBreakTime;
    private Integer minWeekBreakTime;

    public ArrayList<EmployeePreferences> getPreferences() {
        return preferences;
    }

    public void setPreferences(ArrayList<EmployeePreferences> preferences) {
        this.preferences = preferences;
    }

    public Integer getDailyWorkTime() {
        return dailyWorkTime;
    }

    public void setDailyWorkTime(Integer dailyWorkTime) {
        this.dailyWorkTime = dailyWorkTime;
    }

    public Integer getMinWorkingDaysNumber() {
        return minWorkingDaysNumber;
    }

    public void setMinWorkingDaysNumber(Integer minWorkingDaysNumber) {
        this.minWorkingDaysNumber = minWorkingDaysNumber;
    }

    public Integer getMaxWorkingDaysNumber() {
        return maxWorkingDaysNumber;
    }

    public void setMaxWorkingDaysNumber(Integer maxWorkingDaysNumber) {
        this.maxWorkingDaysNumber = maxWorkingDaysNumber;
    }

    public Integer getMinDayBreakTime() {
        return minDayBreakTime;
    }

    public void setMinDayBreakTime(Integer minDayBreakTime) {
        this.minDayBreakTime = minDayBreakTime;
    }

    public Integer getMinWeekBreakTime() {
        return minWeekBreakTime;
    }

    public void setMinWeekBreakTime(Integer minWeekBreakTime) {
        this.minWeekBreakTime = minWeekBreakTime;
    }
}
