package lox

import lox.expressions.Expr
import lox.TokenType.*
import lox.expressions.Binary
import lox.expressions.Grouping
import lox.expressions.Literal
import lox.expressions.Unary

class Parser(private val tokens: List<Token>) {

  private class ParseError: RuntimeException()

  private var current: Int = 0

  fun parse(): Expr? {
    return try {
      expression()
    } catch (error: ParseError) {
      null
    }
  }

  private fun expression(): Expr = equality()

  private fun equality(): Expr {
    var expr = comparison()
    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      val operator = previous()
      val right = comparison()
      expr = Binary(expr, operator, right)
    }
    return expr
  }

  private fun comparison(): Expr {
    var expr = addition()
    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      val operator = previous()
      val right = addition()
      expr = Binary(expr, operator, right)
    }
    return expr;
  }

  private fun addition(): Expr {
    var expr = multiplication()
    while (match(MINUS, PLUS)) {
      val operator = previous()
      val right = multiplication()
      expr = Binary(expr, operator, right)
    }
    return expr
  }

  private fun multiplication(): Expr {
    var expr = unary()
    while (match(SLASH, STAR)) {
      val operator = previous()
      val right = unary()
      expr = Binary(expr, operator, right)
    }
    return expr
  }

  private fun unary(): Expr {
    if (match(BANG, MINUS)) {
      val operator = previous()
      val right = unary()
      return Unary(operator, right)
    }
    return primary()
  }

  private fun primary(): Expr {
    if (match(FALSE)) return Literal(false)
    if (match(TRUE)) return Literal(true)
    if (match(NIL)) return Literal(null)
    if (match(NUMBER, STRING)) {
      return Literal(previous().literal)
    }
    if (match(LEFT_PAREN)) {
      val expr = expression()
      consume(RIGHT_PAREN, "Expect ')' after expression.")
      return Grouping(expr)
    }
    throw error(peek(), "Expect expression.")
  }

  private fun match(vararg types: TokenType): Boolean {
    types.forEach { type ->
      if (check(type)) {
        advance()
        return true
      }
    }
    return false
  }

  private fun consume(type: TokenType, message: String): Token {
    if (check(type)) return advance()
    throw error(peek(), message)
  }

  private fun check(tokenType: TokenType): Boolean {
    if (isAtEnd()) return false
    return peek().type == tokenType
  }

  private fun advance(): Token {
    if (!isAtEnd()) current += 1
    return previous()
  }

  private fun isAtEnd(): Boolean = peek().type == EOF

  private fun peek(): Token = tokens[current]

  private fun previous(): Token = tokens[current - 1]

  private fun error(token: Token, message: String): ParseError {
    Lox.error(token, message)
    return ParseError()
  }

  private fun synchronize() {
    advance()
    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) return
      when (peek().type) {
        CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> return
        else -> advance()
      }
    }
  }
}