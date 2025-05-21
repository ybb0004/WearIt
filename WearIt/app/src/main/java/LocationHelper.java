import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public LocationHelper(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(LocationListener listener) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            listener.onPermissionRequired();
            return;
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    listener.onLocationFailed();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        listener.onLocationFound(location.getLatitude(), location.getLongitude());
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                        return;
                    }
                }
                listener.onLocationFailed();
            }
        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public interface LocationListener {
        void onLocationFound(double latitude, double longitude);
        void onPermissionRequired();
        void onLocationFailed();
    }
}