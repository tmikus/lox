package lox

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
  when {
    args.size > 1 -> println("Usage: jlox [script]")
    args.size == 1 -> Lox.runFile(args[0])
    else -> Lox.runPrompt()
  }
}

object Lox {
  private val interpreter = Interpreter()
  private var hadError = false
  private var hadRuntimeError = false

  fun runFile(path: String) {
    val bytes = Files.readAllBytes(Paths.get(path))
    runSource(String(bytes, Charset.defaultCharset()))
    if (hadError) System.exit(65)
    if (hadRuntimeError) System.exit(70)
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
    val tokens = scanner.scanTokens()
    val parser = Parser(tokens)
    val expression = parser.parse()

    if (hadError) return

    if (expression != null) {
      interpreter.interpret(expression)
    }
  }

  fun error(line: Int, message: String) {
    report(line, "", message)
  }

  fun error(token: Token, message: String) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end ", message);
    } else {
      report(token.line, " at '${token.lexeme}'", message);
    }
  }

  fun runtimeError(error: RuntimeError) {
    System.err.println("${error.message}\n[line ${error.token.line}]")
    hadRuntimeError = true
  }

  private fun report(line: Int, where: String, message: String) {
    System.err.println("[line $line] Error$where: $message")
    hadError = true
  }
}


