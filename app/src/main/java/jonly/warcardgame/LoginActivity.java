package jonly.warcardgame;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by wintow on 4/9/2018.
 */

public class LoginActivity extends AppCompatActivity{

    // Database Variables
    private GameHelperDb helperDb;
    private SQLiteDatabase db;
    ContentValues values;
    private static int currentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        helperDb = new GameHelperDb(getApplicationContext());
        db = helperDb.getWritableDatabase();
        values = new ContentValues();
        currentID = 0;

        final Button loginButton = (Button)findViewById(R.id.login_button);
        final CheckBox registerCheckbox = (CheckBox) findViewById(R.id.register_checkbox);

        registerCheckbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loginButton.setText(registerCheckbox.isChecked() ? "Register": "Login");
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username_field = (EditText) findViewById(R.id.username_field);
                EditText password_field = (EditText) findViewById(R.id.password_field);

                String username = username_field.getText().toString();
                String password = password_field.getText().toString();

                if(registerCheckbox.isChecked()) {
                    //registering

                    //TODO: Add Toast(s) for: duplicate usernames

                    if(username.length() < 4){
                        // 4 character long check
                        Toast.makeText(getBaseContext(), "Username must be at least 4 characters long", Toast.LENGTH_SHORT).show();
                    }else if(!Pattern.compile(".*(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d].*").matcher(password).matches()) {
                        // password check
                        Toast.makeText(getBaseContext(), "Password must contain 1 uppercase, 1 lowercase, and 1 number", Toast.LENGTH_LONG).show();
                    }else{
                        //successful register
                        Toast.makeText(getBaseContext(), "You're registered!", Toast.LENGTH_SHORT).show();

                        values.put(GameContract.DBEntry._ID, currentID++);
                        values.put(GameContract.DBEntry.COLUMN_USERNAME, username);
                        values.put(GameContract.DBEntry.COLUMN_PASSWORD, password);
                        db.insert(GameContract.DBEntry.TABLE_NAME, null, values);
                    }
                } else{
                    SQLiteDatabase readDb = helperDb.getReadableDatabase();

                    String query = "SELECT * FROM " + GameContract.DBEntry.TABLE_NAME + " WHERE" +
                            GameContract.DBEntry.COLUMN_USERNAME + " = " + username +
                            " AND " + GameContract.DBEntry.COLUMN_PASSWORD + " = " + password;

                    Cursor cursor = db.rawQuery(query, null);

                    //TODO: Set the username to a variable.
                    //TODO: Add Toast(s) for: wrong password, no username exists

                    if(cursor.getCount() > 0) {//successful login
                        Intent gameIntent = new Intent(LoginActivity.this, MainActivity.class);
                        gameIntent.putExtra(username, "USERNAME");
                        startActivity(gameIntent);
                    }else{
                        Toast.makeText(getBaseContext(), "Credientials were not found in our records.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
