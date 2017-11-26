package lox

import lox.expressions.*
import lox.TokenType.*

class Interpreter : Visitor<Any?> {
  fun interpret(expr: Expr) {
    try {
      val value = evaluate(expr)
      println(stringify(value))
    } catch (error: RuntimeError) {
      Lox.runtimeError(error)
    }
  }

  override fun visitBinaryExpr(binary: Binary): Any? {
    val left = evaluate(binary.left)
    val right = evaluate(binary.right)

    return when (binary.operator.type) {
      MINUS -> {
        checkNumberOperands(binary.operator, left, right)
        (left as Double) - (right as Double)
      }
      PLUS -> {
        if (left is Double && right is Double) left + right
        else if (left is String && right is String) left + right
        else throw RuntimeError(binary.operator, "Operands must be two numbers or two strings.")
      }
      SLASH -> {
        checkNumberOperands(binary.operator, left, right)
        (left as Double) / (right as Double)
      }
      STAR -> {
        checkNumberOperands(binary.operator, left, right)
        (left as Double) * (right as Double)
      }
      GREATER -> {
        checkNumberOperands(binary.operator, left, right)
        (left as Double) > (right as Double)
      }
      GREATER_EQUAL -> {
        checkNumberOperands(binary.operator, left, right)
        (left as Double) >= (right as Double)
      }
      LESS -> {
        checkNumberOperands(binary.operator, left, right)
        (left as Double) < (right as Double)
      }
      LESS_EQUAL -> {
        checkNumberOperands(binary.operator, left, right)
        (left as Double) <= (right as Double)
      }
      BANG_EQUAL -> !isEqual(left, right)
      EQUAL_EQUAL -> isEqual(left, right)
      else -> null
    }
  }

  override fun visitUnaryExpr(unary: Unary): Any? {
    val right = evaluate(unary.right)
    return when (unary.operator.type) {
      BANG -> !isTruthy(right)
      MINUS -> {
        checkNumberOperand(unary.operator, right)
        -(right as Double)
      }
      else -> null
    }
  }

  override fun visitLiteralExpr(literal: Literal): Any? = literal.value

  override fun visitGroupingExpr(grouping: Grouping): Any? = evaluate(grouping.expression)

  private fun checkNumberOperand(operator: Token, operand: Any?) {
    if (operand !is Double) {
      throw RuntimeError(operator, "Operand must be a number.")
    }
  }

  private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
    if (left !is Double || right !is Double) {
      throw RuntimeError(operator, "Operands must be numbers.")
    }
  }

  private fun evaluate(expr: Expr): Any? = expr.accept(this)

  private fun isTruthy(obj: Any?): Boolean {
    if (obj == null) return false
    if (obj is Boolean) return obj
    return true
  }

  private fun isEqual(left: Any?, right: Any?): Boolean {
    if (left == null && right == null) return true
    if (left == null) return false
    return left == right
  }

  private fun stringify(obj: Any?): String {
    if (obj == null) return "nil"
    if (obj is Double) {
      val text = obj.toString()
      if (text.endsWith(".o")) {
        return text.substring(0, text.length - 2)
      }
      return text
    }
    return obj.toString()
  }
}
