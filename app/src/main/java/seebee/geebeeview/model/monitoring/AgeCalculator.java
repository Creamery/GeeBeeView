package seebee.geebeeview.model.monitoring;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joy on 7/11/2017.
 */

public class AgeCalculator {
    /**
     * calculate the age of the patient at the date of record
     */
    public static int calculateAge(String birthday, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date birthdate, recordDate;
        birthdate = recordDate = null;
        int age = 0;
        try {
            birthdate = sdf.parse(birthday);
            recordDate = sdf.parse(date);
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTimeInMillis(birthdate.getTime());
            Calendar recordDay = Calendar.getInstance();
            recordDay.setTimeInMillis(recordDate.getTime());
            age = recordDay.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
            /* if the record was created before the birthday of the patient, remove 1 year from the age */
            if (recordDay.get(Calendar.MONTH) < birthDay.get(Calendar.MONTH)) {
                age--;
            }
            //Log.v(TAG, "Age: "+age);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return age;
    }
}
