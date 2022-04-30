package eho.jotangi.com.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.core.app.ActivityCompat
import timber.log.Timber

/**
 *Created by Luke Liu on 2/5/21.
 */

class LocationHelper {
	companion object{
		fun requestLocationPermissions(activity: Activity){
			Timber.e("LOCATION-> requestLocationPermissions")
			val permissions = arrayOf(
				Manifest.permission.ACCESS_FINE_LOCATION
			)

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				ActivityCompat.requestPermissions(
					activity, permissions,
					JotangiUtilConstants.PERMISSION_LOCATION_REQUEST_CODE
				)
			}
		}

		fun checkLocationPermission(context: Context): Boolean{
			Timber.e("LOCATION-> checkLocationPermission")
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
			) {
				return false
			}
			return true
		}

		fun setSharePreferenceLocation(location: Location) {
			Timber.e("LOCATION-> setSharePreferenceLocation: $location")

			SharedPreferencesUtil.instances.run {
				setUserLatitude("${location.latitude}")
				setUserLongitude("${location.longitude}")

				Timber.e("LOCATION-> SharePreferenceLocation: ${getUserLatitude()}, ${getUserLongitude()}")
			}
		}
	}
}