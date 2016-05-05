package employeeScheduler.model;

/**
 * Simple class with hour and minute.
 */
public class DayTime {
    private int hour;
    private int minute;

    public DayTime(int hour, int minute) {
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

    public int getDifferenceInMinutes(DayTime startTime) {
        int endMinutes = hour*60+minute;
        int startMinutes= startTime.hour*60+startTime.minute;

        if (endMinutes>startMinutes){
            return endMinutes-startMinutes;
        }
        if (startMinutes>endMinutes){
            return 1440-startMinutes+endMinutes;
        }
        return 0;
    }
}
