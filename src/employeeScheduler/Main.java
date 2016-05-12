package employeeScheduler;

import employeeScheduler.builder.CPLEXBuilder;
import employeeScheduler.files.CSVReader;
import employeeScheduler.files.CSVWriter;
import employeeScheduler.model.*;

import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * lib test
 */
public class Main {

    public static void main(String[] args) {
        try {
            CSVReader reader = new CSVReader();
            ScheduleModel scheduleModel = reader.getModelFromFile("scheduleModel.csv");
            CPLEXBuilder builder = new CPLEXBuilder();
            builder.buildSchedule(scheduleModel);
            ResultingSchedule resultingSchedule = builder.getResultingSchedule();
            System.out.print(resultingSchedule.toString());
            CSVWriter writer = new CSVWriter();
            writer.writeResultingSchedule(resultingSchedule,"resultingSchedule.csv");

        }catch (java.io.IOException e){

        }
    }
}