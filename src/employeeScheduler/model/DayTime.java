package employeeScheduler.model;

/**
 * Simple class with hour and minute.
 */
public class DayTime {
    private int hour;
    private int minute;

    DayTime(int hour, int minute) {
        if (hour > 23) {
            this.hour = 23;
        } else if (hour < 0) {
            this.hour = 0;
        } else {
            this.hour = hour;
        }

        if (minute > 59) {
            this.minute = 59;
        } else if (minute < 0) {
            this.minute = 0;
        } else {
            this.minute = minute;
        }
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        if (hour > 23) {
            this.hour = 23;
        } else if (hour < 0) {
            this.hour = 0;
        } else {
            this.hour = hour;
        }
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        if (minute > 59) {
            this.minute = 59;
        } else if (minute < 0) {
            this.minute = 0;
        } else {
            this.minute = minute;
        }
    }
}
