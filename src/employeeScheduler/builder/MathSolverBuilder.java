package employeeScheduler.builder;

import employeeScheduler.model.*;
import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.search.*;

import java.util.ArrayList;

/**
 * Builder uses an open source math solver to create the final work schedule.
 * TO DO in the future or not :)
 */
public class MathSolverBuilder implements ScheduleBuilder{

    private ResultingSchedule resultingSchedule;
    private EmployeeSchedule employeeSchedule;
    private ScheduleModel model;

    public void buildSchedule(ScheduleModel model){
        this.model = model;

        int size = 0;
        for (Shift shift: model.getShifts()) {
            size +=shift.getMaxEmployeesNumber();
        }
        int employeeMaxDayNumber = size;
        size*=model.getScheduleDaysNumber();
/*
        Store store = new Store();
        IntVar[][][] x = new IntVar[model.getScheduleDaysNumber()][model.getShifts().size()][model.getEmployeePreferences().size()];

        //define domain of result
        for (int i=0; i<model.getScheduleDaysNumber(); i++)
            for (int j=0;j<model.getShifts().size();j++)
                for (int k=0;k<model.getEmployeePreferences().size();k++){
                    x[i][j][k]= new IntVar(store, "x"+i+j+k, 0,1);
                }
            //result[i] = new IntVar(store, "result"+i, 0, 1);

        //constraints


        //working days number (0 is "no employee")
        IntVar counts[] = new IntVar[model.getEmployeePreferences().size()];
        for (int i=0;i<model.getEmployeePreferences().size()-1; i++){
            counts[i]= new IntVar(store, "counts"+i,
                    model.getEmployeePreferences().get(i+1).getMinWorkingDaysNumber(),
                    model.getEmployeePreferences().get(i+1).getMaxWorkingDaysNumber());
            store.impose(new Among(x[i][][i], new IntervalDomain(i+1,i+1), counts[i]));
        }

        //diffren people in day
        for (int i=0; i<size;i+=employeeMaxDayNumber){
            for (int k=i;k<i+employeeMaxDayNumber;k++){
                for (int j=k+1;j<i+employeeMaxDayNumber;j++) {
                    store.impose(new XneqY(result[k], result[j]));
                }
            }
        }

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select =
                new InputOrderSelect<IntVar>(store, result,
                        new IndomainMin<IntVar>());

        boolean isResult = search.labeling(store, select);

        if ( isResult ){
            System.out.println("Solution: ");
            for (int i=0;i<size;i++){
                System.out.print(result[i]+", ");
            }
        }
        else
            System.out.println("*** No");
*/


    }

    public ResultingSchedule getResultingSchedule(){
        return resultingSchedule;
    }

    public EmployeeSchedule getEmployeeSchedule(){
        return employeeSchedule;
    }
}
