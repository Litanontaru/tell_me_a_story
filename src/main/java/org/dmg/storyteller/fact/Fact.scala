package org.dmg.storyteller.fact

import scala.language.{implicitConversions, postfixOps}

trait Fact {
  def suit(context: ContextLike): Boolean

  def isComplex: Boolean = false

  def conform(expression: String): Boolean = throw new UnsupportedOperationException

  def toLiterals: Seq[Seq[String]] = throw new UnsupportedOperationException

  def openParenthesesForNegation: Fact = throw new UnsupportedOperationException

  //--------------------------------------------------------------------------------------------------------------------

  def unary_!! : Fact = StrictNegativeFact(this)

  def &&(fact: Fact): Fact = StrictAndFact(Seq(this, fact))

  def ||(fact: Fact): Fact = StrictOrFact(Seq(this, fact))

  //--------------------------------------------------------------------------------------------------------------------

  def unary_! : Fact = WeakNegativeFact(this)

  def &(fact: Fact): Fact = WeakAndFact(Seq(this, fact))

  def |(fact: Fact): Fact = WeakOrFact(Seq(this, fact))

  def unary_~ : Fact = !this & this

  //--------------------------------------------------------------------------------------------------------------------

  def ::(path: Fact): Fact = PathFact(this, path)
}

//----------------------------------------------------------------------------------------------------------------------

object NoFact extends Fact {
  override def suit(context: ContextLike): Boolean = true

  override def conform(expression: String): Boolean = false

  override def toLiterals: Seq[Seq[String]] = Seq(Seq())

  override def openParenthesesForNegation: Fact = AllFacts
}

object AllFacts extends Fact {
  override def suit(context: ContextLike): Boolean = false

  override def conform(expression: String): Boolean = true

  override def toLiterals: Seq[Seq[String]] = Seq(Seq(""))

  override def openParenthesesForNegation: Fact = NoFact
}

private[fact] case class SymbolFact(expression: String) extends Fact {
  override def suit(context: ContextLike): Boolean = context.superposition.exists(_ conform expression)

  override def conform(expression: String): Boolean = this.expression == expression

  override def toLiterals: Seq[Seq[String]] = Seq(Seq(expression))

  override def openParenthesesForNegation: Fact = SymbolFact("!" + expression)
}

//----------------------------------------------------------------------------------------------------------------------

private[fact] case class PathFact(fact: Fact, path: Fact) extends Fact {
  override def suit(context: ContextLike): Boolean = (context in path) { fact suit } exists identity

  override def isComplex: Boolean = true
}

//----------------------------------------------------------------------------------------------------------------------

private[fact] case class StrictNegativeFact(fact: Fact) extends Fact {
  override def suit(context: ContextLike): Boolean = !(fact suit context)

  override def isComplex: Boolean = true

  override def unary_!! : Fact = fact
}

private[fact] case class StrictAndFact(facts: Seq[Fact]) extends Fact {
  override def suit(context: ContextLike): Boolean = context.superposition.exists(c ⇒ facts.forall(_ suit c))

  override def isComplex: Boolean = true

  override def &&(fact: Fact): Fact = StrictAndFact(facts :+ fact)
}

private[fact] case class StrictOrFact(facts: Seq[Fact]) extends Fact {
  override def suit(context: ContextLike): Boolean = context.superposition.exists(c ⇒ facts.exists(_ suit c))

  override def isComplex: Boolean = true

  override def ||(fact: Fact): Fact = StrictOrFact(facts :+ fact)
}

//----------------------------------------------------------------------------------------------------------------------

private[fact] case class WeakNegativeFact(fact: Fact) extends Fact {
  override def suit(context: ContextLike): Boolean = context.superposition.exists(c ⇒ !(fact suit c))

  override def conform(expression: String): Boolean = !fact.conform(expression)

  override def toLiterals: Seq[Seq[String]] = fact.openParenthesesForNegation.toLiterals

  override def openParenthesesForNegation: Fact = fact

  override def unary_! : Fact = fact
}

private[fact] case class WeakAndFact(facts: Seq[Fact]) extends Fact {
  override def suit(context: ContextLike): Boolean = facts.forall(_ suit context)

  override def conform(expression: String): Boolean = facts.exists(_ conform expression)

  override def toLiterals: Seq[Seq[String]] = facts
    .map(_.toLiterals)
    .foldLeft(Seq(Seq[String]()))(for {a <- _; b <- _} yield a ++ b)

  override def openParenthesesForNegation: Fact = WeakOrFact(facts.map(!_))

  override def &(fact: Fact): Fact = WeakAndFact(facts :+ fact)
}

private[fact] case class WeakOrFact(facts: Seq[Fact]) extends Fact {
  override def suit(context: ContextLike): Boolean = facts.exists(_ suit context)

  override def conform(expression: String): Boolean = facts.exists(_ conform expression)

  override def toLiterals: Seq[Seq[String]] = facts.flatMap(_.toLiterals)

  override def openParenthesesForNegation: Fact = WeakAndFact(facts.map(!_))

  override def |(fact: Fact): Fact = WeakOrFact(facts :+ fact)
}

//----------------------------------------------------------------------------------------------------------------------

object Fact {
  implicit def stringToFact(string: String): Fact =
    if (string.isEmpty) AllFacts else SymbolFact(string)
}