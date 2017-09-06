package seebee.geebeeview.model.monitoring;

/**
 * The PatientRecord class contains the record data of a patient including their age and gender.
 * This class was created for the sole convenience
 * of having the record with the age and gender of the patient.
 *
 * @author Stephanie Dy
 */

public class PatientRecord  {

    private final String TAG = "PatientRecord";

    /* gender
     * 0 - Male
     * 1 - Female */
    private int gender;

    private int age;

    //private float bmi;

    private Record record;

    public PatientRecord (int gender, String birthday, Record record) {
        this.gender = gender;
        setAge(birthday, record.getDateCreated());
        this.record = record;
    }

    private void setAge(String birthday, String date)  {
        age = AgeCalculator.calculateAge(birthday, date);
    }

    public boolean getGender() {
        boolean isGirl = false;
        if(gender == 1){
            isGirl = true;
        }
        return isGirl;
    }

    public int getAge() {
        return age;
    }

    public Record getRecord() {
        return record;
    }
}
