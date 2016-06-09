package employeeScheduler;

import employeeScheduler.builder.CPLEXBuilder;
import employeeScheduler.files.CSVReader;
import employeeScheduler.files.CSVWriter;
import employeeScheduler.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * lib test
 */
public class Main {

    public static void main(String[] args) {
        try {
            String fileIn="scheduleModel.csv";
            String fileOut="resultingSchedule.csv";
            if ((args.length > 0) && (args.length < 3)) {
                fileIn = args[0];
                fileOut = args[1];
            }
            CSVReader reader = new CSVReader();
            ScheduleModel scheduleModel = reader.getModelFromFile(fileIn);
            CPLEXBuilder builder = new CPLEXBuilder();
            builder.buildSchedule(scheduleModel);
            ResultingSchedule resultingSchedule = builder.getResultingSchedule();
            System.out.print(resultingSchedule.toString());
            CSVWriter writer = new CSVWriter();
            writer.writeResultingSchedule(resultingSchedule, fileOut);
            System.out.println("Press \"ENTER\" to continue...");
            System.in.read();
        } catch (java.io.IOException e) {
            System.out.println("Problem with input file. Press \"ENTER\" to continue...");
            try{
                System.in.read();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}