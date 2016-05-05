package employeeScheduler.model;

import java.util.ArrayList;

/**
 * Object created by schedule builder. It contains structure of defined work shifts combined with employees.
 */
public class ResultingSchedule {
    private ArrayList<Integer>[][] result;
    private Integer days;
    private Integer shifts;

    public ResultingSchedule(Integer days, Integer shifts) {
        this.days = days;
        this.shifts = shifts;

        result = new ArrayList[days][shifts];
        for (int i = 0; i < days; i++)
            for (int j = 0; j < shifts; j++) {
                result[i][j] = new ArrayList<>();
            }
    }

    public String toString() {
        String stringSchedule = "";
        for (int i = 0; i < shifts; i++){
            for (int j = 0; j < days; j++) {
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
        result[day][shift].add(employeeID);
    }

}
