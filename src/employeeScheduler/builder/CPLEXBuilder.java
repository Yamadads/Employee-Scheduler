package employeeScheduler.builder;

import employeeScheduler.model.*;

import ilog.concert.*;
import ilog.cplex.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Builder uses not free math solver CPLEX. It is not included in this lib.
 */
public class CPLEXBuilder implements ScheduleBuilder {
    private ResultingSchedule resultingSchedule;
    private ScheduleModel model;

    //build function
    public void buildSchedule(ScheduleModel model) {
        this.model = model;
        int daysNumber = model.getScheduleDaysNumber();
        int shiftsNumber = model.getShifts().size();
        int employeesNumber = model.getEmployeePreferences().size();

        try {
            //------------------model init--------------------
            IloCplex cplex = new IloCplex();
            cplex.setParam(IloCplex.DoubleParam.TiLim, 30);
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
            auxiliaryVariableCons(overTime, cplex);
            minMaxEmployeesInShiftCons(cplex, x);
            oneEmployeeInDayCons(cplex, x);
            minDayBreakTimeCons(cplex, x);
            employeesHardPreferencesCons(cplex, x);
            minWeekBreakTimeCons(cplex, x);

            //--------------------result----------------------
            returnResult(overTime, cplex, x);

        } catch (IloException exc) {
            exc.printStackTrace();
            System.out.println("Problem with cplex. Press \"ENTER\" to continue...");
            try{
                System.in.read();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    //target specify
    private void minimizeFunction(IloNumVar overTime, IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();

        IloLinearNumExpr[] minimizeObjects = new IloLinearNumExpr[employeesNumber * 3];

        numberWorkingDaysCons(minimizeObjects, overTime, cplex, x);
        employeeSoftPreferencesCons(minimizeObjects, overTime, cplex, x);

        IloNumExpr[] minimizeObjectsAbs = new IloNumExpr[employeesNumber * 3];
        for (int i = 0; i < employeesNumber * 3; i++) {
            minimizeObjectsAbs[i] = cplex.abs(minimizeObjects[i]);
        }
        cplex.addMinimize(cplex.max(minimizeObjectsAbs));
    }

    //Constraints
    private void minWeekBreakTimeCons(IloCplex cplex, IloNumVar[][][] x) throws IloException {
        IloOr[] orConstraint = new IloOr[model.getEmployeePreferences().size()];
        ArrayList<IloConstraint[]> minWeekBreak = new ArrayList<>();
        ArrayList<IloLinearNumExpr[]> breakTime = new ArrayList<>();
        for (int emp = 0; emp < model.getEmployeePreferences().size(); emp++) {
            //prepare set of shifts to constraint
            ArrayList<ArrayList<ArrayList<Integer>>> toConstraint = getListOfShiftsToBreakTime(emp);

            Integer maxDay = 0;
            for (int i = 0; i < toConstraint.size(); i++) {
                if (maxDay < toConstraint.get(i).size()) {
                    maxDay = toConstraint.get(i).size();
                }
            }
            for (int firstDay = 0; firstDay < model.getScheduleDaysNumber(); firstDay += 7) {
                if (firstDay+4>=model.getScheduleDaysNumber()){
                    break;
                }
                minWeekBreak.add(new IloConstraint[(7 - maxDay) * toConstraint.size()]);
                breakTime.add(new IloLinearNumExpr[(7 - maxDay) * toConstraint.size()]);
                int constraintNumber = 0;
                for (int day = firstDay; (day < firstDay + 7 - maxDay) && (day < model.getScheduleDaysNumber() - maxDay); day++) {
                    for (int shift = 0; shift < toConstraint.size(); shift++) {
                        //and
                        breakTime.get(breakTime.size()-1)[constraintNumber] = cplex.linearNumExpr();
                        for (int i = 0; i < model.getShifts().size(); i++) {
                            if (i != shift) {
                                breakTime.get(breakTime.size()-1)[constraintNumber].addTerm(1.0, x[day][i][emp]);
                            }
                        }
                        for (int andDay = 0; andDay < toConstraint.get(shift).size(); andDay++) {
                            for (int andShift = 0; andShift < toConstraint.get(shift).get(andDay).size(); andShift++) {
                                breakTime.get(breakTime.size()-1)[constraintNumber].addTerm(1.0, x[andDay + day + 1][toConstraint.get(shift).get(andDay).get(andShift)][emp]);
                            }
                        }
                        minWeekBreak.get(minWeekBreak.size()-1)[constraintNumber] = cplex.le(breakTime.get(breakTime.size()-1)[constraintNumber], 0.0);
                        constraintNumber++;
                        //and
                    }
                }
                orConstraint[emp] = cplex.or();
                orConstraint[emp].add(minWeekBreak.get(minWeekBreak.size()-1));
                cplex.add(orConstraint[emp]);
            }
        }
    }

    private ArrayList<ArrayList<ArrayList<Integer>>> getListOfShiftsToBreakTime(Integer employeeNumber) {
        ArrayList<ArrayList<ArrayList<Integer>>> toConstraint = new ArrayList<>();
        for (int i = 0; i < model.getShifts().size(); i++) {
            toConstraint.add(new ArrayList<>());
            for (int d = 0; d < 7; d++) {
                toConstraint.get(i).add(new ArrayList<>());
                Boolean end = true;
                for (int j = 0; j < model.getShifts().size(); j++) {
                    if (model.getShifts().get(i).isNightShift()) {
                        if ((model.getShifts().get(j).getStartTime().getTimeInMinutes()
                                -
                                model.getShifts().get(i).getEndTime().getTimeInMinutes()
                                +
                                1440 * d
                        ) < model.getEmployeePreferences().get(employeeNumber).getMinWeekBreakTime()) {
                            end = false;
                            toConstraint.get(i).get(d).add(j);
                        }
                    } else {
                        if ((model.getShifts().get(j).getStartTime().getTimeInMinutes()
                                +
                                1440 - model.getShifts().get(i).getEndTime().getTimeInMinutes()
                                + 1440 * d
                        ) < model.getEmployeePreferences().get(employeeNumber).getMinWeekBreakTime()) {
                            end = false;
                            toConstraint.get(i).get(d).add(j);
                        }
                    }
                }
                if (end) {
                    break;
                }
            }
        }
        return toConstraint;
    }

    private void numberWorkingDaysCons(IloLinearNumExpr[] minimizeObjects, IloNumVar overTime, IloCplex cplex, IloNumVar[][][] x) throws IloException {
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

    private void employeeSoftPreferencesCons(IloLinearNumExpr[] minimizeObjects, IloNumVar overTime, IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();
        for (int emp = 0; emp < employeesNumber; emp++) {
            Double empAccPref = model.getEmployeePreferences().get(emp).getPreferencesAcceptanceLevel();
            double requiredNumber = 0.0;
            minimizeObjects[emp + employeesNumber] = cplex.linearNumExpr();
            minimizeObjects[emp + 2 * employeesNumber] = cplex.linearNumExpr();
            for (int i = 0; i < model.getEmployeePreferences().get(emp).getPreferences().size(); i++) {
                EmployeePreferences pref = model.getEmployeePreferences().get(emp).getPreferences().get(i);
                Double shiftTime = model.getShifts().get(pref.getShiftNumber()).getShiftTime().doubleValue();
                if (pref.getPreference() == Preferences.PREFERRED) {
                    requiredNumber -= shiftTime * empAccPref;
                    minimizeObjects[emp + employeesNumber].addTerm(empAccPref * shiftTime, x[pref.getDayNumber()][pref.getShiftNumber()][emp]);
                }
                if (pref.getPreference() == Preferences.UNWANTED) {
                    minimizeObjects[emp + 2 * employeesNumber].addTerm(empAccPref * shiftTime, x[pref.getDayNumber()][pref.getShiftNumber()][emp]);
                }
            }
            minimizeObjects[emp + employeesNumber].addTerm(requiredNumber, overTime);
        }
    }

    private void minMaxEmployeesInShiftCons(IloCplex cplex, IloNumVar[][][] x) throws IloException {
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

    private void oneEmployeeInDayCons(IloCplex cplex, IloNumVar[][][] x) throws IloException {
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

    private void employeesHardPreferencesCons(IloCplex cplex, IloNumVar[][][] x) throws IloException {
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

    private void minDayBreakTimeCons(IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();
        for (int emp = 0; emp < employeesNumber; emp++) {
            Integer minBreakTime = model.getEmployeePreferences().get(emp).getMinDayBreakTime();
            for (int i = 0; i < model.getShifts().size(); i++) {
                for (int j = 0; j < model.getShifts().size(); j++) {
                    if (j == i) continue;
                    Shift outerShift = model.getShifts().get(i);
                    Integer outerShiftEndTimeMinutes = model.getShifts().get(i).getEndTime().getTimeInMinutes();
                    Integer innerShiftStartTimeMinutes = model.getShifts().get(j).getStartTime().getTimeInMinutes();
                    if (outerShift.isNightShift()) {
                        if ((innerShiftStartTimeMinutes - outerShiftEndTimeMinutes) < minBreakTime) {
                            minDayBreakTimeSetConstraint(cplex, x, i, j);
                        }
                    } else {
                        if (((1440 - outerShiftEndTimeMinutes) + innerShiftStartTimeMinutes) < minBreakTime) {
                            minDayBreakTimeSetConstraint(cplex, x, i, j);
                        }
                    }
                }
            }
        }
    }

    private void minDayBreakTimeSetConstraint(IloCplex cplex, IloNumVar[][][] x, Integer outerShiftNr, Integer innerShiftNr) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();
        int daysNumber = model.getScheduleDaysNumber();

        IloLinearNumExpr[][] dayBreakTimeExpr = new IloLinearNumExpr[daysNumber - 1][employeesNumber];

        for (int day = 0; day < daysNumber - 1; day++) {
            for (int emp = 0; emp < employeesNumber; emp++) {
                dayBreakTimeExpr[day][emp] = cplex.linearNumExpr();
                dayBreakTimeExpr[day][emp].addTerm(1.0, x[day][outerShiftNr][emp]);
                dayBreakTimeExpr[day][emp].addTerm(1.0, x[day + 1][innerShiftNr][emp]);
                cplex.addLe(dayBreakTimeExpr[day][emp], 1.0);
            }
        }
    }

    private void auxiliaryVariableCons(IloNumVar overTime, IloCplex cplex) throws IloException {
        IloLinearNumExpr overTimeExpr = cplex.linearNumExpr();
        overTimeExpr.addTerm(1.0, overTime);
        cplex.addGe(overTimeExpr, 1.0);
    }

    //Result
    public ResultingSchedule getResultingSchedule() {
        return resultingSchedule;
    }

    private void returnResult(IloNumVar overTime, IloCplex cplex, IloNumVar[][][] x) throws IloException {
        int employeesNumber = model.getEmployeePreferences().size();
        int daysNumber = model.getScheduleDaysNumber();
        int shiftsNumber = model.getShifts().size();
        if (cplex.solve()) {
            fillResultingSchedule(cplex, x, daysNumber, shiftsNumber, employeesNumber);
            fillEmployeesOvertime();
            fillEmployeeFulfilledPreferences();
            maxEmployeesPerShift();
        } else {
            cplex.output().println("Do not find solution");
            resultingSchedule = new ResultingSchedule(0, 0, 0);
        }
        cplex.end();
    }

    private void fillEmployeeFulfilledPreferences() {
        int employeesNumber = model.getEmployeePreferences().size();
        for (int emp = 0; emp < employeesNumber; emp++) {
            Double fulfilledPreferences = 0.0;
            Double unfulfilled = 0.0;
            Integer preferencesNumber = model.getEmployeePreferences().get(emp).getPreferences().size();
            for (int i = 0; i < preferencesNumber; i++) {
                Integer day = model.getEmployeePreferences().get(emp).getPreferences().get(i).getDayNumber();
                Integer shift = model.getEmployeePreferences().get(emp).getPreferences().get(i).getShiftNumber();
                Preferences preference = model.getEmployeePreferences().get(emp).getPreferences().get(i).getPreference();

                if (preference == Preferences.PREFERRED) {
                    if (!resultingSchedule.getEmployeesSchedules().get(emp).isOnShift(day, shift)) {
                        unfulfilled += 1.0;
                    }
                }
                if (preference == Preferences.UNWANTED) {
                    if (resultingSchedule.getEmployeesSchedules().get(emp).isOnShift(day, shift)) {
                        unfulfilled += 1.0;
                    }
                }
            }
            fulfilledPreferences = ((preferencesNumber.doubleValue() - unfulfilled) / preferencesNumber.doubleValue());
            resultingSchedule.getEmployeesSchedules().get(emp).setFulfilledPreferences(fulfilledPreferences);
        }
    }

    private void fillEmployeesOvertime() {
        int employeesNumber = model.getEmployeePreferences().size();
        for (int emp = 0; emp < employeesNumber; emp++) {
            Integer workTimeTemp = 0;
            for (Integer value : resultingSchedule.getEmployeesSchedules().get(emp).getResult().values()) {
                workTimeTemp += model.getShifts().get(value).getShiftTime();
            }
            resultingSchedule.getEmployeesSchedules().get(emp).setWorkTime(workTimeTemp);
        }
    }

    private void fillResultingSchedule(IloCplex cplex, IloNumVar[][][] x, Integer daysNumber, Integer shiftsNumber, Integer employeesNumber) throws IloException {
        resultingSchedule = new ResultingSchedule(daysNumber, shiftsNumber, employeesNumber);
        cplex.output().println("Solution status = " + cplex.getStatus());
        cplex.output().println("Solution value = " + cplex.getObjValue());
        for (int i = 0; i < daysNumber; i++) {
            for (int j = 0; j < shiftsNumber; j++) {
                double[] val = cplex.getValues(x[i][j]);
                for (int k = 0; k < employeesNumber; k++) {
                    if (val[k] == 1.0) {
                        resultingSchedule.addEmployee(i, j, k);
                    }
                }
            }
        }
    }

    private void maxEmployeesPerShift() {
        for (int i = 0; i < model.getShifts().size(); i++) {
            resultingSchedule.addShiftMaxEmployee(i, model.getShifts().get(i).getMaxEmployeesNumber());
        }
    }


}

