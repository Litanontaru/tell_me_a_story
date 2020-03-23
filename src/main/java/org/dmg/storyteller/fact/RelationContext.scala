package org.dmg.storyteller.fact

class RelationContext(val link: Context, relation: Context) extends RelationContextLike(link, relation) with Context {
  override def :+(fact: Fact): Context = link :+ fact

  override def :-(fact: Fact): Context = link :- fact

  override def link(next: Context): Context = link.link(next)
}

private class RelationContextLike(link: ContextLike, relation: ContextLike) extends ContextLike {
  override def conform(expression: String): Boolean = (link conform expression) || (relation conform expression)

  override def in[A](path: Fact)(f: ContextLike ⇒ A): Traversable[A] = (link in path)(f)

  override def superposition: Seq[ContextLike] =
    for (l ← link.superposition; r ← relation.superposition) yield new RelationContextLike(l, r)
}