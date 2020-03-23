package org.dmg.storyteller.fact

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

class SimpleContext extends Context {
  private val facts = ListBuffer.empty[Fact]
  private val links = ListBuffer.empty[ContextLike]

  override def :+(fact: Fact): Context = {
    if (fact.isComplex) {
      throw new UnsupportedOperationException("Cannot add complex fact " + fact)
    }
    facts.append(fact)
    this
  }

  override def :-(fact: Fact): Context = {
    val i = facts.indexOf(fact)
    if (i >= 0) {
      facts.remove(i)
    }
    this
  }

  override def ++(link: Context): Context = {
    links.append(link)
    this
  }

  override def relations: Iterable[ContextLike] = links

  override def conform(expression: String): Boolean = facts.exists(_ conform expression)

  override def superposition: Seq[ContextLike] = WeakAndFact(facts)
    .toLiterals
    .map(_.map(Fact stringToFact).toSeq)
    .map(facts ⇒ {
      val context = new SimpleContext
      context.facts.append(facts: _*)
      context.links.append(links: _*)
      context
    })
}