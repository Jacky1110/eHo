package eho.jotangi.com.ui.wristband.ecg

import android.os.Parcelable
import com.github.mikephil.charting.data.LineData
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class LineDataModel (
    var data:@RawValue LineData
):Parcelable