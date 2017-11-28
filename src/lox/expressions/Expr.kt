package lox.expressions

import lox.Token

abstract class Expr {
  abstract fun <R> accept(visitor: ExprVisitor<R>): R
}

interface ExprVisitor<out R> {
  fun visitAssignExpr(expr: Assign): R
  fun visitGroupingExpr(expr: Grouping): R
  fun visitBinaryExpr(expr: Binary): R
  fun visitVariableExpr(expr: Variable): R
  fun visitUnaryExpr(expr: Unary): R
  fun visitLiteralExpr(expr: Literal): R
}

data class Assign(val name: Token, val value: Expr): Expr() {
  override fun <R> accept(visitor: ExprVisitor<R>): R {
    return visitor.visitAssignExpr(this)
  }
}

data class Grouping(val expression: Expr): Expr() {
  override fun <R> accept(visitor: ExprVisitor<R>): R {
    return visitor.visitGroupingExpr(this)
  }
}

data class Binary(val left: Expr, val operator: Token, val right: Expr): Expr() {
  override fun <R> accept(visitor: ExprVisitor<R>): R {
    return visitor.visitBinaryExpr(this)
  }
}

data class Variable(val name: Token): Expr() {
  override fun <R> accept(visitor: ExprVisitor<R>): R {
    return visitor.visitVariableExpr(this)
  }
}

data class Unary(val operator: Token, val right: Expr): Expr() {
  override fun <R> accept(visitor: ExprVisitor<R>): R {
    return visitor.visitUnaryExpr(this)
  }
}

data class Literal(val value: Any?): Expr() {
  override fun <R> accept(visitor: ExprVisitor<R>): R {
    return visitor.visitLiteralExpr(this)
  }
}
