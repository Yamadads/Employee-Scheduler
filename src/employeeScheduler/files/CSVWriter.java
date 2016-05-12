package employeeScheduler.files;

import employeeScheduler.model.ResultingSchedule;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Class writing resultant schedule into csv file
 */
public class CSVWriter {
    public void writeResultingSchedule(ResultingSchedule resultingSchedule, String path) throws IOException {
        try
        {
            FileWriter writer = new FileWriter(path);

            //headers
            writer.append("shift");
            writer.append(';');
            for (int i=0;i<resultingSchedule.getDays();i++){
                writer.append(Integer.toString(i));
                writer.append(";");
            }
            writer.append("\n");

            //employees on shifts
            for (int shift=0;shift<resultingSchedule.getShifts();shift++){
                Integer maxEmployee = resultingSchedule.getMaxEmployeesPerShift(shift);
                for (int i=0;i<maxEmployee;i++){
                    writer.append(Integer.toString(shift));
                    writer.append(";");
                    for (int day=0;day<resultingSchedule.getDays();day++){
                        if (resultingSchedule.getEmployeeListOnShift(day,shift).size()>=i+1){
                            writer.append(Integer.toString(resultingSchedule.getEmployeeListOnShift(day,shift).get(i).intValue()));
                        }
                        writer.append(";");
                    }
                    writer.append('\n');
                }
            }
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
