package org.dmg.storyteller.fact

import org.dmg.storyteller.fact.Fact._

@deprecated
object Test {
  def main(args: Array[String]): Unit = {
    val globalContext = new GlobalContext()

    import globalContext._

    ('A | 'D) link ('B / 'C)

    if ("B" :: "C" suit 'D) {
      println("Good")
    }
  }
}