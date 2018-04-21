package jonly.warcardgame;

import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int[] card_drawable_ids;
    private int selected_card;

    private boolean current_chatter;
    private boolean is_tie_breaker;

    private String chat_history;
    private String username;

    private ImageView[] cards;

    private TableFragment tf;
    private MathFragment mf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cards = new ImageView[3];
        card_drawable_ids = new int[3];
        selected_card = -1;
        current_chatter = false;
        is_tie_breaker = false;
        chat_history = "";
        username = getIntent().getStringExtra("USERNAME");

        cards[0] = findViewById(R.id.card1);
        cards[1] = findViewById(R.id.card2);
        cards[2] = findViewById(R.id.card3);

        if(savedInstanceState != null){
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

            if(selected_card != -1)
                cards[savedInstanceState.getInt("Selected_Card")].setBackgroundResource(R.drawable.image_border);

            LinearLayout linearLayout = findViewById(R.id.chatBox);

            if(linearLayout != null){
                String[] chat_parts = chat_history.split("\n");
                for(String s : chat_parts){
                    TextView tv = new TextView(this);
                    tv.setText(s);
                    linearLayout.addView(tv, 0);
                }
            }

        }else {
            card_drawable_ids[0] = randomCard();
            card_drawable_ids[1] = randomCard();
            card_drawable_ids[2] = randomCard();

            cards[0].setImageResource(card_drawable_ids[0]);
            cards[1].setImageResource(card_drawable_ids[1]);
            cards[2].setImageResource(card_drawable_ids[2]);

            mf = new MathFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            tf = new TableFragment();
            ft.add(R.id.main_layout, tf);
            mf = new MathFragment();
            ft.add(R.id.main_layout, mf);
            ft.hide(mf);
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
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
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
    }

    public void selectCard(View view){
        if(!is_tie_breaker) {
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
                if (tf.getLeftDrawable() == 0) {
                    tf.setCardLeftDrawable(card_drawable_ids[index]);
                } else if (tf.getRightDrawable() == 0) {
                    tf.setCardRightDrawable(card_drawable_ids[index]);
                }
                selected_card = -1;
                card_drawable_ids[index] = randomCard();
                ((ImageView) view).setImageResource(card_drawable_ids[index]);
                view.setBackground(null);
            }
        }
    }

    public void submitChat(View view){

        EditText chatInput = findViewById(R.id.chatInput);

        String message = chatInput.getText().toString().trim();

        if(!message.equals("")) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(message.toLowerCase().equals("math") && !is_tie_breaker){
                ft.hide(tf);
                ft.show(mf);
                is_tie_breaker = true;
            }else if(message.toLowerCase().equals("table") && is_tie_breaker){
                ft.hide(mf);
                ft.show(tf);
                is_tie_breaker = false;
            }

            if(!current_chatter) {
                message = username + ": " + message;
                current_chatter = true;
            }
            LinearLayout linearLayout = findViewById(R.id.chatBox);
            TextView text = new TextView(this);
            text.append(message);
            linearLayout.addView(text, 0);
            chatInput.setText("");
            chat_history += message + "\n";
            ft.commit();
        }
    }

    public int randomCard(){
        Random rand = new Random();

        char[] suit = {'c', 'd', 'h', 's'};

        int cardValue = rand.nextInt(13)+1;

        String value = cardValue == 10 ? "j" : cardValue == 11 ? "k" :
                cardValue == 12 ? "q" : cardValue == 13 ? "t" : cardValue + "";

        return getResources().getIdentifier(suit[rand.nextInt(4)] + value, "drawable", getPackageName());
    }
}
