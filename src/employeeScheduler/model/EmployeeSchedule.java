package employeeScheduler.model;

import java.util.HashMap;

/**
 * Object created by schedule builder. It contains structure of work shifts assigned to specific employee.
 */
public class EmployeeSchedule {
    Integer employeeID;
    private HashMap<Integer,Integer> resultSchedule;

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
}
