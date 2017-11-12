package lox.tools

import java.io.PrintWriter

fun main(args: Array<String>) {
	if (args.size != 1) {
		System.err.println("Usage: generate_ast <output_directory>")
		System.exit(1)
	}
	val outputDir = args[0]
	defineAst(outputDir, "Expr", hashMapOf(
			"Binary" to "val left: Expr, val operator: Token, val right: Expr",
			"Grouping" to "val expression: Expr",
			"Literal" to "val value: Any",
			"Unary" to "val operator: Token, val right: Expr"
	))
}

fun defineAst(outputDir: String, baseName: String, types: HashMap<String, String>) {
	val path = "$outputDir/$baseName.kt"
	val writer = PrintWriter(path, "UTF-8")

	writer.println("package lox.expressions")
	writer.println()
	writer.println("import lox.Token")
	writer.println()
	writer.println("abstract class $baseName() {")
  writer.println("  abstract fun <R> accept(visitor: Visitor<R>): R")
  writer.println("}")

  defineVisitor(writer, baseName, types)

	types.forEach { (className, fields) -> defineType(writer, baseName, className, fields) }

	writer.println()
	writer.close()
}

fun defineVisitor(writer: PrintWriter, baseName: String, types: HashMap<String, String>) {
  writer.println()
  writer.println("interface Visitor<R> {")
  types.forEach { (className, _) ->
    writer.println("  fun visit$className$baseName(${className.toLowerCase()}: $className): R")
  }
  writer.println("}")
}

fun defineType(writer: PrintWriter, baseName: String, className: String, fields: String) {
	writer.println()
	writer.println("data class $className($fields): $baseName() {")
  writer.println("  override fun <R> accept(visitor: Visitor<R>): R {")
  writer.println("    return visitor.visit$className$baseName(this)")
  writer.println("  }")
  writer.println("}")
}
