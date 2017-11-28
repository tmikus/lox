package lox

import lox.expressions.*
import lox.TokenType.*

class Interpreter : ExprVisitor<Any?>, StmtVisitor<Unit> {
  private val environment = Environment()

  override fun visitExpressionStmt(stmt: Expression) {
    evaluate(stmt.expression)
  }

  override fun visitPrintStmt(stmt: Print) {
    val value = evaluate(stmt.expression)
    println(stringify(value))
  }

  override fun visitVarStmt(stmt: Var) {
    val value = if (stmt.initializer != null) evaluate(stmt.initializer) else null
    environment.define(stmt.name.lexeme, value)
  }

  fun interpret(statements: List<Stmt>) {
    try {
      statements.forEach { statement -> execute(statement) }
    } catch (error: RuntimeError) {
      Lox.runtimeError(error)
    }
  }

  override fun visitAssignExpr(expr: Assign): Any? {
    val value = evaluate(expr.value)
    environment.assign(expr.name, value)
    return value
  }

  override fun visitBinaryExpr(expr: Binary): Any? {
    val left = evaluate(expr.left)
    val right = evaluate(expr.right)

    return when (expr.operator.type) {
      MINUS -> {
        checkNumberOperands(expr.operator, left, right)
        (left as Double) - (right as Double)
      }
      PLUS -> {
        if (left is Double && right is Double) left + right
        else if (left is String && right is String) left + right
        else throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
      }
      SLASH -> {
        checkNumberOperands(expr.operator, left, right)
        (left as Double) / (right as Double)
      }
      STAR -> {
        checkNumberOperands(expr.operator, left, right)
        (left as Double) * (right as Double)
      }
      GREATER -> {
        checkNumberOperands(expr.operator, left, right)
        (left as Double) > (right as Double)
      }
      GREATER_EQUAL -> {
        checkNumberOperands(expr.operator, left, right)
        (left as Double) >= (right as Double)
      }
      LESS -> {
        checkNumberOperands(expr.operator, left, right)
        (left as Double) < (right as Double)
      }
      LESS_EQUAL -> {
        checkNumberOperands(expr.operator, left, right)
        (left as Double) <= (right as Double)
      }
      BANG_EQUAL -> !isEqual(left, right)
      EQUAL_EQUAL -> isEqual(left, right)
      else -> null
    }
  }

  override fun visitUnaryExpr(expr: Unary): Any? {
    val right = evaluate(expr.right)
    return when (expr.operator.type) {
      BANG -> !isTruthy(right)
      MINUS -> {
        checkNumberOperand(expr.operator, right)
        -(right as Double)
      }
      else -> null
    }
  }

  override fun visitLiteralExpr(expr: Literal): Any? = expr.value

  override fun visitGroupingExpr(expr: Grouping): Any? = evaluate(expr.expression)

  override fun visitVariableExpr(expr: Variable): Any? = environment.get(expr.name)

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

  private fun execute(stmt: Stmt) = stmt.accept(this)

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
