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

  implicit def toContext(path: Fact): Context = links find (path suit) match {
    case Some(context) ⇒ context
    case None ⇒
      val context = new SimpleContext()
      links.append(context)
      context
  }

  implicit def toContext(path: String): Context = toContext(Fact.stringToFact(path))
}
