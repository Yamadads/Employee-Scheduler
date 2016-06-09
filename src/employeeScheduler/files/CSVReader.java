package employeeScheduler.files;

import employeeScheduler.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class reading schedule model from specific csv file
 * this file need to have structure like in example scheduleModel.csv file in this repository
 */
public class CSVReader {
    public ScheduleModel getModelFromFile(String path) throws FileNotFoundException {
        ArrayList<ArrayList<String>> dataFromFile = readIntoStructure(path);
        //printFile(dataFromFile);
        return createModelFromStructure(dataFromFile);
    }

    private ScheduleModel createModelFromStructure(ArrayList<ArrayList<String>> dataFromFile) {
        Integer daysNumber = getDaysNumber(dataFromFile);
        ArrayList<Shift> shifts = getShifts(dataFromFile);
        ArrayList<EmployeeModel> employeePreferences = getEmployeePreferences(dataFromFile);

        ScheduleModel scheduleModel = new ScheduleModel(shifts, employeePreferences, daysNumber);
        return scheduleModel;
    }

    private ArrayList<EmployeeModel> getEmployeePreferences(ArrayList<ArrayList<String>> dataFromFile){
        ArrayList<EmployeeModel> employeePreferences = new ArrayList<>();
        Integer employeeLineNumber = 0;
        for (int i = 0; i < dataFromFile.size() - 1; i++) {
            if (dataFromFile.get(i).get(0).equals("employee")) {
                employeeLineNumber = i + 1;
                break;
            }
        }
        while (dataFromFile.get(employeeLineNumber).size()>8) {
            Integer employeeNumber = Integer.parseInt(dataFromFile.get(employeeLineNumber).get(0));
            Integer dailyWorkTime = Integer.parseInt(dataFromFile.get(employeeLineNumber).get(1));
            Integer minWorkingDays=Integer.parseInt(dataFromFile.get(employeeLineNumber).get(2));
            Integer maxWorkingDays = Integer.parseInt(dataFromFile.get(employeeLineNumber).get(3));
            Integer targetWorkingDays=Integer.parseInt(dataFromFile.get(employeeLineNumber).get(4));
            Integer minDayBreakTime=Integer.parseInt(dataFromFile.get(employeeLineNumber).get(5));
            Integer minWeekBreakTime=Integer.parseInt(dataFromFile.get(employeeLineNumber).get(6));
            Integer currentOvertime=Integer.parseInt(dataFromFile.get(employeeLineNumber).get(7));
            Double acceptanceLevel=Double.parseDouble(dataFromFile.get(employeeLineNumber).get(8));

            ArrayList<EmployeePreferences> preferences = new ArrayList<>();
            for (int i=9;i<dataFromFile.get(employeeLineNumber).size();i=i+3){
                Integer dayNumber = Integer.parseInt(dataFromFile.get(employeeLineNumber).get(i));
                Integer shiftNumber = Integer.parseInt(dataFromFile.get(employeeLineNumber).get(i+1));
                Preferences pref = Preferences.valueOf(dataFromFile.get(employeeLineNumber).get(i+2));
                EmployeePreferences preference = new EmployeePreferences(dayNumber,shiftNumber,pref);
                preferences.add(preference);
            }

            employeePreferences.add(employeeNumber,new EmployeeModel(preferences,dailyWorkTime,minWorkingDays,maxWorkingDays,targetWorkingDays,minDayBreakTime,minWeekBreakTime,currentOvertime,acceptanceLevel));
            employeeLineNumber++;
        }
        return employeePreferences;
    }

    private ArrayList<Shift> getShifts(ArrayList<ArrayList<String>> dataFromFile) {
        ArrayList<Shift> shifts = new ArrayList<>();

        Integer shiftsLineNumber = 0;
        for (int i = 0; i < dataFromFile.size() - 1; i++) {
            if (dataFromFile.get(i).get(0).equals("shift")) {
                shiftsLineNumber = i + 1;
                break;
            }
        }

        while (!dataFromFile.get(shiftsLineNumber).get(0).equals("employee")) {
            Integer shiftNumber = Integer.parseInt(dataFromFile.get(shiftsLineNumber).get(0));
            DayTime startTime = dayTimeFromString(dataFromFile.get(shiftsLineNumber).get(1));
            DayTime endTime = dayTimeFromString(dataFromFile.get(shiftsLineNumber).get(2));
            Integer minEmployees = Integer.parseInt(dataFromFile.get(shiftsLineNumber).get(3));
            Integer maxEmployees = Integer.parseInt(dataFromFile.get(shiftsLineNumber).get(4));
            shifts.add(shiftNumber, new Shift(startTime,endTime,minEmployees,maxEmployees));
            shiftsLineNumber++;
        }
        return shifts;
    }

    private Integer getDaysNumber(ArrayList<ArrayList<String>> dataFromFile) {
        Integer daysNumber = 0;
        for (int i = 0; i < dataFromFile.size() - 1; i++) {
            if (dataFromFile.get(i).get(0).equals("scheduleDaysNumber")) {
                daysNumber = Integer.parseInt(dataFromFile.get(i + 1).get(0));
                break;
            }
        }
        return daysNumber;
    }

    private DayTime dayTimeFromString(String time) {
        String[] parts = time.split(":");
        DayTime dayTime = new DayTime(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        return dayTime;
    }

    private ArrayList<ArrayList<String>> readIntoStructure(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        ArrayList<ArrayList<String>> dataFromFile = new ArrayList<>();
        dataFromFile.add(new ArrayList<>());
        final String DELIMITER = ",";

        while (scanner.hasNextLine())
        {
            String readedLine = scanner.nextLine();
            String[] tokens = readedLine.split(DELIMITER);
            for(String token : tokens)
            {
                dataFromFile.get(dataFromFile.size()-1).add(token);
            }
            dataFromFile.add(new ArrayList<>());
        }
        scanner.close();
        return dataFromFile;
    }

    private void printFile(ArrayList<ArrayList<String>> dataFromFile) {
        for (int i = 0; i < dataFromFile.size(); i++) {
            for (int j = 0; j < dataFromFile.get(i).size(); j++) {
                System.out.print(dataFromFile.get(i).get(j));
                System.out.print(" | ");
            }
            System.out.println();
        }
    }
}
