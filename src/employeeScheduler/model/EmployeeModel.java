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
    private Integer targetWorkingDaysNumber;
    private Integer minDayBreakTime;
    private Integer minWeekBreakTime;
    private Integer currentOvertime;
    private Double preferencesAcceptanceLevel;

    public EmployeeModel(ArrayList<EmployeePreferences> preferences, Integer dailyWorkTime, Integer minWorkingDaysNumber, Integer maxWorkingDaysNumber, Integer targetWorkingDaysNumber, Integer minDayBreakTime, Integer minWeekBreakTime, Integer currentOvertime, Double preferencesAcceptanceLevel) {
        this.preferences = preferences;
        this.dailyWorkTime = dailyWorkTime;
        this.minWorkingDaysNumber = minWorkingDaysNumber;
        this.maxWorkingDaysNumber = maxWorkingDaysNumber;
        this.targetWorkingDaysNumber = targetWorkingDaysNumber;
        this.minDayBreakTime = minDayBreakTime;
        this.minWeekBreakTime = minWeekBreakTime;
        this.currentOvertime = currentOvertime;
        this.preferencesAcceptanceLevel = preferencesAcceptanceLevel;
    }

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

    public Integer getCurrentOvertime() {
        return currentOvertime;
    }

    public void setCurrentOvertime(Integer currentOvertime) {
        this.currentOvertime = currentOvertime;
    }

    public Integer getTargetWorkingDaysNumber() {
        return targetWorkingDaysNumber;
    }

    public void setTargetWorkingDaysNumber(Integer targetWorkingDaysNumber) {
        this.targetWorkingDaysNumber = targetWorkingDaysNumber;
    }

    public Double getPreferencesAcceptanceLevel() {
        return preferencesAcceptanceLevel;
    }

    public void setPreferencesAcceptanceLevel(Double preferencesAcceptanceLevel) {
        this.preferencesAcceptanceLevel = preferencesAcceptanceLevel;
    }
}
