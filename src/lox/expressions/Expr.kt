package lox.expressions

import lox.Token

abstract class Expr {
  abstract fun <R> accept(visitor: Visitor<R>): R
}

interface Visitor<out R> {
  fun visitGroupingExpr(grouping: Grouping): R
  fun visitBinaryExpr(binary: Binary): R
  fun visitUnaryExpr(unary: Unary): R
  fun visitLiteralExpr(literal: Literal): R
}

data class Grouping(val expression: Expr) : Expr() {
  override fun <R> accept(visitor: Visitor<R>): R {
    return visitor.visitGroupingExpr(this)
  }
}

data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr() {
  override fun <R> accept(visitor: Visitor<R>): R {
    return visitor.visitBinaryExpr(this)
  }
}

data class Unary(val operator: Token, val right: Expr) : Expr() {
  override fun <R> accept(visitor: Visitor<R>): R {
    return visitor.visitUnaryExpr(this)
  }
}

data class Literal(val value: Any?) : Expr() {
  override fun <R> accept(visitor: Visitor<R>): R {
    return visitor.visitLiteralExpr(this)
  }
}
