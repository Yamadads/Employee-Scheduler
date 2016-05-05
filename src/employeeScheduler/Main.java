package employeeScheduler;

import employeeScheduler.builder.CPLEXBuilder;
import employeeScheduler.model.*;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * lib test
 */
public class Main {

    public static void main(String[] args) {
        ArrayList<Shift> shifts = new ArrayList<>();
        shifts.add(0,new Shift(new DayTime(7,0), new DayTime(19,0), 1, 3));
        shifts.add(1,new Shift(new DayTime(19,0),new DayTime(7,0), 1, 3));

        ArrayList<EmployeePreferences> preferences = new ArrayList<>();
        //preferences.add(0,new EmployeePreferences())

        ArrayList<EmployeeModel> employeePreferences = new ArrayList<>();

        employeePreferences.add(0,new EmployeeModel(preferences,480,5,9, 6, 660,2100,0, AcceptanceLevel.MEDIUM));
        employeePreferences.add(1,new EmployeeModel(preferences,480,5,9, 7, 660,2100,0, AcceptanceLevel.MEDIUM));
        employeePreferences.add(2,new EmployeeModel(preferences,480,5,9, 8, 660,2100,-10, AcceptanceLevel.MEDIUM));
        employeePreferences.add(3,new EmployeeModel(preferences,480,5,9, 6, 660,2100,0, AcceptanceLevel.MEDIUM));
        ScheduleModel scheduleModel = new ScheduleModel(shifts, employeePreferences, DayOfWeek.MONDAY, 10);
        CPLEXBuilder builder = new CPLEXBuilder();
        builder.buildSchedule(scheduleModel);
        System.out.print(builder.getResultingSchedule().toString());
    }
}