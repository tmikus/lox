package lox

import lox.expressions.*
import lox.expressions.Expr
import lox.expressions.Unary
import lox.expressions.Binary


class AstPrinter : Visitor<String> {
  fun print(expr: Expr): String {
    return expr.accept(this)
  }

  override fun visitGroupingExpr(grouping: Grouping): String {
    return parenthesize("group", grouping.expression)
  }

  override fun visitBinaryExpr(binary: Binary): String {
    return parenthesize(binary.operator.lexeme, binary.left, binary.right)
  }

  override fun visitUnaryExpr(unary: Unary): String {
    return parenthesize(unary.operator.lexeme, unary.right)
  }

  override fun visitLiteralExpr(literal: Literal): String {
    if (literal.value == null) return "nil"
    return literal.value.toString()
  }

  private fun parenthesize(lexeme: String, vararg exprs: Expr): String {
    val builder = StringBuilder()
    builder.append("(").append(lexeme)
    exprs.forEach { expr ->
      builder.append(" ").append(expr.accept(this))
    }
    builder.append(")")
    return builder.toString()
  }
}

fun main(args: Array<String>) {
  val expression = Binary(
    Unary(
      Token(TokenType.MINUS, "-", null, 1),
      Literal(123)
    ),
    Token(TokenType.STAR, "*", null, 1),
    Grouping(Literal(45.67))
  )

  println(AstPrinter().print(expression))
}