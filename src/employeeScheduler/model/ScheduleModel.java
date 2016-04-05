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

}
