package com.theevilroot.deadline

import com.theevilroot.deadline.objects.Exam
import com.theevilroot.deadline.objects.Preference
import com.theevilroot.deadline.objects.PreferenceBoolean

object TheHolder {

    /**
     * Class with application's general properties!
     *
     * Just holder, that accessible anywhere, when @link{android.app.Application} only from activities
     *
     * */

    const val preferenceVersion = 3141592

    var needRefresh = false
    var isConfigLoaded = false
    var examList: List<Exam> = emptyList()

    var userPreferences = listOf(
            Preference(isGroup = true, title = "Главное"),
            Preference(id ="check_for_updates",
                    name = "Проверять обновления",
                    description = "Разрешить приложению проверять наличие актуальных версий программы. Это помогает получать новейшие версии с новыми доработками и исправлениями ошибок",
                    value = PreferenceBoolean(true)),
            Preference(isGroup = true, title = "Таймер"),
            Preference(id = "timer_notif",
                    name = "Нотификация за сутки до экзамена",
                    description = "Сообщать об экзамене за 24 часа до него пуш-уведомлением! Внимание, функция может не работать, если у приложения нет разрешения отправлять уведомления",
                    value = PreferenceBoolean(true)),
            Preference(isGroup = true, title = "UI"),
            Preference(id = "screen_rotate",
                    name = "Разрешить поворот телефона",
                    description = "Разрешить поворачивать экран во время работы приложения",
                    value = PreferenceBoolean(false))
    )

    fun get(id: String): Preference? =
            userPreferences.filter { it.id == id }.getOrNull(0)

    fun isNotificationEnabled(): Boolean = get("timer_notif")!!.value.boolean.booleanValue
    fun isScreenRotationEnabled(): Boolean =  get("screen_rotate")!!.value.boolean.booleanValue
    fun isUpdateCheckerEnabled(): Boolean = get("check_for_updates")!!.value.boolean.booleanValue

}