package org.dmg.storyteller.fact

import org.dmg.storyteller.fact.Fact._

@deprecated
object Test {
  def main(args: Array[String]): Unit = {
    implicit val globalContext: GlobalContext = new GlobalContext()

    import globalContext._

    def test(fact: Fact): Unit = {
      if (fact) {
        println(s"+\t$fact")
      } else {
        println(s"-\t$fact")
      }
    }

    'A add 'a | 'b

    test("A" :: ('a && 'b))

    ('A | 'D) link ('B / 'C)
    ('A | 'D) link ('B / 'E)
    'E add 'C

    if ("Fact 1" || "Fact 2") {

    }

    test('D :: 'B :: ('E || 'C && base('A) :: 'a))
    test('A :: 'B :: ('E || 'C && base('A) :: 'a))
    test('A :: 'B ::: 'C)
    test('A :: 'B ::: 'E)
    test(!('A :: 'B ::: 'E))
  }
}