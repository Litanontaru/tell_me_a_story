package org.dmg.storyteller.fact

import org.dmg.storyteller.fact.Fact._

@deprecated
object Test {
  def main(args: Array[String]): Unit = {
    val say_hellow_1 = Script('say_hello) { (in, _) ⇒
      println("Hello world")
      in
    }
    val say_hellow_2 = Script('say_hello) { (in, _) ⇒
      println("Hello world!")
      in
    }
    implicit val globalContext: GlobalContext = new GlobalContext(Seq(say_hellow_1, say_hellow_2))

    import globalContext._

    //test script call

    "" call 'say_hello

    //test facts and context implicits

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

    test('D :: 'B :: ('E || 'C && base('A) :: 'a))
    test('A :: 'B :: ('E || 'C && base('A) :: 'a))
    test('A :: 'B ::: 'C)
    test('A :: 'B ::: 'E)
    test(!('A :: 'B ::: 'E))
  }
}