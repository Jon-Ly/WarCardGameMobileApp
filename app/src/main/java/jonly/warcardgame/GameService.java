package jonly.warcardgame;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class GameService extends IntentService {

    private static int i = 0;

    public GameService(){
        super("GameService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        if(intent != null)
            doSomething();
    }

    private void doSomething(){
        while(true){
            long value = 0;
            long start = System.currentTimeMillis();
            while(start+1000>System.currentTimeMillis()){
                value++;
            }
            Intent intent = new Intent("sendLongData");
            intent.putExtra("valueData", value);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
