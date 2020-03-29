package org.dmg.storyteller

import org.dmg.storyteller.Fact._

@deprecated
object Test {
  def main(args: Array[String]): Unit = {
    val say_hellow_1 = Script('say_hello, 'A) { (in, g) ⇒
      import g._
      println(s"Hello ${w"world"}")
      in
    }
    val say_hellow_2 = Script('say_hello, 'D) { (in, _) ⇒
      println("Hello world!")
      in
    }

    val world_1 = Word('world) { _ ⇒ "WORLD" }
    val world_2 = Word('world) { _ ⇒ "world" }

    implicit val globalContext: GlobalContext = new GlobalContext(Seq(say_hellow_1, say_hellow_2), Seq(world_1, world_2))

    import globalContext._

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

    //test script call

    'D call 'say_hello
    'A call 'say_hello

  }
}