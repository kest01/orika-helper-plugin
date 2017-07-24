package ru.kest.plugin.orika.test

/**
 * Generates mock test values
 *
 * Created by KKharitonov on 08.07.2017.
 */
class MockDataGenerator {

    private var intCounter = 1
    private var bool = false
    private var dateDay = 1


    fun getNextInt() = intCounter++

    fun getNextDouble() = intCounter++.toDouble()

    fun getNextBoolean() : Boolean {
        bool = !bool
        return bool
    }

    fun getNextDate() : String {
        return "DateUtil.setDate(${dateDay++}, Calendar.DECEMBER, 2015)"
    }

}