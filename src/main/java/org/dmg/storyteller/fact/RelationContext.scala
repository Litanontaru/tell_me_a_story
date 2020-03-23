package org.dmg.storyteller.fact

class RelationContext(val link: Context, relation: ContextLike) extends RelationContextLike(link, relation) with Context {
  override def :+(fact: Fact): Context = link :+ fact

  override def :-(fact: Fact): Context = link :- fact

  override def link(next: Context): Context = link.link(next)
}