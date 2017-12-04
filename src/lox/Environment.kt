package lox

class Environment(private val enclosing: Environment? = null) {
  private val values = HashMap<String, Any?>()

  fun assign(name: Token, value: Any?) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value)
      return
    }
    if (enclosing != null) {
      enclosing.assign(name, value)
      return
    }
    throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
  }

  fun define(name: String, value: Any?) {
    values.put(name, value)
  }

  fun get(name: Token): Any? {
    if (values.containsKey(name.lexeme)) {
      return values[name.lexeme]
    }
    if (enclosing != null) {
      return enclosing.get(name)
    }
    throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
  }
}