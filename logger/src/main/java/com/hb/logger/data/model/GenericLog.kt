package com.hb.logger.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
open class GenericLog : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    open fun generateStandardLog(eventsSequenceLog: EventsSequenceLog): String {

        return ""
    }
}
