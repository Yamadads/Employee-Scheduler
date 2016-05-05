package employeeScheduler.model;

/**
 * Simple class with information about specific work shift.
 */
public class Shift {
    private DayTime startTime;
    private DayTime endTime;
    private Integer minEmployeesNumber;
    private Integer maxEmployeesNumber;

    public Shift(DayTime startTime, DayTime endTime, Integer minEmployeesNumber, Integer maxEmployeesNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.minEmployeesNumber = minEmployeesNumber;
        this.maxEmployeesNumber = maxEmployeesNumber;
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

    public Integer getMinEmployeesNumber() {
        return minEmployeesNumber;
    }

    public void setMinEmployeesNumber(Integer minEmployeesNumber) {
        this.minEmployeesNumber = minEmployeesNumber;
    }

    public Integer getMaxEmployeesNumber() {
        return maxEmployeesNumber;
    }

    public void setMaxEmployeesNumber(Integer maxEmployeesNumber) {
        this.maxEmployeesNumber = maxEmployeesNumber;
    }

    public Integer getShiftTime(){
        return endTime.getDifferenceInMinutes(startTime);
    }
}
