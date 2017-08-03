package seebee.geebeeview.model.account;

import android.util.Log;

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

    public final static String C_SCHOOL_ID = "school_id";

    public final static String C_DATE_CREATED = "date_created";

    public final static String C_STATUS = "status";

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
    public Dataset (int schoolID, String schoolName, String date, int status) {
        this.schoolID = schoolID;
        this.schoolName = schoolName;
        this.date = date;
        this.status = status;
    }
    /**
     * Gets {@link #schoolName}.
     * @return {@link #schoolName}
     */
    public String getSchoolName() {
        return schoolName;
    }
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
        Log.d(TAG, "schoolID: "+schoolID+" schoolName: "+schoolName+" dateCreated: "+date+" status: "+status);
    }
}
