package employeeScheduler.model;

import java.util.ArrayList;
/**
 * Object created by schedule builder. It contains structure of defined work shifts combined with employees.
 */
public class ResultingSchedule {
    private ArrayList<Integer>[][] result;

    ResultingSchedule(Integer days, Integer shifts, Integer maxEmployees){
        result = new ArrayList[days][shifts];
        for (int i=0;i<days;i++)
            for (int j=0;j<shifts;j++){
                result[i][j] = new ArrayList<>();
            }
    }

    ArrayList<Integer> getEmployeeListOnShift(Integer day, Integer shift){
        return result[day][shift];
    }

    ArrayList<Integer>[][] getResult(){
        return result;
    }

    void addEmployee(Integer day, Integer shift, Integer employeeID){
        result[day][shift].add(employeeID);
    }

}
