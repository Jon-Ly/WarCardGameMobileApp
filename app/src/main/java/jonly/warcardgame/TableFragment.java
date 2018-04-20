package jonly.warcardgame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wintow on 3/30/2018.
 */

public class TableFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View fragmentView = inflater.inflate(R.layout.table_fragment, container, false);
        setRetainInstance(true);
        return fragmentView;
    }
}
