package cs102.mevzu06.bustagram2.Activities;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import cs102.mevzu06.bustagram2.Activities.Tables.MainCampus;
import cs102.mevzu06.bustagram2.Activities.Tables.TMD;
import cs102.mevzu06.bustagram2.Fragments.STARS;
import cs102.mevzu06.bustagram2.Fragments.WhereIsMyBus;
import cs102.mevzu06.bustagram2.Other.BackgroundWorker;
import cs102.mevzu06.bustagram2.Other.HTMLFilteredReader;
import cs102.mevzu06.bustagram2.R;

import static android.R.attr.fragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, STARS.OnFragmentInteractionListener {

    private Handler mHandler;
    NfcAdapter nfcAdapter;
    LocationListener locationListener;
    LocationManager locationManager;
    private Context context;
    boolean isFirst;
    final String URL = "http://bustagramm.000webhostapp.com/id.php";
    TextView tv;
    String content;
    Toolbar toolbar;
    ListView listOurs;
    ArrayAdapter<CharSequence> adapter;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        isFirst = true;


        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // List
        listOurs = (ListView) findViewById(R.id.our_list);
        adapter = ArrayAdapter.createFromResource(this, R.array.ours, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        listOurs.setAdapter(adapter);
        listOurs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                Intent listitem = null;
                switch (position) {
                    case 0:
                        listitem = new Intent(context, TMD.class);
                        break;
                    /*case 1:
                        listitem = new Intent( context, SMD.class);
                        break;
                    case 2:
                        listitem = new Intent( context, Ring.class);
                        break;*/
                }

                if (listitem != null)
                    startActivity(listitem);
            }
        });

        // TextView
        tv = (TextView) findViewById(R.id.textmy);
        tv.setText("SHIT");

        // Drawer
        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //NFC Things
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                createNotification("You are at: " + location.getLatitude() + ", " + location.getLongitude());
                HTMLFilteredReader reader = new HTMLFilteredReader(URL);
                String dbid = reader.getPageContents();
                if (isFirst) {
                    sendToDatabase(location.getLatitude() + "", location.getLongitude() + "", content);
                    tv.setText(dbid);
                    isFirst = false;
                } else
                    updateDatabase(location.getLatitude() + "", location.getLongitude() + "", content, dbid);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
    }

    @Override
    public void onBackPressed() {
        // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about_us) {
            startActivity(new Intent(this, AboutUs.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String theTitle = "";

        if (id == R.id.nav_where_is_my_bus) {
            fragment = new WhereIsMyBus();
            theTitle = "Where Is My Bus?";
        } else if (id == R.id.nav_offline_schedule) {
            fragment = new STARS();
            theTitle = "STARS Schedules";
        } /*else if (id == R.id.nav_helpOption) {
            fragment = new HelpFragment();
            theTitle = "Help";
        } else if (id == R.id.nav_settingsOption) {
            fragment = new SettingsFragment();
            theTitle = "Settings";
        }*/

        final Fragment finalFragment = fragment;
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.content_frame, finalFragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        mHandler.post(mPendingRunnable);

        // An alternative way without the animation and the handler.
        /*if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }*/

        if (!theTitle.equals(""))
            setToolbarTitle(theTitle);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);

    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void createNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification n = new Notification.Builder(this)
                .setContentTitle("Bustagram")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_stat_directions_bus)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void trackUsers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 27);
            return;
        }
        locationManager.requestLocationUpdates("gps", 2000, 0, locationListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            // Reading
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
            } else {
                Toast.makeText(this, "TAG IS EMPTY AF", Toast.LENGTH_SHORT).show();
            }

            // Writing
            /*Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage("TMD2");
            writeNdefMessage(tag,ndefMessage);

            // Tracking
            /**trackUsers();
            isFirst = true;*/

            // Notifications
            Toast.makeText(this, "You are on " + content, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Have a nice ride!", Toast.LENGTH_LONG).show();

        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            content = getTextFromNdefRecord(ndefRecord);
            tv.setText(content);
        } else {
            Toast.makeText(this, "No NDEF records", Toast.LENGTH_SHORT).show();
        }

    }

    private String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return tagContent;
    }


    private void formatTag(Tag busTag, NdefMessage thePlate) {
        try {
            NdefFormatable nf = NdefFormatable.get(busTag);
            if (nf == null) {
                Toast.makeText(this, "Tag is not formatable", Toast.LENGTH_SHORT).show();
                return;
            }
            nf.connect();
            nf.format(thePlate);
            nf.close();

            Toast.makeText(this, "Tag written", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    private void writeNdefMessage(Tag busTag, NdefMessage thePlate) {
        try {
            if (busTag == null) {

                Toast.makeText(this, "Tag cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(busTag);
            if (ndef == null) {
                formatTag(busTag, thePlate);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag cannot be written", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(thePlate);
                ndef.close();

                Toast.makeText(this, "Tag written!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);
            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content) {
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});
        return ndefMessage;
    }


    public void sendToDatabase(String latitude, String longitude, String buscode) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute("uploadLocation", latitude, longitude, buscode);
    }

    //Updates the data.
    public void updateDatabase(String latitude, String longitude, String buscode, String dbid) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute("updateLocation", latitude, longitude, buscode, dbid);
    }
}



