package cs102.mevzu06.bustagram2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import cs102.mevzu06.bustagram2.Activities.Tables.MainCampus;
import cs102.mevzu06.bustagram2.R;

/**
 * Created by Mert Acar on 7.05.2017.
 */
public class STARS extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public STARS() {
        // Required empty public constructor
    }

    public static STARS newInstance(String param1, String param2) {
        STARS fragment = new STARS();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stars,container,false);
        String[] menuItems = {"Main Campus", "East Campus", "Main - East Campus Ring", "Main Campus Ring", "Regional Services"};
        ListView spinner = (ListView) view.findViewById(R.id.starsList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.bus_spinner, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Intent busMenu = null;
                switch (position) {
                    case 0:
                        busMenu = new Intent(getActivity(), MainCampus.class);
                        break;
            /*case 1:
                busMenu = new Intent(getActivity(), EastCampus.class);
                break;
            case 2:
                busMenu = new Intent(getActivity(), MainEastCampus.class);
                break;
            case 3:
                busMenu = new Intent(getActivity(), MainCampusRing.class);
                break;
            case 4:
                busMenu = new Intent(getActivity(), Regional.class);
                break;*/
                }

                if (busMenu != null)
                    startActivity(busMenu);
            }
        });
        return view;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        view.setSelected(true);
        Intent busMenu = null;
        switch (pos) {
            case 0:
                busMenu = new Intent(getActivity(), MainCampus.class);
                break;
            /*case 1:
                busMenu = new Intent(getActivity(), EastCampus.class);
                break;
            case 2:
                busMenu = new Intent(getActivity(), MainEastCampus.class);
                break;
            case 3:
                busMenu = new Intent(getActivity(), MainCampusRing.class);
                break;
            case 4:
                busMenu = new Intent(getActivity(), Regional.class);
                break;*/
        }

            if (busMenu != null)
                startActivity(busMenu);
    }


    public void onNothingSelected(AdapterView<?> parent) {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
