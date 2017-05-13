package cs102.mevzu06.bustagram2.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.TimerTask;

import cs102.mevzu06.bustagram2.R;


/**
 * Created by Mert Acar on 7.05.2017.
 */

public class WhereIsMyBus extends Fragment implements OnMapReadyCallback {
    double busLatitude;
    double busLongitude;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    ListView listView;
    Marker tmd1;
    TextView tvv;
    Button b;
    Handler handler;
    Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        update();
        return mView;
    }

    public WhereIsMyBus() {
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null)
            mMapView.onCreate(null);
        mMapView.onResume();
        mMapView.getMapAsync(this);
        // .setMyLocationEnabled(true);

        listView = (ListView) mView.findViewById(R.id.listOnline);
        final String[] listItems = {"TMD", "SMD", "Ring"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, Arrays.asList(listItems));
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "You've clicked: " + listItems[position], Toast.LENGTH_SHORT).show();
            }
        });


        tvv = (TextView) view.findViewById(R.id.myText);
        b = (Button) view.findViewById(R.id.myButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBusLocation();
                tvv.setText(busLatitude + " ," + busLongitude);
            }
        });

        getBusLocation();
        Toast.makeText(this.getContext(), busLatitude + " ," + busLongitude, Toast.LENGTH_LONG).show();


        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.VISIBLE);
            }
        });

    }

    // Magic don't touch!
    public void update() {
        handler = new Handler();
        handler.postDelayed(runnable, 3000);
        runnable = new Runnable() {
            @Override
            public void run() {
                getBusLocation();
                tvv.setText(busLatitude + " ," + busLongitude);
                handler.postDelayed(runnable, 3000);
            }
        };

    }

    class LocationFetcher extends AsyncTask<Void, String, String> {
        //properties
        String average_url = "http://bustagramm.000webhostapp.com/avg.php";
        Context context;
        String result;

        //constructor
        LocationFetcher(Context ctx) {
            context = ctx;
        }

        //methods
        protected String doInBackground(Void... voids) {
            try {
                String line;
                URL url = new URL(average_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            for (int i = 0; i < result.length(); i++) {
                if (result.charAt(i) == '<') {
                    busLatitude = Double.parseDouble(result.substring(4, i));
                    busLongitude = Double.parseDouble(result.substring(i + 4));
                    tmd1.setPosition(new LatLng(busLatitude, busLongitude));
                }
            }
        }
    }

    public void getBusLocation() {
        LocationFetcher locationFetcher = new LocationFetcher(this.getContext());
        locationFetcher.execute();
    }

    class UpdateMapTask extends TimerTask {
        public void run() {
            getBusLocation();
            tvv.setText(busLatitude + " ," + busLongitude);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        LatLng test = new LatLng(busLatitude, busLongitude);
        tmd1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(busLatitude, busLongitude)).title("Mert").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_directions_bus2)));
        tmd1.setPosition(new LatLng(busLatitude, busLongitude));
        mGoogleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        /*googleMap.addMarker (new MarkerOptions().position(new LatLng(39.917563,32.823518)).title("Bahçeli")).setVisible(true);
        googleMap.addMarker (new MarkerOptions().position(new LatLng(39.909977,32.857625)).title("Tunus")).setVisible(true);
        googleMap.addMarker (new MarkerOptions().position(new LatLng(39.908574,32.776625)).title("Kentpark")).setVisible(true);
        googleMap.addMarker (new MarkerOptions().position(new LatLng(39.911822,32.809670)).title("Armada")).setVisible(true);*/
        CameraPosition bilkent = CameraPosition.builder().target(new LatLng(busLatitude, busLongitude)).zoom(13).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(bilkent));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                listView.setVisibility(View.GONE);
            }
        });
    }


}
