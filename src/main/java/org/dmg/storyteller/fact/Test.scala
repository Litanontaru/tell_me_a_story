package org.dmg.storyteller.fact

import org.dmg.storyteller.fact.Fact._

@deprecated
object Test {
  def main(args: Array[String]): Unit = {
    val globalContext = new GlobalContext()

    import globalContext._

    def test(fact: Fact): Unit = {
      if (fact) {
        println(fact)
      } else {
        println(s"no $fact")
      }
    }

    'A add 'a | 'b

    test("A" :: ('a && 'b))

    ('A | 'D) link ('B / 'C)    //todo create superposition of Context to return several contexts when they called
    ('A | 'D) link ('B / 'E)
    'E add 'C

    test('D :: 'B :: 'C)
    test('A :: 'B ::: 'C)
    test('A :: 'B ::: 'E)
    test(!('A :: 'B ::: 'E))
  }
}