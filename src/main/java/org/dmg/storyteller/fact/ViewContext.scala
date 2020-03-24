package org.dmg.storyteller.fact

class ViewContext(base: ContextLike, view: Context) extends ViewContextLike(base, view) with Context {
  override def add(fact: Fact): Context = view add fact

  override def remove(fact: Fact): Context = view remove fact

  override def link(next: Context): Context = view.link(next)
}