package com.myrtle.busschedule

class BusRepository private constructor() {

    companion object {
        val instance by lazy { BusRepository() }
    }

    protected val busList by lazy { initializeData() }

    fun list() = busList

    fun item(index: Int) = busList[index]

    protected fun initializeData(): List<Bus> {
        //TODO заменить на информацию из апи
        val data = mutableListOf<Bus>()

        for (position in 0 until 25) {

            val time = "$position"

            val bus = Bus(time)
            data.add(bus)
        }

        return data
    }
}