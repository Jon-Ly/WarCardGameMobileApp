package jonly.warcardgame;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

public class ChatService extends IntentService {

    private RequestQueue queue;
    private Intent intent;
    private String input;

    public ChatService() {
        super("ChatService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        this.intent = intent;
        this.input = "";

        if (intent != null) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    getMessages();
                }
            };

            timer.schedule(task, 2000, 2000);
        }
    }

    private void getMessages() {
        String url = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?getMessage=1";

        queue = Volley.newRequestQueue(getBaseContext());

        StringRequest string_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Intent local = new Intent("chatIntent");
                local.putExtra("NEW_MESSAGE", response);
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
