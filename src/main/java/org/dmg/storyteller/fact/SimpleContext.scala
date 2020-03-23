package org.dmg.storyteller.fact

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

class SimpleContext extends Context {
  private val facts = ListBuffer.empty[Fact]
  private val relations = ListBuffer.empty[Context]

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

  override def link(next: Context): Context = ???

  override def conform(expression: String): Boolean = facts.exists(_ conform expression)

  override def in[A](path: Fact)(f: ContextLike ⇒ A): Traversable[A] = ???

  override def superposition: Seq[ContextLike] = WeakAndFact(facts)
    .toLiterals
    .map(_.map(Fact stringToFact).toSeq)
    .map(facts ⇒ {
      val context = new SimpleContext
      context.facts.append(facts: _*)
      context.relations.append(relations: _*)
      context
    })
}
