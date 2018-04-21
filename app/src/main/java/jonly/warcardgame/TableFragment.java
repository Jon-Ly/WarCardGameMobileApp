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
    private int card_left_drawable;
    private int card_right_drawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.table_fragment, container, false);
        card_left_drawable = 0;
        card_right_drawable = 0;

        if(savedInstanceState != null){
            card_left_drawable = savedInstanceState.getInt("Card_Left_Drawable");
            card_right_drawable = savedInstanceState.getInt("Card_Right_Drawable");
        }
        return fragmentView;
    }

    @Override
    public void onStart(){
        super.onStart();

        if(card_left == null) {
            card_left = (ImageView) getActivity().findViewById(R.id.played_card_left);
            if(card_left_drawable != 0)
                card_left.setImageResource(card_left_drawable);
        }
        if(card_right == null) {
            card_right = (ImageView) getActivity().findViewById(R.id.played_card_right);
            if(card_right_drawable != 0)
                card_right.setImageResource(card_right_drawable);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Card_Left_Drawable", card_left_drawable);
        savedInstanceState.putInt("Card_Right_Drawable", card_right_drawable);
    }

    public void setCardLeftDrawable(int drawable_id){
        card_left.setImageResource(drawable_id);
        card_left_drawable = drawable_id;
    }

    public void setCardRightDrawable(int drawable_id){
        card_right.setImageResource(drawable_id);
        card_right_drawable = drawable_id;
    }

    public int getLeftDrawable(){
        return card_left_drawable;
    }

    public int getRightDrawable(){
        return card_right_drawable;
    }
}
