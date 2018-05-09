package jonly.warcardgame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by wintow on 3/30/2018.
 */

public class TableFragment extends Fragment {

    private ImageView card_left;
    private ImageView card_right;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.table_fragment, container, false);

        if(savedInstanceState != null){
//            card_left_drawable = savedInstanceState.getInt("Card_Left_Drawable");
//            card_right_drawable = savedInstanceState.getInt("Card_Right_Drawable");
        }
        return fragmentView;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(card_left == null) {
            card_left = (ImageView) getActivity().findViewById(R.id.played_card_left);
        }
        if(card_right == null) {
            card_right = (ImageView) getActivity().findViewById(R.id.played_card_right);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putInt("Card_Left_Drawable", card_left_drawable);
//        savedInstanceState.putInt("Card_Right_Drawable", card_right_drawable);
    }

    public void setCardLeftDrawable(Card card){
        card_left = card;
    }

    public void setCardRightDrawable(Card card){
        card_right = card;
    }
}
