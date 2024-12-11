import arrow.core.Either
import arrow.core.flatMap
import com.sun.tools.javac.Main
import java.io.File

fun main(args: Array<String>) {
    getInput(args)
        .map { Pair(partOne(it), partTwo(it)) }
        .map { (resultOne, resultTwo) ->
            println("Result One: $resultOne")
            println("Result Two: $resultTwo")
        }.mapLeft { System.err.println(it) }
}

private fun getInput(args: Array<String>) = Either.Right(args)
    .flatMap {
        if (it.size != 1) {
            Either.Left("Usage: ${Main::class.simpleName} <input>")
        } else {
            Either.Right(it[0])
        }
    }
    .flatMap { fileName ->
        val file = File(fileName)

        if (file.exists()) Either.Right(file) else Either.Left("$fileName: Does not exist")
    }
    .map { file -> file.readLines(charset = Charsets.UTF_8) }

fun partOne(input: List<String>): String {
    return "Input length: ${input.size}"
}

fun partTwo(input: List<String>): String {
    return "Input length: ${input.size}"
}
