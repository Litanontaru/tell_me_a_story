package org.dmg.storyteller.fact

import org.dmg.storyteller.fact.Fact._

@deprecated
object Test {
  def main(args: Array[String]): Unit = {
    val globalContext = new GlobalContext()

    import globalContext._

    ('A | 'D) link ('B / 'C)
    ('A | 'D) link ('B / 'E)
    'E add 'C

    if ('D :: 'B :: 'C) {
      println("A")
    }

    if ('A :: 'B ::: 'C) {
      println("B")
    }

    if ('A :: 'B ::: 'E) {
      println("C")
    }

    if (!('A :: 'B ::: 'E)) {
      println("D")
    }
  }
}