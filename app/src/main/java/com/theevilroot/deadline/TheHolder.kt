package com.theevilroot.deadline

import com.theevilroot.deadline.objects.Exam
import com.theevilroot.deadline.objects.PT_BOOLEAN
import com.theevilroot.deadline.objects.Preference
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

    var userPreferences = listOf<Preference>(
            Preference(true, "Таймер"),
            Preference(false,id = "timer_notif",name = "Нотификация за сутки до экзамена", description = "Сообщать об экзамене за 24 часа до него пуш-уведомлением! Внимание, функция может не работать, если у приложения нет разрешения отправлять уведомления", type = PT_BOOLEAN, value = "true")
    )

    fun get(id: String): Preference? =
            userPreferences.filter { it.id == id }.getOrNull(0)

}