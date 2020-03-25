package org.dmg.storyteller.fact

/**
  * TODO Write JavaDoc please.
  *
  * @author Andrei_Yakushin
  */
class CompositeContext(contexts: Seq[Context]) extends Context {
  override def add(fact: Fact): Context = {
    for (c ← contexts) c add fact
    this
  }

  override def remove(fact: Fact): Context = {
    for (c ← contexts) c remove fact
    this
  }

  override def link(next: Context): Context = {
    for (c ← contexts) c link next
    this
  }

  override def has(expression: String): Boolean = contexts exists (_ has expression)

  override def superposition: Seq[ContextLike] = contexts flatMap (_.superposition)

  override def relations: Iterable[ContextLike] = contexts flatMap (_.relations)
}