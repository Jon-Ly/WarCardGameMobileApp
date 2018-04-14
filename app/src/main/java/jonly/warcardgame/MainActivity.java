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
    private int selectedCard;

    private boolean current_chatter;
    private boolean isTieBreaker;

    private EditText editText;

    private TableFragment tf;

    private ImageView[] cards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onAttachedToWindow(){
        super.onAttachedToWindow();

        cards = new ImageView[5];
        card_drawable_ids = new int[5];
        selectedCard = -1;
        current_chatter = false;
        isTieBreaker = false;

        editText = findViewById(R.id.editText);

        cards[0] = findViewById(R.id.card1);
        cards[1] = findViewById(R.id.card2);
        cards[2] = findViewById(R.id.card3);

        card_drawable_ids[0] = randomCard();
        card_drawable_ids[1] = randomCard();
        card_drawable_ids[2] = randomCard();

        for(int i : card_drawable_ids)
            System.out.println(i);

        cards[0].setImageResource(card_drawable_ids[0]);
        cards[1].setImageResource(card_drawable_ids[1]);
        cards[2].setImageResource(card_drawable_ids[2]);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        tf = new TableFragment();
        ft.add(R.id.main_layout, tf);
        ft.commit();
    }

    public void selectCard(View view){
        if(!isTieBreaker) {
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

            if (index != selectedCard && selectedCard != -1)
                cards[selectedCard].setBackground(null);

            selectedCard = index;

            if (view.getBackground() == null)
                view.setBackgroundResource(R.drawable.image_border);
            else {
                if (card_drawable_ids[3] != 0 && card_drawable_ids[4] == 0) {
                    cards[4].setImageResource(card_drawable_ids[index]);
                    card_drawable_ids[4] = card_drawable_ids[index];
                } else if (card_drawable_ids[3] == 0) {
                    cards[3].setImageResource(card_drawable_ids[index]);
                    card_drawable_ids[3] = card_drawable_ids[index];
                }
                card_drawable_ids[index] = randomCard();
                ((ImageView) view).setImageResource(card_drawable_ids[index]);
                view.setBackground(null);
            }
        }
    }

    public void submitChat(View view){

        EditText editView = findViewById(R.id.editText);

        String message = editView.getText().toString().trim();

        if(!message.equals("")) {
            if(message.equals("math") && !isTieBreaker){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(tf.getId(), new MathFragment());
                ft.remove(tf);
                ft.commit();
                isTieBreaker = true;
            }

            if(!current_chatter) {
                message = getIntent().getExtras().getString("USERNAME") + ": " + message;
                current_chatter = true;
            }
            LinearLayout linearLayout = findViewById(R.id.chatBox);
            TextView text = new TextView(this);
            text.append(message);
            linearLayout.addView(text, 0);
            editView.setText("");
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
