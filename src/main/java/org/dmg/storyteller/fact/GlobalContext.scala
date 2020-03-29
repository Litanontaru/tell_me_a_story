package org.dmg.storyteller.fact

import scala.collection.mutable.ListBuffer
import scala.language.{implicitConversions, postfixOps}

/**
  * TODO Write JavaDoc please.
  *
  * @author Andrei_Yakushin
  */
class GlobalContext {
  private val links = ListBuffer.empty[Context]

  private val suitContext: ContextLike = new ContextLike {
    override def has(expression: String): Boolean = false

    override def superposition: Seq[ContextLike] = Seq(this)

    override def relations: Iterable[ContextLike] = links
  }

  implicit def toContext(path: Fact): Context = {
    val contexts = links filter (path suit)
    if (contexts.isEmpty) {
      val context = SimpleContext(path)
      links.append(context)
      context
    } else {
      new CompositeContext(contexts)
    }
  }

  implicit def toContext(path: String): Context = toContext(Fact.stringToFact(path))

  implicit def toContext(path: Symbol): Context = toContext(Fact.symbolToFact(path))

  implicit def suit(fact: Fact): Boolean = fact suit suitContext
}