package cz.zcu.students.kiwi.vss

import cli.CLIException
import cli.PrintUsageException
import java.io.PrintStream
import java.util.*

class CLI(private val name: String = "Program") {
    private var required = 0
    private var args = ArrayList<Argument>()
    private var options = ArrayList<Option>()

    init {
        this.option("help", short = "h", description = "Prints usage")
    }

    fun arg(name: String, required: Boolean = false, description: String? = null) {
        this.args.add(Argument(name, required, description))
        if (required) this.required++
    }

    fun option(name: String, params: Int = 0, short: String? = null, description: String? = null) = this.options.add(Option(name, params, short, description))

    fun printUsage(out: PrintStream) {
        with(out) {
            println("Usage: ")
            print(name)
            if (options.size > 0) {
                print(" [options]")
            }
            for (arg in args) {
                val argName = arg.name
                print(" " + if (arg.required) "($argName)" else "[$argName]")
            }

            println()
            println("  arguments")
            for (arg in args) {
                println("    %-20s %s".format(arg.name, arg.description))
            }

            println("  options")
            for (o in options) {
                println("    -%-2s \t --%-20s[%d]\t%s".format(o.short, o.name, o.params, o.description))
            }
        }
    }

    fun parse(vararg params: String): Parsed {
        val result = Parsed()

        var i = 0
        while (i < params.size) {
            val param = params[i]
            val option: Option? = findOption(param)

            if (option != null) {
                val optParamStart = i + 1
                val optParamEnd = optParamStart + option.params - 1
                if (optParamEnd >= params.size) {
                    println("")
                    throw CLIException("Option $param does not have enough parameters. Required $optParamStart..$optParamEnd, got " + params.size)
                }

                result.options[option.name] = Arrays.copyOfRange(params, optParamStart, optParamStart + option.params)
                i += option.params
            } else {
                if (result.arguments.size < params.size) {
                    result.arguments.add(param)
                }
            }
            i++
        }

        if (result.optionBoolean("help")) throw PrintUsageException()
        if (result.arguments.size < this.required) throw CLIException("Invalid arguments supplied")

        return result
    }

    private fun findOption(param: String): Option? {
        val query: String
        val predicate: (Option) -> Boolean
        if (param.substring(0..0) == "-") {
            query = param.substring(1)
            predicate = { option -> option.short == query }
        } else if (param.length > 1 && param.substring(0..1) == "--") {
            query = param.substring(2)
            predicate = { option -> option.name == query }
        } else {
            return null
        }

        return options.find { option -> !option.used && predicate(option) }
    }

    class Parsed {
        val arguments = ArrayList<String>()
        val options = HashMap<String, Array<String>>()

        operator fun get(a: Int) = arguments[a]

        fun option(name: String): Array<String>? = this.options[name]

        fun optionInt(name: String, default: Int = 0): Int {
            val opt = this.option(name)
            if (opt == null || opt.isEmpty()) {
                return default
            }
            return opt[0].toInt()
        }


        fun optionLong(name: String, default: Long = 0): Long {
            val value = this.option(name)
            if (value == null || value.isEmpty()) {
                return default
            }

            return value[0].toLong()
        }

        fun optionBoolean(name: String): Boolean {
            return this.options.containsKey(name)
        }
    }
}

private data class Argument(val name: String, val required: Boolean = false, val description: String?)

private data class Option(val name: String, val params: Int = 0, val short: String?, val description: String?) {
    var used = false
}
