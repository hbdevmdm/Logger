package com.hb.logger.data.model

import android.text.TextUtils

class FilterState {
    var keyword: String? = ""
    var dateTime: String = ""
    var dateTimeToDisplay: String = ""
    var filterType: String = ""

    fun isFilterApplied(): Boolean {
        return !TextUtils.isEmpty(dateTime) || !TextUtils.isEmpty(filterType)
    }
}
