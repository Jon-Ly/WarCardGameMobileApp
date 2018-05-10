package jonly.warcardgame;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;

/**
 * Created by Jonathan Ly on 5/7/2018.
 */

public class Card extends AppCompatImageView{

    private final int ACE = 1;
    private final int JACK = 11;
    private final int QUEEN = 12;
    private final int KING = 13;

    // (ACE) 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, (JACK) 11, (QUEEN) 12, (KING) 13
    private int value;
    private String label;
    private int id;

    public Card(Context context, int value, String label, int id){
        super(context);

        this.value = value;
        this.label = label;
        this.id = id;
    }

    public int getValue(){
        return this.value;
    }

    public String getLabel(){
        return this.label;
    }
    public int getId(){
        return this.id;
    }
}
