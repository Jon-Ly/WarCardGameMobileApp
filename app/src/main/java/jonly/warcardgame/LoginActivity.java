package jonly.warcardgame;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.regex.Pattern;

/**
 * Created by wintow on 4/9/2018.
 */

public class LoginActivity extends AppCompatActivity{

    // Database Variables
    private GameHelperDb helperDb;
    private SQLiteDatabase db;
    private ContentValues values;
    private Cursor cursor;
    private static int currentID;

    private String username;

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

                final String username = username_field.getText().toString();
                final String password = password_field.getText().toString();

                if(registerCheckbox.isChecked()) {
                    //registering
                    String query = "SELECT * FROM " + GameContract.DBEntry.TABLE_NAME;

                    cursor = db.rawQuery(query, null);

                    boolean duplicate = false;

                    while(cursor.moveToNext()){
                        String db_username = cursor.getString(1);

                        if(username.equals(db_username)){
                            duplicate = true;
                            break;
                        }
                    }

                    if(duplicate)
                        Toast.makeText(getBaseContext(), "That username has been taken already.", Toast.LENGTH_SHORT).show();
                    else if(username.length() < 4){
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
                        db.insertWithOnConflict(GameContract.DBEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                        Intent gameIntent = new Intent(LoginActivity.this, MainActivity.class);
                        gameIntent.putExtra("USERNAME", username);
                        startActivity(gameIntent);
                    }
                } else{
                    //Login
                    String query = "SELECT * FROM " + GameContract.DBEntry.TABLE_NAME;

                    Cursor cursor = db.rawQuery(query, null);

                    boolean successful = false, isIncorrectUsername = true;

                    while(cursor.moveToNext()){
                        String db_username = cursor.getString(1);
                        String db_password = cursor.getString(2);

                        if(username.equals(db_username)){
                            if(password.equals(db_password)){
                                successful = true;
                                break;
                            }
                            Toast.makeText(getBaseContext(), "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            isIncorrectUsername = false;
                            break;
                        }
                    }

                    if(!successful && isIncorrectUsername) {
                        Toast.makeText(getBaseContext(), "Your username was not found. Try registering.", Toast.LENGTH_SHORT).show();
                    } else if(successful){
                        String url = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?getEverything=1";

                        RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                        StringRequest string_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                            public void onResponse(String response) {
                                String cleaned_response = "";
                                for (int i = 0; i < response.length(); i++) {
                                    if (response.charAt(i) != '[' && response.charAt(i) != ']' &&
                                            response.charAt(i) != '"') {
                                        if (response.charAt(i) == ',')
                                            cleaned_response += "~";
                                        else
                                            cleaned_response += response.charAt(i);
                                    }
                                }

                                String[] parts = cleaned_response.split("~");

                                System.out.println(cleaned_response + " | " + parts[4] + " | " + parts[5]);

                                if(!parts[4].equals("1") || !parts[5].equals("1")) {
                                    Intent gameIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    gameIntent.putExtra("USERNAME", username);
                                    startActivity(gameIntent);
                                }else{
                                    Toast.makeText(getBaseContext(), "There are two players already, please wait your turn", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError er) {

                            }
                        });
                        queue.add(string_request);
                    }
                }
            }
        });
    }
}
