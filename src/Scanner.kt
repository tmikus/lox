import TokenType.*

class Scanner(val source: String) {
	val tokens: MutableList<Token> = ArrayList()
	var start: Int = 0
	var current: Int = 0
	var line: Int = 0

	fun scanTokens(): List<Token> {
		while (!isAtEnd()) {
			start = current
			scanToken()
		}
		tokens.add(Token(EOF, "", null, line))
		return tokens
	}

	private fun scanToken() {
		val c = advance()
		when (c) {
			'(' -> addToken(LEFT_PAREN)
			')' -> addToken(RIGHT_PAREN)
			'{' -> addToken(LEFT_BRACE)
			'}' -> addToken(RIGHT_BRACE)
			',' -> addToken(COMMA)
			'.' -> addToken(DOT)
			'-' -> addToken(MINUS)
			'+' -> addToken(PLUS)
			';' -> addToken(SEMICOLON)
			'*' -> addToken(STAR)
			'!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
			'=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
			'<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
			'>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
			'/' -> if (match('/')) {
				while (!isAtEnd() && peek() != '\n') advance()
			} else {
				addToken(SLASH)
			}
			' ', '\r', '\t' -> {}
			'\n' -> line++
			'"' -> string()
			else ->
				if (isDigit(c)) number()
				else if (isAlpha(c)) identifier()
				else Lox.error(line, "Unexpected character.")
		}
	}

	private fun identifier() {
		while (isAlphaNumberic(peek())) advance()
		addToken(IDENTIFIER)
	}

	private fun number() {
		while (isDigit(peek())) advance()

		if (peek() == '.' && isDigit(peekNext())) {
			advance()
			while(isDigit(peek())) advance()
		}

		addToken(NUMBER, source.substring(start, current).toDouble())
	}

	private fun string() {
		while (!isAtEnd() && peek() != '"') {
			if (peek() == '\n') line++
			advance()
		}

		if (isAtEnd()) {
			Lox.error(line, "Unterminated string.")
			return
		}

		advance()

		val value = source.substring(start + 1, current - 1)
		addToken(STRING, value)
	}

	private fun match(expected: Char): Boolean {
		if (isAtEnd()) return false
		if (source[current] != expected) return false
		current++
		return true
	}

	private fun peek(): Char {
		if (isAtEnd()) return '\u0000'
		return source[current]
	}

	private fun peekNext(): Char {
		if (current + 1 >= source.length) return '\u0000'
		return source[current + 1]
	}

	private fun isAlpha(c: Char): Boolean {
		return (c in 'a'..'z')
			|| (c in 'A'..'Z')
		  || (c == '_')
	}

	private fun isAlphaNumberic(c: Char): Boolean {
		return isAlpha(c) || isDigit(c)
	}

	private fun isDigit(c: Char): Boolean {
		return c in '0'..'9'
	}

	private fun addToken(type: TokenType) {
		addToken(type, null)
	}

	private fun addToken(type: TokenType, literal: Any?) {
		val text = source.substring(start, current)
		tokens.add(Token(type, text, literal, line))
	}

	private fun advance(): Char {
		current++
		return source[current - 1]
	}

	private fun isAtEnd(): Boolean {
		return current >= source.length
	}
}