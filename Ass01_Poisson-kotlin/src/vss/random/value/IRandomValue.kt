package cz.zcu.students.kiwi.vss.random.value

import cz.zcu.students.kiwi.vss.random.stats.RandValDistributionStats

interface IRandomValue {

    fun getNext(): Double

    fun getDistribution(): RandValDistributionStats

}
