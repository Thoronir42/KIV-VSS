package cz.zcu.students.kiwi.vss.random.value

import cz.zcu.students.kiwi.vss.random.stats.RandValDistributionStats
import java.util.*

/**
 * Poisson variance generator:
 *
 * algorithm: Poisson generator based upon the inversion by sequential search
 *
 * @see <a href="https://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables">Poisson variance at Wikipedia</a>
 */
class PoissonDistribution(private var rng: Random, private var lambda: Double) : IRandomValue {

    override fun getNext(): Double {
        var x = 0
        var p = Math.exp(-this.lambda)
        var s = p

        val u = rng.nextDouble()
        while (u > s) {
            x++
            p = (p * this.lambda) / x
            s += p
        }

        return x.toDouble()
    }

    override fun getDistribution() = RandValDistributionStats(lambda, lambda)

}
