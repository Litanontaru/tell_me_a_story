package org.dmg.storyteller.fact

trait ContextLike {
  def conform(expression: String): Boolean

  def in[A](path: Fact)(f: ContextLike ⇒ A): Traversable[A]

  def superposition: Seq[ContextLike]
}