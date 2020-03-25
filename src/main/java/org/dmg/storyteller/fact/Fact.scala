package org.dmg.storyteller.fact

import scala.language.{implicitConversions, postfixOps}

trait Fact {
  def suit(context: ContextLike): Boolean

  /**
    * Show if this Fact can be transformed to Literals and can open parentheses for negation
    *
    * @return true - if it cannot be transformed to literals
    */
  def isComplex: Boolean = false

  def conform(expression: String): Boolean = throw new UnsupportedOperationException

  def toLiterals: Seq[Seq[String]] = throw new UnsupportedOperationException

  def openParenthesesForNegation: Fact = throw new UnsupportedOperationException

  //--- USE ONLY TO BUILD PREDICATE EXPRESSION -------------------------------------------------------------------------

  def unary_!! : Fact = StrictNegativeFact(this)

  def &&(fact: Fact): Fact = StrictAndFact(Seq(this, fact))

  def ||(fact: Fact): Fact = StrictOrFact(Seq(this, fact))

  //--------------------------------------------------------------------------------------------------------------------

  def unary_! : Fact = WeakNegativeFact(this)

  def &(fact: Fact): Fact = WeakAndFact(Seq(this, fact))

  def |(fact: Fact): Fact = WeakOrFact(Seq(this, fact))

  def unary_~ : Fact = MayByFact(this)

  //--------------------------------------------------------------------------------------------------------------------

  def ::(path: Fact): Fact = PathFact(this, path)

  def :::(path: Fact): Fact = AllPathFact(this, path)
}

//----------------------------------------------------------------------------------------------------------------------

object NoFact extends Fact {
  override def suit(context: ContextLike): Boolean = true

  override def conform(expression: String): Boolean = false

  override def toLiterals: Seq[Seq[String]] = Seq(Seq())

  override def openParenthesesForNegation: Fact = AllFacts

  override def toString: String = "NO"
}

object AllFacts extends Fact {
  override def suit(context: ContextLike): Boolean = false

  override def conform(expression: String): Boolean = true

  override def toLiterals: Seq[Seq[String]] = Seq(Seq(""))

  override def openParenthesesForNegation: Fact = NoFact

  override def toString: String = "ALL"
}

private[fact] case class SymbolFact(expression: String) extends Fact {
  override def suit(context: ContextLike): Boolean = context.superposition.exists(_ has expression)

  override def conform(expression: String): Boolean = this.expression == expression

  override def toLiterals: Seq[Seq[String]] = Seq(Seq(expression))

  override def openParenthesesForNegation: Fact = SymbolFact("!" + expression)

  override def toString: String = s"F($expression)"
}

private[fact] case class MayByFact(fact: Fact) extends Fact {
  private val inner = WeakAndFact(Seq(fact, !fact))

  override def suit(context: ContextLike): Boolean = inner suit context

  override def conform(expression: String): Boolean = inner conform expression

  override def toLiterals: Seq[Seq[String]] = inner.toLiterals

  override def openParenthesesForNegation: Fact = inner.openParenthesesForNegation

  override def toString: String = s"~$fact"
}

//----------------------------------------------------------------------------------------------------------------------

private[fact] case class PathFact(fact: Fact, path: Fact) extends Fact {
  override def suit(context: ContextLike): Boolean = context.relations filter (path suit) exists (fact suit)

  override def isComplex: Boolean = true

  override def toString: String = s"$path :: $fact"
}

private[fact] case class AllPathFact(fact: Fact, path: Fact) extends Fact {
  override def suit(context: ContextLike): Boolean = {
    val contexts = context.relations filter (path suit)
    contexts.nonEmpty && (contexts forall (fact suit))
  }

  override def isComplex: Boolean = true

  override def toString: String = s"$path ::: $fact"
}

//----------------------------------------------------------------------------------------------------------------------

private[fact] case class StrictNegativeFact(fact: Fact) extends Fact {
  override def suit(context: ContextLike): Boolean = !(fact suit context)

  override def isComplex: Boolean = true

  override def unary_!! : Fact = fact

  override def toString: String = s"!!$fact"
}

private[fact] case class StrictAndFact(facts: Seq[Fact]) extends Fact {
  override def suit(context: ContextLike): Boolean = context.superposition.exists(c ⇒ facts.forall(_ suit c))

  override def isComplex: Boolean = true

  override def &&(fact: Fact): Fact = StrictAndFact(facts :+ fact)

  override def toString: String = facts.mkString("(", " && ", ")")
}

private[fact] case class StrictOrFact(facts: Seq[Fact]) extends Fact {
  override def suit(context: ContextLike): Boolean = context.superposition.exists(c ⇒ facts.exists(_ suit c))

  override def isComplex: Boolean = true

  override def ||(fact: Fact): Fact = StrictOrFact(facts :+ fact)

  override def toString: String = facts.mkString("(", " || ", ")")
}

//----------------------------------------------------------------------------------------------------------------------

private[fact] case class WeakNegativeFact(fact: Fact) extends Fact {
  override def suit(context: ContextLike): Boolean = context.superposition.exists(c ⇒ !(fact suit c))

  override def conform(expression: String): Boolean = !fact.conform(expression)

  override def toLiterals: Seq[Seq[String]] = fact.openParenthesesForNegation.toLiterals

  override def openParenthesesForNegation: Fact = fact

  override def unary_! : Fact = fact

  override def toString: String = s"!$fact"
}

private[fact] case class WeakAndFact(facts: Seq[Fact]) extends Fact {
  override def suit(context: ContextLike): Boolean = facts.forall(_ suit context)

  override def conform(expression: String): Boolean = facts.exists(_ conform expression)

  override def toLiterals: Seq[Seq[String]] = (Seq(Seq[String]()) /: facts.map(_.toLiterals))(for (a <- _; b <- _) yield a ++ b)

  override def openParenthesesForNegation: Fact = WeakOrFact(facts.map(!_))

  override def &(fact: Fact): Fact = WeakAndFact(facts :+ fact)

  override def toString: String = facts.mkString("(", " & ", ")")
}

private[fact] case class WeakOrFact(facts: Seq[Fact]) extends Fact {
  override def suit(context: ContextLike): Boolean = facts.exists(_ suit context)

  override def conform(expression: String): Boolean = facts.exists(_ conform expression)

  override def toLiterals: Seq[Seq[String]] =
    (Seq(Seq[String]()) /: facts.map(_.toLiterals)) { (r, l) ⇒ r ++ (for (a <- r; b <- l) yield a ++ b) }
      .filter(_.nonEmpty)

  override def openParenthesesForNegation: Fact = WeakAndFact(facts.map(!_))

  override def |(fact: Fact): Fact = WeakOrFact(facts :+ fact)

  override def toString: String = facts.mkString("(", " | ", ")")
}

//----------------------------------------------------------------------------------------------------------------------

object Fact {
  implicit def symbolToFact(symbol: Symbol): Fact = SymbolFact(symbol.name)

  implicit def stringToFact(string: String): Fact = if (string.isEmpty) AllFacts else SymbolFact(string)
}