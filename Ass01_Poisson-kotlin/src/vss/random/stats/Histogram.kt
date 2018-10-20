package cz.zcu.students.kiwi.vss.random.stats

class Histogram(private val min: Double, val max: Double) {
    private var intervals: Array<Int> = Array(0) { 0 }
    private var intervalSize: Double = 0.0

    fun build(values: Array<Double>, intervals: Int = 10): Histogram {
        this.intervals = Array(intervals + 1) { 0 }
        this.intervalSize = (this.max - this.min) / intervals

        for (value in values) {
            val h = ((value - this.min) / this.intervalSize).toInt()
            this.intervals[h]++
        }

        return this
    }

    fun intervals() = this.intervals

    fun everyInterval(callback: (Double, Double, Int) -> Unit) {
        for (i in 0 until this.intervals.size) {
            val start = min + intervalSize * i
            val end = start + intervalSize
            callback(start, end, intervals[i])
        }
    }

}
