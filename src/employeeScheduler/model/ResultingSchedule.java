package employeeScheduler.model;

import java.util.ArrayList;

/**
 * Object created by schedule builder. It contains structure of defined work shifts combined with employees.
 */
public class ResultingSchedule {
    private ArrayList<Integer>[][] result;
    private ArrayList<EmployeeSchedule> employeesSchedules;
    private Integer days;
    private Integer shifts;
    private ArrayList<Integer> maxEmployeesPerShift;

    public ResultingSchedule(Integer days, Integer shifts, Integer employeesNumber) {
        this.setDays(days);
        this.setShifts(shifts);
        maxEmployeesPerShift = new ArrayList<>();
        employeesSchedules = new ArrayList<>();
        for (int i=0;i<employeesNumber;i++){
            getEmployeesSchedules().add(new EmployeeSchedule(i));
        }

        result = new ArrayList[days][shifts];
        for (int i = 0; i < days; i++)
            for (int j = 0; j < shifts; j++) {
                result[i][j] = new ArrayList<>();
            }
    }

    public void addShiftMaxEmployee(Integer shiftNumber, Integer maxEmployees){
        maxEmployeesPerShift.add(shiftNumber,maxEmployees);
    }

    public Integer getMaxEmployeesPerShift(Integer shiftNumber){
        return maxEmployeesPerShift.get(shiftNumber);
    }

    public String toString() {
        String stringSchedule = "";
        if (getDays() ==0){
            return "No schedule";
        }
        for (int i = 0; i < getShifts(); i++){
            for (int j = 0; j < getDays(); j++) {
                stringSchedule += result[j][i].toString();
            }
            stringSchedule += "\n";
        }
        return stringSchedule;
    }

    public ArrayList<Integer> getEmployeeListOnShift(Integer day, Integer shift) {
        return result[day][shift];
    }

    public ArrayList<Integer>[][] getResult() {
        return result;
    }

    public void addEmployee(Integer day, Integer shift, Integer employeeID) {
        getEmployeesSchedules().get(employeeID).addShift(day,shift);
        result[day][shift].add(employeeID);
    }

    public ArrayList<EmployeeSchedule> getEmployeesSchedules() {
        return employeesSchedules;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getShifts() {
        return shifts;
    }

    public void setShifts(Integer shifts) {
        this.shifts = shifts;
    }
}
