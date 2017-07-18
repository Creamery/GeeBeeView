package seebee.geebeeview.model.monitoring;

import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Joy on 7/6/2017.
 */

public class PatientRecord  {

    private final String TAG = "PatientRecord";

    /* gender
     * 0 - Male
     * 1 - Female */
    private int gender;

    private int age;

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
