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

    private static int[] card_drawable_ids;
    private static int selected_card;

    private static boolean current_chatter;
    private static boolean is_tie_breaker;

    private static TableFragment tf;

    private static ImageView[] cards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cards = new ImageView[5];
        card_drawable_ids = new int[5];
        selected_card = -1;
        current_chatter = false;
        is_tie_breaker = false;

        cards[0] = findViewById(R.id.card1);
        cards[1] = findViewById(R.id.card2);
        cards[2] = findViewById(R.id.card3);

        if(savedInstanceState != null){
            if(savedInstanceState.getBoolean("Is_Math_Frag")){
                getSupportFragmentManager().beginTransaction().add(R.id.main_layout, new MathFragment()).commit();
            }else{
                getSupportFragmentManager().beginTransaction().add(R.id.main_layout, new TableFragment()).commit();
            }

            card_drawable_ids[0] = savedInstanceState.getInt("Card_1");
            card_drawable_ids[1] = savedInstanceState.getInt("Card_2");
            card_drawable_ids[2] = savedInstanceState.getInt("Card_3");
            card_drawable_ids[3] = savedInstanceState.getInt("Card_4");
            card_drawable_ids[4] = savedInstanceState.getInt("Card_5");

            cards[0].setImageResource(savedInstanceState.getInt("Card_1"));
            cards[1].setImageResource(savedInstanceState.getInt("Card_2"));
            cards[2].setImageResource(savedInstanceState.getInt("Card_3"));

            selected_card = savedInstanceState.getInt("Selected_Card");
            current_chatter = savedInstanceState.getBoolean("Current_Chatter");

            if(selected_card != -1)
                cards[savedInstanceState.getInt("Selected_Card")].setBackgroundResource(R.drawable.image_border);
        }else {
            card_drawable_ids[0] = randomCard();
            card_drawable_ids[1] = randomCard();
            card_drawable_ids[2] = randomCard();

            cards[0].setImageResource(card_drawable_ids[0]);
            cards[1].setImageResource(card_drawable_ids[1]);
            cards[2].setImageResource(card_drawable_ids[2]);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            tf = new TableFragment();
            ft.add(R.id.main_layout, tf);
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        boolean is_math_frag = false;

        savedInstanceState.putInt("Card_1", card_drawable_ids[0]);
        savedInstanceState.putInt("Card_2", card_drawable_ids[1]);
        savedInstanceState.putInt("Card_3", card_drawable_ids[2]);
        savedInstanceState.putInt("Card_Left", card_drawable_ids[3]);
        savedInstanceState.putInt("Card_Right", card_drawable_ids[4]);
        savedInstanceState.putInt("Selected_Card", selected_card);
        savedInstanceState.putBoolean("Current_Chatter", current_chatter);
        savedInstanceState.putBoolean("Tie_Breaker", is_tie_breaker);

        if(findViewById(R.id.math_fragment) != null)
            is_math_frag = true;

        savedInstanceState.putBoolean("Is_Math_Frag", is_math_frag);

        //save chat state
        LinearLayout linearLayout = findViewById(R.id.chatBox);
        if(linearLayout != null) {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                View view = linearLayout.getChildAt(i);
                if (view instanceof TextView) {
                    savedInstanceState.putString("Chat_" + i, ((TextView) view).getText().toString());
                }
            }
        }
    }

    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();
    }

    public void selectCard(View view){
        if(!is_tie_breaker) {
            int index = -1; // index of the card selected (0-2)

            if (cards[3] == null || cards[4] == null) { //fragment was not set, set the cards.
                cards[3] = findViewById(R.id.played_card_left);
                cards[4] = findViewById(R.id.played_card_right);
            }

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
                if (card_drawable_ids[3] != 0 && card_drawable_ids[4] == 0) {
                    cards[4].setImageResource(card_drawable_ids[index]);
                    card_drawable_ids[4] = card_drawable_ids[index];
                } else if (card_drawable_ids[3] == 0) {
                    cards[3].setImageResource(card_drawable_ids[index]);
                    card_drawable_ids[3] = card_drawable_ids[index];
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
            if(message.equals("math") && !is_tie_breaker){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(tf.getId(), new MathFragment());
                ft.remove(tf);
                ft.commit();
                is_tie_breaker = true;
            }

            if(!current_chatter) {
                message = getIntent().getExtras().getString("USERNAME") + ": " + message;
                current_chatter = true;
            }
            LinearLayout linearLayout = findViewById(R.id.chatBox);
            TextView text = new TextView(this);
            text.append(message);
            linearLayout.addView(text, 0);
            chatInput.setText("");
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
