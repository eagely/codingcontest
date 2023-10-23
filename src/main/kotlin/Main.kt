
import utils.Utils.removeTrailingNumbers
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipFile

fun main() {
    unzipInput()
    val solutions = getSolutions()
    var total = 0
    solutions.forEach { solutionInstance ->
        val startTime = System.currentTimeMillis().toInt()

        val inputs = solutionInstance.getInput()
        inputs.forEach { input ->
            solutionInstance.solve(input)
            val output = solutionInstance.solve(input)
            solutionInstance.getOutput(input).writeText(output)
            println("Solution of Level ${solutionInstance.level} (${solutionInstance.javaClass.simpleName.removeTrailingNumbers()}): $output")
        }

        val endTime = System.currentTimeMillis().toInt()
        println("Level ${solutionInstance.level} took ${(endTime - startTime).toString().substringBefore(".")}ms\\n")
        total += endTime - startTime
    }
    println("Total time: ${total.toString().substringBefore(".")}ms")
}

fun getSolutions(): List<Solution> {
    val solutionsDir = File("src/main/kotlin/solutions")
    val currentFiles = solutionsDir.listFiles()?.map { it.name } ?: emptyList()

    return currentFiles
        .filter { it.endsWith(".kt") && !it.equals("Contest.kt") }
        .map { it.removeSuffix(".kt") }
        .map {
            Class.forName("solutions.$it").getDeclaredConstructor().newInstance() as Solution
        }.sortedBy { it.level }
}

fun unzipInput() {
    val resourcesDir = File("src/main/resources")
    resourcesDir.listFiles()?.forEach { file ->
        if (file.extension == "zip") {
            val outputFolder = resourcesDir.resolve(file.nameWithoutExtension)
            outputFolder.mkdir()
            ZipFile(file).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val outputFile = outputFolder.resolve(entry.name)
                    if (entry.isDirectory) {
                        outputFile.mkdirs()
                    } else {
                        zip.getInputStream(entry).use { input ->
                            Files.copy(input, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        }
                    }
                }
            }
            file.delete()
        }
    }
}