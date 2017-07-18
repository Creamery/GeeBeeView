package seebee.geebeeview.layout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.account.User;

public class MainActivity extends AppCompatActivity {

    protected final String TAG = "Main Activity";

    private TextView title, tvUsername, tvPassword;
    private EditText etUsername, etPassword;
    private Button btnLogin, btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // lock orientation of activity to landscape
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // connect with views from layout
        title = (TextView) findViewById(R.id.main_title);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvPassword = (TextView) findViewById(R.id.tv_password);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        // get fonts from assets
        Typeface chawpFont = Typeface.createFromAsset(getAssets(), "font/chawp.ttf");
        Typeface rudimentFont = Typeface.createFromAsset(getAssets(), "font/rudiment.ttf");
        // set font to text views
        title.setTypeface(chawpFont);
        tvUsername.setTypeface(rudimentFont);
        tvPassword.setTypeface(rudimentFont);
        btnLogin.setTypeface(chawpFont);
        btnSignUp.setTypeface(chawpFont);

        /* set up database */
        checkDatabase();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                int val = -1;
                if(username != null && password != null) {
                    User user = new User(val, username, password, val);
                    saveUserToDatabase(user);
                    /* if failed, prompt the user again
                     * if success give message
                     */
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if(username != null && password != null) {
                    int val = -1;
                    User user = new User(val, username, password, val);
                    val = checkUserFromDatabase(user);
                    Log.v("Main Activity", "Access Level = "+val);
                    if(val > 0) {
                        Intent intent = new Intent(view.getContext(), ViewDatasetListActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    /**
     * Checks if database exists
     */
    private void checkDatabase() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(MainActivity.this);
        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            finish(); //exit app if database creation fails
        }
        getBetterDb.closeDatabase();
    }

    /**
     * Saves user to database.
     * @param user to be saved to the database
     */
    private void saveUserToDatabase(User user){
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* try to insert user to database */
        try{
            getBetterDb.insertUser(user);
        } catch (SQLiteConstraintException e){
            Toast.makeText(getBaseContext(), "Unable to insert user.", Toast.LENGTH_SHORT).show();
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
    }
    /**
     * Checks if user is in database.
     * @param user to be saved to the database
     * @return accessLvl
     */
    public int checkUserFromDatabase(User user) {
        int accessLvl = -1;
        User dbUser = null;
        DatabaseAdapter getBetterDb = new DatabaseAdapter(MainActivity.this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* try to get user from database */
        try{
            dbUser = getBetterDb.getUser(user.getUsername());
        } catch (SQLiteConstraintException e){
            e.printStackTrace();
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        /* check if user is allowed access */
        if(dbUser != null) {
            if (user.getUsername().contentEquals(dbUser.getUsername()) &&
                    user.getPassword().contentEquals(dbUser.getPassword())) {
                accessLvl = dbUser.getAccessLvl();
            }
        } else {
            Toast.makeText(getBaseContext(), "Unregistered user!", Toast.LENGTH_SHORT).show();
        }
        return accessLvl;
    }

}
