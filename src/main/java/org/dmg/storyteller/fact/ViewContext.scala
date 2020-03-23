package org.dmg.storyteller.fact

class ViewContext(base: ContextLike, view: Context) extends ViewContextLike(base, view) with Context {
  override def :+(fact: Fact): Context = view :+ fact

  override def :-(fact: Fact): Context = view :- fact

  override def link(next: Context): Context = view.link(next)
}