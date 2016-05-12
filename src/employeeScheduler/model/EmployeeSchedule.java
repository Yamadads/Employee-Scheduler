package employeeScheduler.model;

import java.util.HashMap;

/**
 * Object created by schedule builder. It contains structure of work shifts assigned to specific employee.
 */
public class EmployeeSchedule {
    private Integer employeeID;
    private HashMap<Integer,Integer> resultSchedule;
    private Double fulfilledPreferences;
    private Integer workTime;


    public Boolean isOnShift(Integer day, Integer shift){
        if (resultSchedule.containsKey(day)) {
            if (resultSchedule.get(day)==shift){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public EmployeeSchedule(Integer employeeID){
        this.employeeID = employeeID;
        resultSchedule = new HashMap<>();
    }

    public HashMap<Integer,Integer> getResult(){
        return resultSchedule;
    }

    public void addShift(Integer day, Integer shift){
        resultSchedule.put(day,shift);
    }

    Integer getEmployeeID(){
        return employeeID;
    }

    public Double getFulfilledPreferences() {
        return fulfilledPreferences;
    }

    public void setFulfilledPreferences(Double fulfilledPreferences) {
        this.fulfilledPreferences = fulfilledPreferences;
    }

    public Integer getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Integer overtime) {
        this.workTime = overtime;
    }
}
