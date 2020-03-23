package org.dmg.storyteller.fact

trait Context {
  def conform(expression: String): Boolean

  def in[A](path: Fact)(f: Context ⇒ A): Traversable[A]

  def superposition: Seq[Context]
}
