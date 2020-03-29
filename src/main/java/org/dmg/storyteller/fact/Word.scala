package org.dmg.storyteller.fact

trait Word {
  def descriptor: Context

  def prerequisite: Fact

  def apply(call: Fact, globalContext: GlobalContext): Option[String] =
    if ((call suit descriptor) && (globalContext suit prerequisite)) Some(apply(globalContext)) else None

  private[fact] def apply(globalContext: GlobalContext): String
}

case class SimpleWord(descriptor: Context, prerequisite: Fact, script: GlobalContext ⇒ String) extends Word {
  override def apply(globalContext: GlobalContext): String = script(globalContext)
}

object Word {
  def apply(descriptor: Fact, prerequisite: Fact = NoFact)(script: GlobalContext ⇒ String): Word =
    SimpleWord(SimpleContext(descriptor), prerequisite, script)
}