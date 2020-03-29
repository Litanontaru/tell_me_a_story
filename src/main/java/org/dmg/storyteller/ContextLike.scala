package org.dmg.storyteller

trait ContextLike {
  def has(expression: String): Boolean

  def superposition: Seq[ContextLike]

  def relations: Iterable[ContextLike]
}