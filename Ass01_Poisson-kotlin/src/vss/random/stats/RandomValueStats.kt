package cz.zcu.students.kiwi.vss.random.stats

class RandomValueStats(measures: Int) {
    private var filled = 0
    private var measures = Array(measures) { 0.0 }
    private var min: Double = Double.MAX_VALUE
    private var max: Double = Double.MIN_VALUE

    fun put(value: Double) {
        this.measures[this.filled++] = value
        if (value > max) max = value
        if (value < min) min = value
    }

    fun getHistogram(intervals: Int): Histogram {
        return Histogram(this.min, this.max).build(measures.copyOfRange(0, this.filled), intervals)
    }

    fun getDistribution(): RandValDistributionStats {
        var sum = 0.0
        for(i in 0 until filled) {
            sum += measures[i]
        }
        val expectedValue = sum / filled

        var sampleVariance = 0.0
        for(i in 0 until filled) {
            sampleVariance += Math.pow(expectedValue - measures[i], 2.0)
        }
        val variance = sampleVariance / filled

        return RandValDistributionStats(expectedValue, variance)
    }

}
