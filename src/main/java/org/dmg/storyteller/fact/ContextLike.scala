package org.dmg.storyteller.fact

trait ContextLike {
  def has(expression: String): Boolean

  def superposition: Seq[ContextLike]

  def relations: Iterable[ContextLike]
}