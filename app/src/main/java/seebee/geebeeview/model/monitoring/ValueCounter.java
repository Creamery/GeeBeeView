package seebee.geebeeview.model.monitoring;

import java.util.ArrayList;

/**
 * Created by Joy on 6/27/2017.
 */

public class ValueCounter {
    private final String TAG = "Value Counter";

    private String[] lblVisualAcuity = {"20/200", "20/100", "20/70", "20/50", "20/40", "20/30", "20/25", "20/20",
            "20/15", "20/10", "20/5"};
    private int[] valVisualAcuityLeft = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[] valVisualAcuityRight = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String[] lblColorVision = {"Normal", "Abnormal"};
    private int[] valColorVision = {0, 0};
    private String[] lblHearing = {"Normal Hearing", "Mild Hearing Loss", "Moderate Hearing Loss",
            "Moderately-Servere Hearing Loss", "Severe Hearing Loss", "Profound Hearing Loss"};
    private int[] valHearingLeft = {0, 0, 0, 0, 0, 0};
    private int[] valHearingRight = {0, 0, 0, 0, 0, 0};
    private String[] lblGrossMotor = {"Pass", "Fail", "N/A"};
    private int[] valGrossMotor = {0, 0, 0};
    private String[] lblFineMotor = {"Pass", "Fail"};
    private int[] valFineMotorDom = {0, 0};
    private int[] valFineMotorNonDom = {0, 0};
    private String[] lblFineMotorHold = {"Hold", "Not Hold"};
    private int[] valFineMotorHold = {0, 0};

    private String[] lblBMI = {"Underweight", "Normal", "Overweight", "Obese", "N/A"};
    private int[] valBMI = {0, 0, 0, 0, 0};

    private ArrayList<PatientRecord> patientRecords;

    public ValueCounter(ArrayList<PatientRecord> patientRecords) {
        this.patientRecords = patientRecords;
        setValVisualAcuity();
        setValColorVision();
        setValHearing();
        setValGrossMotor();
        setValFineMotor();
        setValBMI();
    }

    public String[] getLblVisualAcuity() {
        return lblVisualAcuity;
    }

    private void setValVisualAcuity() {
        Record record;
        int[] eye; String vision;
        for(int i = 0; i < patientRecords.size(); i++) {
            record = patientRecords.get(i).getRecord();
            countVisualAcuity(record.getVisualAcuityLeft(), "Left");
            countVisualAcuity(record.getVisualAcuityRight(), "Right");
        }
    }

    private void countVisualAcuity(String vision, String eye) {
        int[] valVision = valVisualAcuityLeft;
        if(eye.contentEquals("Right")) {
            valVision = valVisualAcuityRight;
        }
        switch(vision) {
            case "20/200": valVision[0]++;
                break;
            case "20/100": valVision[1]++;
                break;
            case "20/70": valVision[2]++;
                break;
            case "20/50": valVision[3]++;
                break;
            case "20/40": valVision[4]++;
                break;
            case "20/30": valVision[5]++;
                break;
            case "20/25": valVision[6]++;
                break;
            case "20/20": valVision[7]++;
                break;
            case "20/15": valVision[8]++;
                break;
            case "20/10": valVision[9]++;
                break;
            case "20/5": valVision[10]++;
                break;
        }
    }

    public int[] getValVisualAcuityLeft() {
        return valVisualAcuityLeft;
    }

    public int[] getValVisualAcuityRight() {
        return valVisualAcuityRight;
    }

    public String[] getLblColorVision() {
        return lblColorVision;
    }

    private void setValColorVision() {
        Record record;
        for(int i = 0; i < patientRecords.size(); i++) {
            record = patientRecords.get(i).getRecord();
            switch (record.getColorVision()) {
                case "Normal": valColorVision[0]++;
                    break;
                case "Abnormal": valColorVision[1]++;
                    break;
            }
        }
    }

    public int[] getValColorVision() {
        return valColorVision;
    }

    public String[] getLblHearing() {
        return lblHearing;
    }

    private void setValHearing() {
        Record record;
        for(int i = 0; i < patientRecords.size(); i++) {
            record = patientRecords.get(i).getRecord();
            countHearing(record.getHearingLeft(), "Left");
            countHearing(record.getHearingRight(), "Right");
        }
    }

    private void countHearing(String hearing, String ear) {
        int[] valHearing = valHearingLeft;
        if(ear.contentEquals("Right")) {
            valHearing = valHearingRight;
        }
        switch (hearing) {
            case "Normal Hearing": valHearing[0]++;
                break;
            case "Mild Hearing Loss": valHearing[1]++;
                break;
            case "Moderate Hearing Loss": valHearing[2]++;
                break;
            case "Moderately-Servere Hearing Loss": valHearing[3]++;
                break;
            case "Severe Hearing Loss": valHearing[4]++;
                break;
            case "Profound Hearing Loss": valHearing[5]++;
                break;
        }
    }

    public int[] getValHearingLeft() {
        return valHearingLeft;
    }

    public int[] getValHearingRight() {
        return valHearingRight;
    }

    public String[] getLblGrossMotor() {
        return lblGrossMotor;
    }

    private void setValGrossMotor() {
        Record record;
        for(int i = 0; i < patientRecords.size(); i++) {
            record = patientRecords.get(i).getRecord();
            switch (record.getGrossMotor()) {
                case 0: valGrossMotor[0]++;
                    break;
                case 1: valGrossMotor[1]++;
                    break;
                case 2: valGrossMotor[2]++;
            }
        }
    }

    public int[] getValGrossMotor() {
        return valGrossMotor;
    }

    public String[] getLblFineMotor() {
        return lblFineMotor;
    }

    private void setValFineMotor() {
        Record record;
        for(int i = 0; i < patientRecords.size(); i++) {
            record = patientRecords.get(i).getRecord();
            countFineMotor(record.getFineMotorDominant(), "Dominant");
            countFineMotor(record.getFineMotorNDominant(), "Non-Dominant");
            countFineMotor(record.getFineMotorHold(), "Hold");
        }
    }

    private void countFineMotor(int result, String hand) {
        int[] valFineMotor = valFineMotorDom;
        if(hand.contentEquals("Non-Dominant")) {
            valFineMotor = valFineMotorNonDom;
        } else if (hand.contentEquals("Hold")) {
            valFineMotor = valFineMotorHold;
        }
        switch (result) {
            case 0: valFineMotor[0]++;
                break;
            case 1: valFineMotor[1]++;
                break;
        }
    }

    public int[] getValFineMotorDom() {
        return valFineMotorDom;
    }

    public int[] getValFineMotorNonDom() {
        return valFineMotorNonDom;
    }

    public String[] getLblFineMotorHold() {
        return lblFineMotorHold;
    }

    public int[] getValFineMotorHold() {
        return valFineMotorHold;
    }

    public int[] getValBMI() {
        return valBMI;
    }

    private void setValBMI() {
        String result;
        for(int i = 0; i < patientRecords.size(); i++) {
            result = calculateBMI(patientRecords.get(i));
            switch (result) {
                case "Underweight": valBMI[0]++;
                    break;
                case "Normal": valBMI[1]++;
                    break;
                case "Overweight": valBMI[2]++;
                    break;
                case "Obese": valBMI[3]++;
                    break;
                case "N/A": valBMI[4]++;
            }
            //Log.v(TAG, "Patient No: "+i+" Result: "+result);
        }
    }

    private String calculateBMI(PatientRecord patientRecord) {
        int height, weight;
        height = Double.valueOf(patientRecord.getRecord().getHeight()).intValue();
        weight =Double.valueOf(patientRecord.getRecord().getWeight()).intValue();
        float bmi = BMICalculator.computeBMIMetric(height, weight);
        return BMICalculator.getBMIResultString(patientRecord.getGender(), patientRecord.getAge(), bmi);
    }

    public String[] getLblBMI() {
        return lblBMI;
    }

    public static String convertRecordColumn(String column) {
        String recordColumn = "";
        switch (column){
            case "Visual Acuity Right":
                recordColumn = Record.C_VISUAL_ACUITY_RIGHT;
                break;
            case "Visual Acuity Left":
                recordColumn = Record.C_VISUAL_ACUITY_LEFT;
                break;
            case "Color Vision":
                recordColumn = Record.C_COLOR_VISION;
                break;
            case "Hearing Left":
                recordColumn = Record.C_HEARING_LEFT;
                break;
            case "Hearing Right":
                recordColumn = Record.C_HEARING_RIGHT;
                break;
            case "Gross Motor":
                recordColumn = Record.C_GROSS_MOTOR;
                break;
            case "Fine Motor (Dominant Hand)":
                recordColumn = Record.C_FINE_MOTOR_DOMINANT;
                break;
            case "Fine Motor (Non-Dominant Hand)":
                recordColumn = Record.C_FINE_MOTOR_N_DOMINANT;
                break;
            case "Fine Motor (Hold)":
                recordColumn = Record.C_FINE_MOTOR_HOLD;
                break;
        }
        return recordColumn;
    }

    public static int ConvertMotor(String v) {
        int result = 2;
        if(v.contentEquals("Pass")) {
            result = 0;
        } else if(v.contentEquals("Fail")) {
            result = 1;
        }
        return result;
    }

    public static int ConvertHold(String v) {
        int result = 2;
        if(v.contentEquals("Hold")) {
            result = 0;
        } else if(v.contentEquals("Not Hold")) {
            result = 1;
        }
        return result;
    }
}
