package lox.expressions

import lox.Token

abstract class Stmt {
  abstract fun <R> accept(visitor: StmtVisitor<R>): R
}

interface StmtVisitor<out R> {
  fun visitBlockStmt(stmt: Block): R
  fun visitPrintStmt(stmt: Print): R
  fun visitExpressionStmt(stmt: Expression): R
  fun visitVarStmt(stmt: Var): R
}

data class Block(val statements: List<Stmt>): Stmt() {
  override fun <R> accept(visitor: StmtVisitor<R>): R {
    return visitor.visitBlockStmt(this)
  }
}

data class Print(val expression: Expr): Stmt() {
  override fun <R> accept(visitor: StmtVisitor<R>): R {
    return visitor.visitPrintStmt(this)
  }
}

data class Expression(val expression: Expr): Stmt() {
  override fun <R> accept(visitor: StmtVisitor<R>): R {
    return visitor.visitExpressionStmt(this)
  }
}

data class Var(val name: Token, val initializer: Expr?): Stmt() {
  override fun <R> accept(visitor: StmtVisitor<R>): R {
    return visitor.visitVarStmt(this)
  }
}
