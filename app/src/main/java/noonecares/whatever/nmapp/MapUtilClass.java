package noonecares.whatever.nmapp;

import android.annotation.SuppressLint;
import android.content.Context;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by justdial on 4/27/17.
 */
@SuppressLint("NewApi")

public class MapUtilClass {

    public static Marker drawMarker(Double lat, Double lon, Context context, MapView mapView, int markerImage, String title) {
        GeoPoint point = new GeoPoint(lat,lon);
        Marker marker = new Marker(mapView);
        marker.setDraggable(false);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(context.getDrawable(markerImage));
        marker.setTitle(title);
        mapView.getOverlays().add(marker);
        mapView.getController().setCenter(point);
        mapView.getController().setZoom(18);
        mapView.getController().animateTo(point);
        return  marker;
    }

    public static Marker drawMarker(GeoPoint point, Context context, MapView mapView, int markerImage, String title) {
        Marker marker = new Marker(mapView);
        marker.setDraggable(false);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(context.getDrawable(markerImage));
        marker.setTitle(title);
        mapView.getOverlays().add(marker);
        mapView.getController().setCenter(point);
        mapView.getController().setZoom(18);
        mapView.getController().animateTo(point);
        return  marker;
    }

}
