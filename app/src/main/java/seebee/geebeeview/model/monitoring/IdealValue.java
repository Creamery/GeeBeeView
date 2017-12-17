package seebee.geebeeview.model.monitoring;

import android.util.Log;

/**
 * The IdealValue class represents a health record
 * containing information from the ideal growth values
 * provided by World Health Organization.
 *
 * @author Stephanie Dy
 * @since 8/15/2017
 */

public class IdealValue {
    /**
     * Used to identify the source of a log message
     */
    public final static String TAG = "IdealGrowthValue";

    /**
     * Database table name for Ideal Growth Value.
     */
    public final static String TABLE_NAME = "tbl_ideal_growth_val";

    /**
     * Database column name for storing {@link #growthID}.
     */
    public final static String C_GROWTH_ID = "growth_id";

    /**
     * Database column name for storing {@link #recordColumn}.
     */
    public final static String C_RECORD_COLUMN = "record_column";

    /**
     * Database column name for storing {@link #gender}.
     */
    public final static String C_GENDER = "gender";

    /**
     * Database column name for storing {@link #year}.
     */
    public final static String C_YEAR = "year";

    /**
     * Database column name for storing {@link #month}.
     */
    public final static String C_MONTH = "month";

    /**
     * Database column name for storing {@link #n3SD}.
     */
    public final static String C_N3SD = "n3sd";

    /**
     * Database column name for storing {@link #n2SD}.
     */
    public final static String C_N2SD = "n2sd";

    /**
     * Database column name for storing {@link #n1SD}.
     */
    public final static String C_N1SD = "n1sd";

    /**
     * Database column name for storing {@link #median}.
     */
    public final static String C_MEDIAN = "median";

    /**
     * Database column name for storing {@link #p1SD}.
     */
    public final static String C_P1SD = "p1sd";

    /**
     * Database column name for storing {@link #p2SD}.
     */
    public final static String C_P2SD = "p2sd";

    /**
     * Database column name for storing {@link #p3SD}.
     */
    public final static String C_P3SD = "p3sd";

    /**
     * ID of the Ideal Value.
     */
    private int growthID;

    /**
     * Column of the record wherein the ideal value is given.
     */
    private String recordColumn;

    /**
     * Gender of the patient as basis on the ideal value given.
     */
    private int gender;

    /**
     * Age of patient in years.
     */
    private int year;

    /**
     * Age of patient in months after a complete year.
     */
    private int month;

    /**
     * -3SD from the ideal value.
     */
    private float n3SD;

    /**
     * -2SD from the ideal value.
     */
    private float n2SD;

    /**
     * -1SD from the ideal value.
     */
    private float n1SD;

    /**
     * Median of the ideal value.
     */
    private float median;

    /**
     * 1SD from the ideal value.
     */
    private float p1SD;

    /**
     * 2 SD from the ideal value.
     */
    private float p2SD;

    /**
     * 3 SD from the ideal value.
     */
    private float p3SD;

    /**
     * Constructor
     * @param growthID {@link #growthID}
     * @param recordColumn {@link #recordColumn}
     * @param gender {@link #gender}
     * @param year {@link #year}
     * @param month {@link #month}
     * @param n3SD {@link #n3SD}
     * @param n2SD {@link #n2SD}
     * @param n1SD {@link #n1SD}
     * @param median {@link #median}
     * @param p1SD {@link #p1SD}
     * @param p2SD {@link #p2SD}
     * @param p3SD {@link #p3SD}
     */
    public IdealValue(int growthID, String recordColumn, int gender, int year, int month,
                      float n3SD, float n2SD, float n1SD, float median, float p1SD, float p2SD, float p3SD) {
        this.growthID = growthID;
        this.recordColumn = recordColumn;
        this.gender = gender;
        this.year = year;
        this.month = month;
        this.n3SD = n3SD;
        this.n2SD = n2SD;
        this.n1SD = n1SD;
        this.median = median;
        this.p1SD = p1SD;
        this.p2SD = p2SD;
        this.p3SD = p3SD;
    }

    /**
     * ID of the Ideal Value.
     */
    public int getGrowthID() {
        return growthID;
    }

    public void setGrowthID(int growthID) {
        this.growthID = growthID;
    }

    /**
     * Column of the record wherein the ideal value is given.
     */
    public String getRecordColumn() {
        return recordColumn;
    }

    public void setRecordColumn(String recordColumn) {
        this.recordColumn = recordColumn;
    }

    /**
     * Gender of the patient as basis on the ideal value given.
     */
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * Age of patient in years.
     */
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Age of patient in months after a complete year.
     */
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * -3SD from the ideal value.
     */
    public float getN3SD() {
        return n3SD;
    }

    public void setN3SD(float n3SD) {
        this.n3SD = n3SD;
    }

    /**
     * -2SD from the ideal value.
     */
    public float getN2SD() {
        return n2SD;
    }

    public void setN2SD(float n2SD) {
        this.n2SD = n2SD;
    }

    /**
     * -1SD from the ideal value.
     */
    public float getN1SD() {
        return n1SD;
    }

    public void setN1SD(float n1SD) {
        this.n1SD = n1SD;
    }

    /**
     * Median of the ideal value.
     */
    public float getMedian() {
        return median;
    }

    public void setMedian(float median) {
        this.median = median;
    }

    /**
     * 1SD from the ideal value.
     */
    public float getP1SD() {
        return p1SD;
    }

    public void setP1SD(float p1SD) {
        this.p1SD = p1SD;
    }

    /**
     * 2 SD from the ideal value.
     */
    public float getP2SD() {
        return p2SD;
    }

    public void setP2SD(float p2SD) {
        this.p2SD = p2SD;
    }

    /**
     * 3 SD from the ideal value.
     */
    public float getP3SD() {
        return p3SD;
    }

    public void setP3SD(float p3SD) {
        this.p3SD = p3SD;
    }

    public String getCompleteInfo() {
        return "growthID: "+growthID+", record column: "+recordColumn+", gender: "+gender
                +", year: "+year+", month: "+month+", -3SD: "+n3SD+", -2SD: "+n2SD+", -1SD: "+n1SD
                +", median: "+median+", 1SD: "+p1SD+", 2SD: "+p2SD+", 3SD: "+p3SD;
    }

    public void print() {
        Log.d(TAG, getCompleteInfo());
    }
}
