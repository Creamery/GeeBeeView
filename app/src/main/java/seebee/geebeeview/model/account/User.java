package seebee.geebeeview.model.account;

/**
 * The User class represents a user
 * containing information about the user
 * and their level of authorization.
 *
 * @author Stephanie
 * @since 5/25/2017
 */
public class User {
    /**
     * Used to identify the source of a log message
     */
    public final static String TAG = "User";
    /**
     * Database table name for User.
     */
    public final static String TABLE_NAME = "tbl_user";
    /**
     * Database column name for storing {@link #userID}.
     */
    public final static String C_USER_ID = "user_id";
    /**
     * Database column name for storing {@link #username};
     */
    public final static String C_USERNAME = "username";
    /**
     * Datebase column name for storing {@link #password};
     */
    public final static String C_PASSWORD = "password";
    /**
     * Database column name for storing {@link #accessLvl};
     */
    public final static String C_ACCESS = "access_lvl";
    /* ID of the User */
    private int userID;
    /* username of the user for his/her GetBetter Account */
    private String username;
    /* password of the user for his/her GetBetter Account */
    private String password;
    /* access level of the user */
    private int accessLvl;

    /** Constructor
     * @param {@link #userID];
     * @param {@link #username};
     * @param {@link #password};
     * @param {@link #accessLvl];
    */
    public User (int userID, String username, String password, int accessLvl) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.accessLvl = accessLvl;
    }

    public User (String username, String password, int accessLvl) {
        this.username = username;
        this.password = password;
        this.accessLvl = accessLvl;
    }

    

    /**
     * Gets {@link #username}.
     * @return {@link #username}
     */
    public String getUsername() {
        return username;
    }
    /**
     * Gets {@link #password}.
     * @return {@link #password}
     */
    public String getPassword() {
        return password;
    }
    /**
     * Gets {@link #accessLvl}.
     * @return {@link #accessLvl}
     */
    public int getAccessLvl() {
        return accessLvl;
    }
}