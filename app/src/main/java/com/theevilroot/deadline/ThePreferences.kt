package com.theevilroot.deadline

import android.graphics.Color

object ThePreferences {


    /**
     * Class with application's preferences' properties!
     *
     * Just holder, that accessible anywhere, when @link{android.app.Application} only from activities
     *
     * */

    var backgroundColor = Color.parseColor("#f4f4f4")
    var secondBackgroundColor = Color.parseColor("#ffffff")
    var textColor = Color.parseColor("#e8e8e8")
    var iconsColor = Color.parseColor("#e8e8e8")
    var inactiveColor = Color.parseColor("#bbbbbb")
    var activeColor = Color.parseColor("#000000")
    var selectionColor = Color.argb(168, 111, 169, 251)

}