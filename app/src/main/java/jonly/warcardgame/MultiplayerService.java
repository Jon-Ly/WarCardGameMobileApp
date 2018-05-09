package jonly.warcardgame;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

public class MultiplayerService extends IntentService {

    private final String CHECK_URL = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?checkGame=1";

    public MultiplayerService(){
        super("MultiplayerService");
    }

    @Override
    public void onHandleIntent(Intent intent){
        if(intent != null){

            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    checkTwoPlayer();
                }
            };
            timer.schedule(task, 1250,1250);

            checkTwoPlayer();
        }
    }

    public void checkTwoPlayer(){

        RequestQueue queue = Volley.newRequestQueue(getBaseContext());

        StringRequest string_request = new StringRequest(Request.Method.GET, CHECK_URL, new Response.Listener<String>() {
            public void onResponse(String response) {
                Intent local = new Intent("confirm_multiplayer");
                local.putExtra("CONFIRMATION", response);
                LocalBroadcastManager.getInstance(getBaseContext()).
                        sendBroadcast(local);
            }

        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError er) {

            }
        });

        queue.add(string_request);
    }

}
