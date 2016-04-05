package employeeScheduler.model;

/**
 * Simple class with information about specific work shift.
 */
public class Shift {
    private DayTime startTime;
    private DayTime endTime;
    private Integer requiredEmployeesNumber;

    public Shift(DayTime startTime, DayTime endTime, Integer requiredEmployeesNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.requiredEmployeesNumber = requiredEmployeesNumber;
    }

    public Integer getRequiredEmployeesNumber() {
        return requiredEmployeesNumber;
    }

    public void setRequiredEmployeesNumber(Integer requiredEmployeesNumber) {
        this.requiredEmployeesNumber = requiredEmployeesNumber;
    }

    public DayTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DayTime startTime) {
        this.startTime = startTime;
    }

    public DayTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DayTime endTime) {
        this.endTime = endTime;
    }

}
