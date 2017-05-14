package cs102.mevzu06.bustagram2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cs102.mevzu06.bustagram2.Activities.Tables.SMD;
import cs102.mevzu06.bustagram2.Activities.Tables.TMD;
import cs102.mevzu06.bustagram2.R;

/**
 * Created by Mert Acar on 5/14/2017.
 */

public class OfflineSchedule extends Fragment {
    View mView;
    ListView listOurs;
    Context context;
    ArrayAdapter<CharSequence> adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_offline, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = this.getActivity();
    }
}
