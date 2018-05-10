package jonly.warcardgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
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
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final String GET_FIRST_PLAYER_URL = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?isPlayerOne=1";
    private final String RESET_MESSAGE_URL = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?resetMessage=1";
    private final String RESET_GAME_URL = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?resetGame=1";
    private final String RESET_CARDS_URL = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?resetCards=1";
    private String insert_message_url = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?message=";

    private int[] card_drawable_ids;
    private int selected_card;

    private boolean current_chatter;
    private boolean is_tie_breaker;
    private boolean is_first_player;
    private boolean current_turn;
    private boolean can_start;
    private boolean game_started;
    private boolean has_selected_card;
    private boolean is_checking;

    private String chat_history;
    private String username;

    private ImageView[] cards;

    private TableFragment tf;
    private MathFragment mf;

    private Deck player1;
    private Deck player2;
    private ArrayList<Card> card_pool;

    private Random rand;

    private Random random_cards;

    //scoreboard
    private TextView score_left;
    private TextView score_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current_turn = false;
        game_started = false;
        has_selected_card = false;
        is_checking = false;

        if (savedInstanceState == null) {
            //set up who goes first
            RequestQueue queue = Volley.newRequestQueue(getBaseContext());

            char[] suit = {'c', 'd', 'h', 's'};

            card_pool = new ArrayList<Card>();

            for (int i = 0; i < suit.length; i++) {
                for (int j = 1; j < 14; j++) {
                    String value = j == 10 ? "t" : j == 11 ? "j" :
                            j == 12 ? "q" : j == 13 ? "k" : j + "";

                    Card card = new Card(getBaseContext(), j, suit[i] + value, getResources()
                            .getIdentifier(suit[i] + value, "drawable", getPackageName()));
                    card.setImageResource(getResources()
                            .getIdentifier(suit[i] + value, "drawable", getPackageName()));

                    card_pool.add(card);
                }
            }

            player1 = new Deck();
            player2 = new Deck();

            for (int i = 0; i < card_pool.size(); i += 2) {
                player1.add(card_pool.get(i));
                player2.add(card_pool.get(i + 1));
            }

            StringRequest string_request = new StringRequest(Request.Method.GET, GET_FIRST_PLAYER_URL, new Response.Listener<String>() {
                public void onResponse(String response) {

                    if (response.contains("Yes")) {
                        is_first_player = true;
                        current_turn = true;
                        score_left = (TextView) findViewById(R.id.score_left);
                        score_left.setText(player1.size() + "");
                    } else {
                        RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                        String message = username + "=Player2-has-logged-on!".trim();

                        StringRequest string_request = new StringRequest(Request.Method.GET, insert_message_url + message, new Response.Listener<String>() {
                            public void onResponse(String response) {

                            }

                        }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError er) {

                            }
                        });
                        score_right = (TextView) findViewById(R.id.score_right);
                        score_right.setText(player2.size() + "");
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
        random_cards = new Random(122);

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

            startGame();
        }
    }

    public void startGame() {
        //register the receiver to listen for broadcasts
        IntentFilter intentFilter = new IntentFilter("chatIntent");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /* Cards are in this format: name + value (ex: ct10).
                   Messages are in this format: username + | + message.
                   Math completed is to tell who won.
                   The last 2 bits are to tell if both players have connected.
                   [0, 0, "Message", "Username", 0, 0]
                 */

                //Player1Card, Player2Card,
                String temp = intent.getStringExtra("NEW_MESSAGE");

                String[] parts = temp.split("~");

                if (Integer.parseInt(parts[4].trim()) == 1 && Integer.parseInt(parts[5].trim()) == 1
                        && !can_start) {
                    can_start = true;

                    RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                    StringRequest string_request = new StringRequest(Request.Method.GET, RESET_GAME_URL, new Response.Listener<String>() {
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

                if (can_start && !game_started) {
                    if(cards == null) {
                        cards = new ImageView[3];
                        cards[0] = findViewById(R.id.card1);
                        cards[1] = findViewById(R.id.card2);
                        cards[2] = findViewById(R.id.card3);
                    }
                    if (is_first_player) {
                        score_right = (TextView) findViewById(R.id.score_right);
                        score_right.setText(player2.size() + "");
                        card_drawable_ids[0] = randomCard();
                        cards[0].setImageResource(card_drawable_ids[0]);
                        removeCard(card_drawable_ids[0]);
                        card_drawable_ids[1] = randomCard();
                        cards[1].setImageResource(card_drawable_ids[1]);
                        removeCard(card_drawable_ids[0]);
                        card_drawable_ids[2] = randomCard();
                        cards[2].setImageResource(card_drawable_ids[2]);
                        removeCard(card_drawable_ids[0]);
                    } else {
                        score_left = (TextView) findViewById(R.id.score_left);
                        score_left.setText(player1.size() + "");
                        card_drawable_ids[0] = randomCard();
                        cards[0].setImageResource(card_drawable_ids[0]);
                        removeCard(card_drawable_ids[0]);

                        card_drawable_ids[1] = randomCard();
                        cards[1].setImageResource(card_drawable_ids[1]);
                        removeCard(card_drawable_ids[1]);

                        card_drawable_ids[2] = randomCard();
                        cards[2].setImageResource(card_drawable_ids[2]);
                        removeCard(card_drawable_ids[2]);
                    }
                    game_started = true;
                }

                if (game_started) {
                    String player1_card = parts[0];
                    String player2_card = parts[1];
                    String[] message = parts[2].split("=");
                    String won_math = parts[3];

                    boolean check_cards_played = player1_card.length() > 1 && player2_card.length() > 1;

                    if (check_cards_played && !is_checking) { //determine who won or tie
                        is_checking = true;
                        if(is_first_player){
                            ((ImageView) findViewById(R.id.played_card_right)).setImageResource(
                                    getResources().getIdentifier(player2_card.substring(0, 2), "drawable", getPackageName())
                            );
                        }else{
                            ((ImageView) findViewById(R.id.played_card_right)).setImageResource(
                                    getResources().getIdentifier(player1_card.substring(0, 2), "drawable", getPackageName())
                            );
                        }
                        checkTableState(player1_card, player2_card);
                    }

                    if (!won_math.equals("")) { //loss

                        Card card1, card2, card3;

                        if (!is_first_player && won_math.equals(username)) {//p2 wins
                            int rand_card1 = random_cards.nextInt(player1.size());
                            card1 = player1.get(rand_card1);
                            player1.remove(rand_card1);
                            int rand_card2 = random_cards.nextInt(player1.size());
                            card2 = player1.get(rand_card2);
                            player1.remove(rand_card2);
                            int rand_card3 = random_cards.nextInt(player1.size());
                            card3 = player1.get(rand_card3);
                            player1.remove(rand_card3);

                            player2.add(card1);
                            player2.add(card2);
                            player2.add(card3);
                        } else { //p1 wins
                            int rand_card1 = random_cards.nextInt(player2.size());
                            card1 = player2.get(rand_card1);
                            player2.remove(rand_card1);
                            int rand_card2 = random_cards.nextInt(player2.size());
                            card2 = player2.get(rand_card2);
                            player2.remove(rand_card2);
                            int rand_card3 = random_cards.nextInt(player2.size());
                            card3 = player2.get(rand_card3);
                            player2.remove(rand_card3);

                            player1.add(card1);
                            player1.add(card2);
                            player1.add(card3);
                        }

                        has_selected_card = false;

                        score_left.setText(player1.size());
                        score_right.setText(player2.size());

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.hide(mf);
                        ft.show(tf);
                        is_tie_breaker = true;
                        ft.commit();

                        mf.resetCounter();
                    }

                    if (player1.size() >= 52) {
                        resetGame(1);
                    }
                    if (player2.size() >= 52) {
                        resetGame(2);
                    }

                    if (!message[0].trim().equals("") && !message[0].contains(username)) {

                        temp = message[0] + ": " + message[1];

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
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);

        //start the service
        Intent intent = new Intent(this, ChatService.class);
        startService(intent);
    }

    public void resetGame(int player){

        String message = "";

        if(is_first_player && player == 1){
            message = "Player 1 wins!";
        } else{
            message = "Player 2 wins!";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle("Auto-closing Dialog");
        builder.setMessage(message);
        builder.setCancelable(true);

        final AlertDialog dlg = builder.create();

        dlg.show();

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss(); // when the task active then close the dialog
                RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                StringRequest string_request = new StringRequest(Request.Method.GET, RESET_GAME_URL, new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError er) {
                        //do nothing
                    }
                });
                queue.add(string_request);
                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, 2000); // after 2 second (or 2000 milliseconds), the task will be active.
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

    public void checkTableState(String player1_card, String player2_card) {

        int player1_card_value = Integer.parseInt(player1_card.substring(2));
        int player2_card_value = Integer.parseInt(player2_card.substring(2));

        String label1 = player1_card.charAt(0) + "" + player1_card.charAt(1);
        String label2 = player2_card.charAt(0) + "" + player2_card.charAt(1);

        if (player1_card_value > player2_card_value) { //p1 wins
            for (int i = 0; i < player2.size(); i++) {
                if (player2.get(i).getLabel().equals(label2)) {
                    player1.add(player2.get(i));
                    player2.remove(i);
                    break;
                }
            }

            final Timer t = new Timer();
            TimerTask task = new TimerTask(){
                @Override
                public void run(){
                    RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                    StringRequest string_request = new StringRequest(Request.Method.GET, RESET_CARDS_URL, new Response.Listener<String>() {
                        public void onResponse(String response) {
                            // do nothing
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError er) {
                            //do nothing
                        }
                    });
                    queue.add(string_request);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.score_left)).setText(player1.size()+"");
                            ((TextView)findViewById(R.id.score_right)).setText(player2.size()+"");
                            has_selected_card = false;
                            ((ImageView) findViewById(R.id.played_card_right)).setImageResource(android.R.color.transparent);
                            ((ImageView) findViewById(R.id.played_card_left)).setImageResource(android.R.color.transparent);
                            t.cancel();
                            is_checking = false;
                        }
                    });
                }
            };

            t.schedule(task, 2000, 2000);

        } else if (player1_card_value < player2_card_value) { //p2 wins
            for (int i = 0; i < player1.size(); i++) {
                if (player1.get(i).getLabel().equals(label1)) {
                    player2.add(player1.get(i));
                    player1.remove(i);
                    break;
                }
            }
            final Timer t = new Timer();
            TimerTask task = new TimerTask(){
                @Override
                public void run(){
                    RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                    StringRequest string_request = new StringRequest(Request.Method.GET, RESET_CARDS_URL, new Response.Listener<String>() {
                        public void onResponse(String response) {
                            // do nothing
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError er) {
                            //do nothing
                        }
                    });
                    queue.add(string_request);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.score_left)).setText(player1.size()+"");
                            ((TextView)findViewById(R.id.score_right)).setText(player2.size()+"");
                            has_selected_card = false;
                            ((ImageView) findViewById(R.id.played_card_right)).setImageResource(android.R.color.transparent);
                            ((ImageView) findViewById(R.id.played_card_left)).setImageResource(android.R.color.transparent);
                            t.cancel();
                            is_checking = false;
                        }
                    });
                }
            };

            t.schedule(task, 2000, 2000);
        } else { //tie
            Toast.makeText(getBaseContext(), "MATH CHALLENGE INCOMING!!", Toast.LENGTH_SHORT).show();

            final Timer t = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.hide(tf);
                    ft.show(mf);
                    is_tie_breaker = true;
                    ft.commit();
                    t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                }
            };
            t.schedule(task, 2000, 2000); // after 2 second (or 2000 miliseconds), the task will be active.
        }
    }

    public void selectCard(View view) {
        if (!is_tie_breaker && !has_selected_card) {
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

            String card_label_value = "";

            if (view.getBackground() == null) {
                view.setBackgroundResource(R.drawable.image_border);
            } else {
                tf.setCardLeftDrawable(card_drawable_ids[index]);
                card_label_value = getCardLabelValue(card_drawable_ids[index]);
                selected_card = -1;
                card_drawable_ids[index] = randomCard();
                if(is_first_player) {
                    ((ImageView) view).setImageResource(card_drawable_ids[index]);
                    removeCard(card_drawable_ids[index]);
                    String insert_player1_card_url = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?insertPlayerOneCard=" + card_label_value;

                    RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                    StringRequest string_request = new StringRequest(Request.Method.GET, insert_player1_card_url, new Response.Listener<String>() {
                        public void onResponse(String response) {

                        }

                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError er) {

                        }
                    });

                    queue.add(string_request);
                }else{
                    String insert_player2_card_url = "http://webdev.cs.uwosh.edu/students/lyj47/labProcedures.php?insertPlayerTwoCard=" + card_label_value;
                    ((ImageView) view).setImageResource(card_drawable_ids[index]);
                    removeCard(card_drawable_ids[index]);

                    RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                    StringRequest string_request = new StringRequest(Request.Method.GET, insert_player2_card_url, new Response.Listener<String>() {
                        public void onResponse(String response) {

                        }

                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError er) {

                        }
                    });

                    queue.add(string_request);
                }
                view.setBackground(null);

                has_selected_card = true;
            }
        }
    }

    public void submitChat(View view) {
        if (can_start) {
            EditText chatInput = findViewById(R.id.chatInput);

            String message = chatInput.getText().toString().trim();

            message = username + "=" + message;

            RequestQueue queue = Volley.newRequestQueue(getBaseContext());

            StringRequest string_request = new StringRequest(Request.Method.GET, insert_message_url + message, new Response.Listener<String>() {
                public void onResponse(String response) {

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
                    message = username + ": " + message.split("=")[0];
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

    public String getCardLabelValue(int id){
        for(int i = 0; i < card_pool.size(); i++){
            if(id == card_pool.get(i).getId())
                return card_pool.get(i).getLabel() + card_pool.get(i).getValue();
        }

        return "";
    }

    public int randomCard() {
        if(is_first_player) {
            return player1.get(rand.nextInt(player1.size())).getId();
        }else{
            return player2.get(rand.nextInt(player2.size())).getId();
        }
    }

    public void removeCard(int id){
        if(is_first_player){
            for(int i = 0; i < player1.size(); i++){
                if(id == player1.get(i).getId()){
                    player1.remove(i);
                    break;
                }
            }
        }else{
            for(int i = 0; i < player2.size(); i++){
                if(id == player2.get(i).getId()){
                    player2.remove(i);
                    break;
                }
            }
        }
    }
}
