package jonly.warcardgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final String GET_FIRST_PLAYER_URL = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?isPlayerOne=1";
    private final String RESET_MESSAGE_URL = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?resetMessage=1";
    private String insert_message_url = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?message=";

    private int[] card_drawable_ids;
    private int selected_card;

    private boolean current_chatter;
    private boolean is_tie_breaker;
    private boolean is_first_player;
    private boolean current_turn;
    private boolean can_start;

    private String chat_history;
    private String username;

    private ImageView[] cards;

    private TableFragment tf;
    private MathFragment mf;

    private Deck current_hand;

    private Random rand;

    //scoreboard
    private TextView score_left;
    private TextView score_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current_turn = false;

        if (savedInstanceState == null) {
            //set up who goes first
            RequestQueue queue = Volley.newRequestQueue(getBaseContext());

            StringRequest string_request = new StringRequest(Request.Method.GET, GET_FIRST_PLAYER_URL, new Response.Listener<String>() {
                public void onResponse(String response) {
                    current_hand = new Deck();
                    char[] suit = {'c', 'd', 'h', 's'};

                    if (response.contains("Yes")) {
                        is_first_player = true;
                        current_turn = true;

                        int j = 1;
                        int limit = 14;

                        for (int i = 0; i < suit.length; i++) {
                            for (; j < limit; j+=2) { // 13 cards
                                String value = j == 10 ? "t" : j == 11 ? "j" :
                                        j == 12 ? "q" : j == 13 ? "k" : j + "";

                                Card card = new Card(getBaseContext(), j, suit[i]+value);
                                card.setImageResource(getResources()
                                        .getIdentifier(suit[i] + value, "drawable", getPackageName()));

                                current_hand.add(card);
                            }
                            if(j == 15) {
                                j = 2;
                                limit = 13;
                            }
                            else {
                                j = 1;
                                limit = 14;
                            }
                        }
                        score_left = (TextView) findViewById(R.id.score_left);
                        score_left.setText(current_hand.size() + "");
                    } else {
                        RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                        String message = username + "|Player2-has-logged-on!".trim();

                        StringRequest string_request = new StringRequest(Request.Method.GET, insert_message_url + message, new Response.Listener<String>() {
                            public void onResponse(String response) {

                            }

                        }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError er) {

                            }
                        });

                        int j = 2;
                        int limit = 13;

                        for (int i = 0; i < suit.length; i++) {
                            for (; j < limit; j+=2) { // 13 cards
                                String value = j == 10 ? "t" : j == 11 ? "j" :
                                        j == 12 ? "q" : j == 13 ? "k" : j + "";

                                Card card = new Card(getBaseContext(), j, suit[i]+value);
                                card.setImageResource(getResources()
                                        .getIdentifier(suit[i] + value, "drawable", getPackageName()));

                                current_hand.add(card);
                            }
                            if(j == 14) {
                                j = 1;
                                limit = 14;
                            }
                            else {
                                j = 2;
                                limit = 13;
                            }
                        }
                        score_right = (TextView) findViewById(R.id.score_right);
                        score_right.setText(current_hand.size() + "");
                        queue.add(string_request);
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError er) {

                }
            });
            queue.add(string_request);
        }

        cards = new ImageView[3];
        card_drawable_ids = new int[3];
        selected_card = -1;
        current_chatter = false;
        is_tie_breaker = false;
        chat_history = "";
        username = getIntent().getStringExtra("USERNAME");
        rand = new Random();

        cards[0] = findViewById(R.id.card1);
        cards[1] = findViewById(R.id.card2);
        cards[2] = findViewById(R.id.card3);

        if (savedInstanceState != null) {
            mf = (MathFragment) getSupportFragmentManager().getFragment(savedInstanceState, "Math_Fragment");
            tf = (TableFragment) getSupportFragmentManager().getFragment(savedInstanceState, "Table_Fragment");

            card_drawable_ids[0] = savedInstanceState.getInt("Card_1");
            card_drawable_ids[1] = savedInstanceState.getInt("Card_2");
            card_drawable_ids[2] = savedInstanceState.getInt("Card_3");

            cards[0].setImageResource(savedInstanceState.getInt("Card_1"));
            cards[1].setImageResource(savedInstanceState.getInt("Card_2"));
            cards[2].setImageResource(savedInstanceState.getInt("Card_3"));

            selected_card = savedInstanceState.getInt("Selected_Card");
            current_chatter = savedInstanceState.getBoolean("Current_Chatter");

            chat_history = savedInstanceState.getString("Chat_History");

            if (selected_card != -1)
                cards[savedInstanceState.getInt("Selected_Card")].setBackgroundResource(R.drawable.image_border);

            LinearLayout linearLayout = findViewById(R.id.chatBox);

            if (linearLayout != null) {
                String[] chat_parts = chat_history.split("\n");
                for (String s : chat_parts) {
                    TextView tv = new TextView(this);
                    tv.setText(s);
                    linearLayout.addView(tv, 0);
                }
            }

        } else {
            mf = new MathFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            tf = new TableFragment();
            ft.add(R.id.main_layout, tf);
            mf = new MathFragment();
            ft.add(R.id.main_layout, mf);
            ft.hide(mf);
            ft.commit();

            startMultiplayerGame();
        }
    }

    public void startMultiplayerGame() {
        //register the receiver to listen for broadcasts
        IntentFilter intentFilter = new IntentFilter("confirm_multiplayer");
        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String temp = intent.getStringExtra("CONFIRMATION");
                System.out.println(temp);
                if (temp.contains("go")) {
                    can_start = true;
                }
                System.out.println("checking");

                if (can_start) {
                    System.out.println("STARTING!!!");
                    startChat();

                    if(is_first_player){
                        score_right = (TextView) findViewById(R.id.score_right);
                        score_right.setText(current_hand.size() + "");
                    }else{
                        score_left = (TextView) findViewById(R.id.score_left);
                        score_left.setText(current_hand.size() + "");
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

        //start the service
        Intent intent = new Intent(this, MultiplayerService.class);
        startService(intent);
    }

    public void startChat() {
        //register the receiver to listen for broadcasts
        IntentFilter intentFilter = new IntentFilter("chatIntent");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String temp = intent.getStringExtra("NEW_MESSAGE");

                String[] parts = temp.split("|");

                if (!temp.trim().equals("") && !parts[0].contains(username)) {

                    temp = parts[0] + ": " + parts[1];

                    current_chatter = false;

                    LinearLayout linearLayout = findViewById(R.id.chatBox);
                    TextView text = new TextView(getBaseContext());
                    text.append(temp);
                    linearLayout.addView(text, 0);

                    RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                    StringRequest string_request = new StringRequest(Request.Method.GET, RESET_MESSAGE_URL, new Response.Listener<String>() {
                        public void onResponse(String response) {
                            // do nothing
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError er) {
                            //do nothing
                        }
                    });
                    queue.add(string_request);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

        //start the service
        Intent intent = new Intent(this, ChatService.class);
        startService(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("Card_1", card_drawable_ids[0]);
        savedInstanceState.putInt("Card_2", card_drawable_ids[1]);
        savedInstanceState.putInt("Card_3", card_drawable_ids[2]);
        savedInstanceState.putInt("Selected_Card", selected_card);
        savedInstanceState.putBoolean("Current_Chatter", current_chatter);
        savedInstanceState.putBoolean("Tie_Breaker", is_tie_breaker);
        savedInstanceState.putString("Chat_History", chat_history);
        getSupportFragmentManager().putFragment(savedInstanceState, "Math_Fragment", mf);
        getSupportFragmentManager().putFragment(savedInstanceState, "Table_Fragment", tf);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        final String RESET_GAME_URL = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?resetGame=1";

        RequestQueue queue = Volley.newRequestQueue(getBaseContext());

        StringRequest string_request = new StringRequest(Request.Method.GET, RESET_GAME_URL, new Response.Listener<String>() {
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError er) {

            }
        });

        queue.add(string_request);

        this.finish();
    }

    public void selectCard(View view) {
        if (!is_tie_breaker) {
            int index = -1; // index of the card selected (0-2)

            for (int i = 0; i < cards.length; i++) {
                if (cards[i].getId() == view.getId()) {
                    index = i;
                    break;
                }
            }

            if (index != selected_card && selected_card != -1)
                cards[selected_card].setBackground(null);

            selected_card = index;

            if (view.getBackground() == null) {
                view.setBackgroundResource(R.drawable.image_border);
            } else {
//                if (tf.getLeftDrawable() == 0) {
//                    tf.setCardLeftDrawable(card_drawable_ids[index]);
//                } else if (tf.getRightDrawable() == 0) {
//                    tf.setCardRightDrawable(card_drawable_ids[index]);
//                }
                selected_card = -1;
//                card_drawable_ids[index] = randomCard();
                ((ImageView) view).setImageResource(card_drawable_ids[index]);
                view.setBackground(null);
            }
        }
    }

    public void submitChat(View view) {
        if(can_start) {
            EditText chatInput = findViewById(R.id.chatInput);

            String message = chatInput.getText().toString().trim();

            message = username + "|" + message;

            RequestQueue queue = Volley.newRequestQueue(getBaseContext());

            StringRequest string_request = new StringRequest(Request.Method.GET, insert_message_url + message, new Response.Listener<String>() {
                public void onResponse(String response) {
                    System.out.println(response);
                }

            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError er) {

                }
            });

            queue.add(string_request);

            if (!message.equals("")) {
                //            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //            if (message.toLowerCase().equals("math") && !is_tie_breaker) {
                //                ft.hide(tf);
                //                ft.show(mf);
                //                is_tie_breaker = true;
                //            } else if (message.toLowerCase().equals("table") && is_tie_breaker) {
                //                ft.hide(mf);
                //                ft.show(tf);
                //                is_tie_breaker = false;
                //            }

                if (!current_chatter) {
                    message = username + ": " + message;
                    current_chatter = true;
                }
                LinearLayout linearLayout = findViewById(R.id.chatBox);
                TextView text = new TextView(this);
                text.append(message);
                linearLayout.addView(text, 0);
                chatInput.setText("");
                chat_history += message + "\n";
                //            ft.commit();
            }
        }
    }

    public int RandomCard() {
        return 0;
    }
}
