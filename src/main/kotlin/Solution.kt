import java.io.File
import javax.swing.JOptionPane
import javax.swing.UIManager

abstract class Solution {

    val level: Int = this.javaClass.simpleName.takeLastWhile { it.isDigit() }.toInt()
    val path: String = "src/main/resources/level$level"

    init {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getInput(): List<File> =
        File(path).takeIf { it.exists() }?.listFiles { file -> file.extension == "in" }?.toList()
            ?: requestAndProcessInputData()

    private fun requestAndProcessInputData(): List<File> {
        val rawInput = requestInputData()
        return processInputData(rawInput)
    }

    private fun requestInputData(): String {
        val questionIcon = UIManager.getIcon("OptionPane.questionIcon")
        val message = "Enter input data for level $level (use '?' to separate sublevels):"
        return JOptionPane.showInputDialog(null, message, "Level $level", JOptionPane.PLAIN_MESSAGE, questionIcon, null, null) as String? ?: ""
    }

    private fun processInputData(rawInput: String): List<File> =
        rawInput.split("?")
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { index, data ->
                File("$path/level${level}_${index + 1}.in").apply {
                    parentFile.mkdirs()
                    writeText(data)
                }
            }
            .plus(requestExampleData().takeIf { it.isNotEmpty() }?.let {
                File("$path/level${level}_example.in").apply {
                    parentFile.mkdirs()
                    writeText(it)
                }
            })
            .filterNotNull()  // Added this line to filter out any null values
            .toList()

    private fun requestExampleData(): String {
        val exampleText = "Example"
        val questionIcon = UIManager.getIcon("OptionPane.questionIcon")
        return JOptionPane.showInputDialog(null, "Enter example input data:", exampleText, JOptionPane.PLAIN_MESSAGE, questionIcon, null, null) as String? ?: ""
    }

    fun getOutput(inputFile: File): File = File(inputFile.path.replace(".in", ".out")).apply { createNewFile() }

    abstract fun solve(input: File): String
}
