package com.theevilroot.deadline.objects

import java.util.*

class IllegalPreferenceTypeException(msg: String): RuntimeException(msg)

class PreferenceInt(val intValue: Int = 0): PreferenceValue(intValue)
class PreferenceString(val stringValue: String = ""): PreferenceValue(stringValue)
class PreferenceBoolean(val booleanValue: Boolean = false): PreferenceValue(booleanValue)
class PreferenceFloat(val floatValue: Float = 0.0f): PreferenceValue(floatValue)
class PreferenceDate(val dateValue: Date = Date(0), val pattern: String = ""): PreferenceValue(dateValue)

open class PreferenceValue(var value: Any) {
    val isInt
        get() = this is PreferenceInt
    val isFloat
        get() = this is PreferenceFloat
    val isString
        get() = this is PreferenceString
    val isBoolean
        get() = this is PreferenceBoolean
    val isDate
        get() = this is PreferenceDate

    val int: PreferenceInt
        get() = if(!isInt) throw IllegalPreferenceTypeException("Int is the wrong type!") else this as PreferenceInt
    val float: PreferenceFloat
        get() = if(!isFloat) throw IllegalPreferenceTypeException("Float is the wrong type!") else this as PreferenceFloat
    val string:PreferenceString
        get() = if(!isString) throw IllegalPreferenceTypeException("String is the wrong type!") else this as PreferenceString
    val boolean: PreferenceBoolean
        get() = if(!isBoolean) throw IllegalPreferenceTypeException("Boolean is the wrong type!") else this as PreferenceBoolean
    val date: PreferenceDate
        get() = if(!isDate) throw IllegalPreferenceTypeException("Date is the wrong type!") else this as PreferenceDate
}