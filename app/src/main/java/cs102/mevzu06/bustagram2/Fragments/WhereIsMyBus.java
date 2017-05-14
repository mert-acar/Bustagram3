package cs102.mevzu06.bustagram2.Fragments;

import android.content.Context;
import android.graphics.BitmapFactory;
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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import cs102.mevzu06.bustagram2.Other.Bus;
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
    Handler handler;
    Runnable runnable;
    String average_tmd_url = "http://bustagramm.000webhostapp.com/avgTMD.php";
    String average_smd_url = "http://bustagramm.000webhostapp.com/avgSMD.php";
    String average_ring_url = "http://bustagramm.000webhostapp.com/avgRING.php";
    ArrayList<Bus> buses;
    ArrayList<Marker> markers;
    String url;
    String tag;
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
        url = average_tmd_url;
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null)
            mMapView.onCreate(null);
        mMapView.onResume();
        mMapView.getMapAsync(this);

        buses = new ArrayList<>();
        markers = new ArrayList<>();

        listView = (ListView) mView.findViewById(R.id.listOnline);

        final String[] listItems = {"TMD", "SMD", "Ring"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, Arrays.asList(listItems));
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        url = average_tmd_url;
                        listView.setVisibility(View.GONE);
                        break;
                    case 1:
                        url = average_smd_url;
                        listView.setVisibility(View.GONE);
                        break;
                    case 2:
                        url = average_ring_url;
                        listView.setVisibility(View.GONE);
                        break;
                }
            }
        });

        for (int i = 0; i < 5; i++) {
            buses.add(new Bus());
        }

        getBusLocation();
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
        handler.postDelayed(runnable, 1500);
        runnable = new Runnable() {
            @Override
            public void run() {
                getBusLocation();
                handler.postDelayed(runnable, 1500);
            }
        };

    }

    public class LocationFetcher extends AsyncTask<Void, String, String> {
        //properties
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
                URL url2 = new URL(url);

                if (url.equals(average_tmd_url))
                    tag = "TMD";
                else if (url.equals(average_smd_url))
                    tag = "SMD";
                else if (url.equals(average_ring_url))
                    tag = "RING";

                HttpURLConnection httpURLConnection = (HttpURLConnection) url2.openConnection();
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
            String tempLat;
            String tempLong;
            int loc_a;
            int loc_b;
            int count = 0;
            for (int i = 0; i < result.length(); i++) {
                if ((result.charAt(i) + "").equals("a")) {
                    if (result.charAt(i + 1) != 'b') {
                        loc_b = result.indexOf("b", i);
                        System.out.println("Loc_b: " + loc_b);
                        tempLat = result.substring(i + 1, loc_b);
                        System.out.println("tempLat: " + tempLat);
                        loc_a = result.indexOf("a", loc_b);
                        if (loc_a != -1) {
                            System.out.println("Loc_a: " + loc_a);
                            tempLong = result.substring(loc_b + 1, loc_a);
                            System.out.println("tempLong: " + tempLong);
                            buses.get(count).setLatLang(Double.parseDouble(tempLat), Double.parseDouble(tempLong));
                            count++;
                            i = loc_a - 1;
                        }
                    } else
                        i = result.length();
                }
            }

            for (int i = 0; i < buses.size(); i++) {
                if (buses.get(i).getLatitude() == 0)
                    markers.get(i).setVisible(false);
                else {
                    markers.get(i).setPosition(new LatLng(buses.get(i).getLatitude(), buses.get(i).getLongitude()));
                    markers.get(i).setTitle(tag + (i + 1));
                }
            }

        }
    }

    public void getBusLocation() {
        LocationFetcher locationFetcher = new LocationFetcher(this.getContext());
        locationFetcher.execute();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        for (int i = 0; i < buses.size(); i++) {
            markers.add(googleMap.addMarker(new MarkerOptions().position(new LatLng(buses.get(i).getLatitude(), buses.get(i).getLongitude())).title(buses.get(i).getTag()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_directions_bus2))));
            markers.get(i).setVisible(true);
        }

        googleMap.addMarker(new MarkerOptions().position(new LatLng(39.917563, 32.823518)).title("Bahçeli")).setVisible(true);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(39.909977, 32.857625)).title("Tunus")).setVisible(true);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(39.908574, 32.776625)).title("Kentpark")).setVisible(true);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(39.911822, 32.809670)).title("Armada")).setVisible(true);
        CameraPosition bilkent = CameraPosition.builder().target(new LatLng(39.88661, 32.803614)).zoom(12).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(bilkent));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                listView.setVisibility(View.GONE);
            }
        });
    }
}