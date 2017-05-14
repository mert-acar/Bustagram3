package cs102.mevzu06.bustagram2.Other;

/**
 * Created by Mert Acar on 5/13/2017.
 */

public class Bus {
    String tag;
    double latitude;
    double longitude;

    public Bus () {}

    public Bus ( String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    public void setTag ( String theTag) {
        tag = theTag;
    }

    public double getLatitude () {
        return latitude;
    }

    public void setLatLang (double lat, double longi) {
        latitude = lat;
        longitude = longi;
    }

    public double getLongitude () {
        return longitude;
    }

    public String getTag () {
        return tag;
    }
}
