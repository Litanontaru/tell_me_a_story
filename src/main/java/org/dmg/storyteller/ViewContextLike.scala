package org.dmg.storyteller

private[storyteller] class ViewContextLike(base: ContextLike, view: ContextLike) extends ContextLike {
  override def has(expression: String): Boolean = (view has expression) || (base has expression)

  override def superposition: Seq[ContextLike] =
    for (l ← view.superposition; r ← base.superposition) yield new ViewContextLike(r, l)

  override def relations: Iterable[ContextLike] = view.relations ++ base.relations
}