package seebee.geebeeview.model.account;

import android.util.Log;

import java.util.Objects;

/**
 * The Dataset class represents a dataset
 * containing information about the health records
 * of each school and the date of checkup.
 *
 * @author Stephanie Dy
 * @since 5/31/2017.
 */

public class Dataset {
    public final static String TAG = "Dataset";

    public final static String TABLE_NAME = "tbl_dataset";

    public final static String C_ID = "dataset_id";

    public final static String C_SCHOOLID = "school_id";

    public final static String C_SCHOOL_NAME = "school_name";

    public final static String C_DATE_CREATED = "date_created";

    public final static String C_STATUS = "status";

    private int id;

    private int schoolID;
    /* School name from where dataset was recorded */
    private String schoolName;
    /* Date when the dataset was recorded */
    private String date;
    /* Status of the dataset in device,
     * 0 = not available
     * 1 = downloaded
     */
    private int status;
    /** Constructor
     * @param schoolName {@link #schoolName}
     * @param date {@link #date}
     * @param status {@link #status}
     */
    public Dataset (int id, int schoolID, String schoolName, String date, int status) {
        this.id = id;
        this.schoolID = schoolID;
        this.date = date;
        this.status = status;
        this.schoolName = schoolName;
    }
    /**
     * Gets {@link #schoolName}.
     * @return {@link #schoolName}
     */

    /**
     * Gets {@link #date}.
     * @return {@link #date}
     */
    public String getDate() {
        return date;
    }
    /**
     * Gets {@link #status}.
     * @return {@link #status}
     */
    public int getStatus() {
        return status;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public void printDataset() {
        Log.d(TAG, "schoolID: "+schoolID+" dateCreated: "+date+" status: "+status);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatusTo1(){status =1;}

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
