import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
	when {
		args.size > 1 -> println("Usage: jlox [script]")
		args.isEmpty() -> Lox.runFile(args[0])
		else -> Lox.runPrompt()
	}
}

object Lox {
	private var hadError = false

	fun runFile(path: String) {
		val bytes = Files.readAllBytes(Paths.get(path))
		runSource(String(bytes, Charset.defaultCharset()))
		if (hadError) System.exit(65)
	}

	fun runPrompt() {
		while (true) {
			print("> ")
			runSource(readLine().orEmpty())
			hadError = false
		}
	}

	private fun runSource(source: String) {
		val scanner = Scanner(source)
		scanner.scanTokens().forEach { token -> println(token) }
	}

	fun error(line: Int, message: String) {
		report(line, "", message)
	}

	private fun report(line: Int, where: String, message: String) {
		System.err.println("[line $line] Error$where: $message")
		hadError = true
	}
}


