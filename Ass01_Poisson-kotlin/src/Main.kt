package cz.zcu.students.kiwi

import cz.zcu.students.kiwi.cli.*
import cz.zcu.students.kiwi.vss.random.stats.Histogram
import cz.zcu.students.kiwi.vss.random.stats.RandValDistributionStats
import cz.zcu.students.kiwi.vss.random.stats.RandomValueStats
import cz.zcu.students.kiwi.vss.random.value.PoissonDistribution
import java.util.*

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        mainRun("1000", "96", "-s", "420", "-hi", "20")
        mainRun("1000000", "96", "-s", "420", "-hi", "20", "-hc", "1000")
//        mainRun("100000", "72")
    } else {
        mainRun(*args)
    }
}

private fun cli(): CLI {
    val cli = CLI("evalPoisson")
    cli.arg("measures", true)
    cli.arg("lambda", true, "Expected value of Poisson Distribution")
    cli.option("seed", 1, "s", "RNG seed")
    cli.option("histogramIntervals", 1, "hi", "Amount of intervals to split histogram into")
    cli.option("histogramCollapse", 1, "hc", "Histogram visualiser symbol occurrence divisor")

    return cli
}

fun mainRun(vararg args: String) {
    // process cli
    val cli = cli()
    val cliParsed: CLI.Parsed
    try {
        cliParsed = cli.parse(*args)
    } catch (ex: CLIException) {
        if(ex is PrintUsageException) {
            cli.printUsage(System.out)
        } else {
            System.err.println(ex.toString())
            cli.printUsage(System.err)
        }
        return
    }

    if (cliParsed.optionBoolean("help")) {
        cli.printUsage(System.out)
        System.exit(0)
    }

    val measures = cliParsed[0].toInt()
    val lambda = cliParsed[1].toDouble()
    val seed = cliParsed.optionLong("seed", System.currentTimeMillis())

    val histogramIntervals = cliParsed.optionInt("histogramIntervals", measures / 10)
    val historyCollapse = cliParsed.optionInt("histogramCollapse", histogramIntervals)


    // create variance function
    val distribution = PoissonDistribution(Random(seed), lambda)
    val distributionTheory = distribution.getDistribution()

    // measure variance
    val stats = RandomValueStats(measures)
    for (i in 0 until measures) {
        stats.put(distribution.getNext())
    }
    val distributionActual = stats.getDistribution()

    // display results
    printStats("teorie", distributionTheory)
    printStats("vypocet", distributionActual)
    println()

    printHistogram(stats.getHistogram(histogramIntervals), historyCollapse)
}

private fun printStats(label: String, distribution: RandValDistributionStats) {
    println("E_$label=" + distribution.expectedValue)
    println("D_$label=" + distribution.variance)
}

private fun printHistogram(histogram: Histogram, historyCollapse: Int) {
    val countMax = histogram.intervals().max()!!
    val countCiphers = Math.ceil(Math.log10(countMax.toDouble())).toInt()

    val intervalCiphers = Math.ceil(Math.log10(histogram.max)).toInt() + 3

    val format = "[%" + countCiphers + "d] - %" + intervalCiphers + "s - %" + intervalCiphers + "s: %s"
    histogram.everyInterval { min, max, count ->
        val sMin = "%.2f".format(min)
        val sMax = "%.2f".format(max)

        println(format.format(count, sMin, sMax, "*".repeat(count / historyCollapse)))
    }
}


