package employeeScheduler.builder;

import employeeScheduler.model.*;

import ilog.concert.*;
import ilog.cplex.*;


/**
 * Created by Karolina on 2016-04-27.
 */
public class CPLEXBuilder implements ScheduleBuilder {
    private ResultingSchedule resultingSchedule;
    private EmployeeSchedule employeeSchedule;
    private ScheduleModel model;

    public void buildSchedule(ScheduleModel model) {
        this.model = model;
        int daysNumber = model.getScheduleDaysNumber();
        int shiftsNumber = model.getShifts().size();
        int employeesNumber = model.getEmployeePreferences().size();

        try {
            //------------------model init--------------------
            IloCplex cplex = new IloCplex();

            //-------------------variables--------------------
            //simple representation of result schedule
            IloNumVar[][][] x = new IloNumVar[daysNumber][shiftsNumber][employeesNumber];
            for (int i = 0; i < daysNumber; i++) {
                for (int j = 0; j < shiftsNumber; j++) {
                    x[i][j] = cplex.boolVarArray(employeesNumber);
                }
            }
            //auxiliary variable needed in minimizeFunction
            IloNumVar overTime = cplex.boolVar();

            //--------------minimize function-----------------
            minimizeFunction(overTime, cplex, x);

            //-----------------constraints--------------------
            auxiliaryVariableExpr(overTime,cplex);
            minMaxEmployeesInShift(cplex, x);
            oneEmployeeInDay(cplex, x);
            employeesHardPreferences(cplex, x);

            //--------------------result----------------------
            returnResult(overTime,cplex,x);

        } catch (IloException exc) {
            exc.printStackTrace();
        }
    }

    private void auxiliaryVariableExpr(IloNumVar overTime, IloCplex cplex) throws IloException {
        IloLinearNumExpr overTimeExpr = cplex.linearNumExpr();
        overTimeExpr.addTerm(1.0, overTime);
        cplex.addGe(overTimeExpr, 1.0);
    }

    private void returnResult(IloNumVar overTime, IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();
        int daysNumber = model.getScheduleDaysNumber();
        int shiftsNumber = model.getShifts().size();
        if (cplex.solve()) {

            resultingSchedule = new ResultingSchedule(daysNumber, shiftsNumber);
            cplex.output().println("temp" + cplex.getValue(overTime));
            cplex.output().println("Solution status = " + cplex.getStatus());
            cplex.output().println("Solution value = " + cplex.getObjValue());
            for (int i = 0; i < daysNumber; i++) {
                //cplex.output().println("Day :" + i);
                for (int j = 0; j < shiftsNumber; j++) {
                    //cplex.output().println("Shift :" + j);
                    double[] val = cplex.getValues(x[i][j]);
                    for (int k = 0; k < employeesNumber; k++) {
                        if (val[k] == 1.0) {
                            resultingSchedule.addEmployee(i, j, k);
                        }
                    }
                }
            }
        } else {
            cplex.output().println("Do not find solution");
        }
        cplex.end();
    }

    private void minimizeFunction(IloNumVar overTime, IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();

        IloLinearNumExpr[] minimizeObjects = new IloLinearNumExpr[employeesNumber * 3];

        numberWorkingDays(minimizeObjects, overTime, cplex, x);
        employeeSoftPreferences(minimizeObjects, overTime, cplex, x);

        IloNumExpr[] minimizeObjectsAbs = new IloNumExpr[employeesNumber * 3];
        for (int i = 0; i < employeesNumber * 3; i++) {
            minimizeObjectsAbs[i] = cplex.abs(minimizeObjects[i]);
        }
        cplex.addMinimize(cplex.max(minimizeObjectsAbs));
    }

    private void numberWorkingDays(IloLinearNumExpr[] minimizeObjects, IloNumVar overTime, IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();
        int daysNumber = model.getScheduleDaysNumber();
        int shiftsNumber = model.getShifts().size();

        IloLinearNumExpr[] minEmployeesDaysExpr = new IloLinearNumExpr[employeesNumber];
        IloLinearNumExpr[] maxEmployeesDaysExpr = new IloLinearNumExpr[employeesNumber];

        for (int emp = 0; emp < employeesNumber; emp++) {
            minEmployeesDaysExpr[emp] = cplex.linearNumExpr();
            maxEmployeesDaysExpr[emp] = cplex.linearNumExpr();
            minimizeObjects[emp] = cplex.linearNumExpr();
            for (int day = 0; day < daysNumber; day++) {
                for (int shift = 0; shift < shiftsNumber; shift++) {
                    double shiftTime = model.getShifts().get(shift).getShiftTime();
                    minEmployeesDaysExpr[emp].addTerm(shiftTime, x[day][shift][emp]);
                    maxEmployeesDaysExpr[emp].addTerm(shiftTime, x[day][shift][emp]);
                    minimizeObjects[emp].addTerm(shiftTime, x[day][shift][emp]);
                }
            }
            double max = model.getEmployeePreferences().get(emp).getMaxWorkingDaysNumber().doubleValue();
            max *= model.getEmployeePreferences().get(emp).getDailyWorkTime();
            max += model.getEmployeePreferences().get(emp).getCurrentOvertime();
            double min = model.getEmployeePreferences().get(emp).getMinWorkingDaysNumber().doubleValue();
            min *= model.getEmployeePreferences().get(emp).getDailyWorkTime();
            min += model.getEmployeePreferences().get(emp).getCurrentOvertime();
            double target = model.getEmployeePreferences().get(emp).getTargetWorkingDaysNumber().doubleValue();
            target *= model.getEmployeePreferences().get(emp).getDailyWorkTime();
            target += model.getEmployeePreferences().get(emp).getCurrentOvertime();
            target *= -1;
            minimizeObjects[emp].addTerm(target, overTime);
            cplex.addLe(maxEmployeesDaysExpr[emp], max);
            cplex.addGe(minEmployeesDaysExpr[emp], min);
        }
    }

    private void employeeSoftPreferences(IloLinearNumExpr[] minimizeObjects, IloNumVar overTime, IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();
        for (int emp = 0; emp < employeesNumber; emp++) {
            //Employee Preferences Acceptance Level
            Double empAccPref = model.getEmployeePreferences().get(emp).getPreferencesAcceptanceLevel().getValue();
            double requiredNumber = 0.0;
            minimizeObjects[emp + employeesNumber] = cplex.linearNumExpr();
            minimizeObjects[emp + 2 * employeesNumber] = cplex.linearNumExpr();
            for (int i = 0; i < model.getEmployeePreferences().get(emp).getPreferences().size(); i++) {
                EmployeePreferences pref = model.getEmployeePreferences().get(emp).getPreferences().get(i);
                Double shiftTime = model.getShifts().get(pref.getShiftNumber()).getShiftTime().doubleValue();
                if (pref.getPreference() == Preferences.PREFERRED) {
                    requiredNumber -= 1.0;
                    minimizeObjects[emp + employeesNumber].addTerm(empAccPref * shiftTime, x[pref.getDayNumber()][pref.getShiftNumber()][emp]);
                }
                if (pref.getPreference() == Preferences.UNWANTED) {
                    minimizeObjects[emp + 2 * employeesNumber].addTerm(empAccPref * shiftTime, x[pref.getDayNumber()][pref.getShiftNumber()][emp]);
                }
            }
            minimizeObjects[emp + employeesNumber].addTerm(requiredNumber * empAccPref, overTime);
        }
    }

    private void minMaxEmployeesInShift(IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int daysNumber = model.getScheduleDaysNumber();
        int shiftsNumber = model.getShifts().size();
        int employeesNumber = model.getEmployeePreferences().size();

        IloLinearNumExpr[][] minEmployeesPerShiftExpr = new IloLinearNumExpr[daysNumber][shiftsNumber];
        IloLinearNumExpr[][] maxEmployeesPerShiftExpr = new IloLinearNumExpr[daysNumber][shiftsNumber];
        for (int shift = 0; shift < shiftsNumber; shift++) {
            int min = model.getShifts().get(shift).getMinEmployeesNumber();
            int max = model.getShifts().get(shift).getMaxEmployeesNumber();
            for (int day = 0; day < daysNumber; day++) {
                minEmployeesPerShiftExpr[day][shift] = cplex.linearNumExpr();
                maxEmployeesPerShiftExpr[day][shift] = cplex.linearNumExpr();
                for (int emp = 0; emp < employeesNumber; emp++) {
                    minEmployeesPerShiftExpr[day][shift].addTerm(1.0, x[day][shift][emp]);
                    maxEmployeesPerShiftExpr[day][shift].addTerm(1.0, x[day][shift][emp]);
                }
                cplex.addGe(minEmployeesPerShiftExpr[day][shift], min);
                cplex.addLe(maxEmployeesPerShiftExpr[day][shift], max);
            }
        }
    }

    private void oneEmployeeInDay(IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int daysNumber = model.getScheduleDaysNumber();
        int shiftsNumber = model.getShifts().size();
        int employeesNumber = model.getEmployeePreferences().size();
        IloLinearNumExpr[][] employeesInDayExpr = new IloLinearNumExpr[daysNumber][employeesNumber];
        for (int day = 0; day < daysNumber; day++) {
            for (int emp = 0; emp < employeesNumber; emp++) {
                employeesInDayExpr[day][emp] = cplex.linearNumExpr();
                for (int shift = 0; shift < shiftsNumber; shift++) {
                    employeesInDayExpr[day][emp].addTerm(1.0, x[day][shift][emp]);
                }
                cplex.addLe(employeesInDayExpr[day][emp], 1.0);
            }
        }
    }

    private void employeesHardPreferences(IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();
        IloLinearNumExpr preferencesRequiredExpr = cplex.linearNumExpr();
        IloLinearNumExpr preferencesForbiddenExpr = cplex.linearNumExpr();
        for (int emp = 0; emp < employeesNumber; emp++) {
            int requiredNumber = 0;
            for (int i = 0; i < model.getEmployeePreferences().get(emp).getPreferences().size(); i++) {
                EmployeePreferences pref = model.getEmployeePreferences().get(emp).getPreferences().get(i);
                if (pref.getPreference() == Preferences.REQUIRED) {
                    requiredNumber++;
                    preferencesRequiredExpr.addTerm(1.0, x[pref.getDayNumber()][pref.getShiftNumber()][emp]);
                }
                if (pref.getPreference() == Preferences.FORBIDDEN) {
                    preferencesForbiddenExpr.addTerm(1.0, x[pref.getDayNumber()][pref.getShiftNumber()][emp]);
                }
                if (pref.getPreference() == Preferences.VACATION) {
                    preferencesForbiddenExpr.addTerm(1.0, x[pref.getDayNumber()][pref.getShiftNumber()][emp]);
                }

            }
            cplex.addLe(preferencesForbiddenExpr, 0.0);
            cplex.addGe(preferencesRequiredExpr, requiredNumber);
        }
    }

    public ResultingSchedule getResultingSchedule() {
        return resultingSchedule;
    }

    public EmployeeSchedule getEmployeeSchedule() {
        return employeeSchedule;
    }
}
