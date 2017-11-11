import TokenType.*

class Scanner(val source: String) {
	val tokens: MutableList<Token> = ArrayList()
	var start: Int = 0
	var current: Int = 0
	var line: Int = 0

	public fun scanTokens(): List<Token> {
		while (!isAtEnd()) {
			start = current
			scanToken()
		}
		tokens.add(Token(EOF, "", null, line))
		return tokens
	}

	private fun scanToken() {
		when (advance()) {
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
			else -> Lox.error(line, "Unexpected character.")
		}
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