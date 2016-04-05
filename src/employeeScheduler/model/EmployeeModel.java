package employeeScheduler.model;

import java.util.ArrayList;

/**
 * Model representing the characteristics and preferences of the employee.
 */
public class EmployeeModel {
    ArrayList<EmployeePreferences> preferences;
    Integer dailyWorkTime;
    Integer minWorkingDaysNumber;
    Integer maxWorkingDaysNumber;
    Integer minDayBreakTime;
    Integer minWeekBreakTime;
}
