package org.dmg.storyteller.fact

private[fact] class ViewContextLike(base: ContextLike, view: ContextLike) extends ContextLike {
  override def conform(expression: String): Boolean = (view conform expression) || (base conform expression)

  override def superposition: Seq[ContextLike] =
    for (l ← view.superposition; r ← base.superposition) yield new ViewContextLike(r, l)

  override def relations: Iterable[ContextLike] = view.relations ++ base.relations
}