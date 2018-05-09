package jonly.warcardgame;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by wintow on 5/7/2018.
 */

public class Deck {
    private ArrayList<Card> current_hand;

    public Deck(){
        this.current_hand = new ArrayList<Card>();
    }

    public ArrayList<Card> getCurrentHand(){
        return current_hand;
    }

    public void add(Card card){
        this.current_hand.add(card);
    }

    public int size(){
        return current_hand.size();
    }

}
