package com.theevilroot.deadline.objects

import android.support.annotation.DrawableRes

class Preference(val key: String,
                 val title: String = "",
                 val summary: String = "",
                 val onValue: String = "",
                 val offValue: String = "",
                 @DrawableRes val icon: Int? = null)