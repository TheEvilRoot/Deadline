package com.theevilroot.deadline

import com.theevilroot.deadline.objects.*
import java.util.*

object TheHolder {

    /**
     * Class with application's general properties!
     *
     * Just holder, that accessible anywhere, when @link{android.app.Application} only from activities
     *
     * */

    var needRefresh = false
    var isConfigLoaded = false
    var examList: List<Exam> = emptyList()

}