package org.dmg.storyteller

/**
  * TODO Write JavaDoc please.
  *
  * @author Andrei_Yakushin
  */
trait Script {
  def descriptor: Context

  def prerequisite: Fact

  def apply(call: Fact, in: Context, globalContext: GlobalContext): Option[Context] =
    if ((call suit descriptor) && (prerequisite suit in)) Some(apply(in, globalContext)) else None

  private[storyteller] def apply(in: Context, globalContext: GlobalContext): Context
}

case class SimpleScript(descriptor: Context, prerequisite: Fact, script: (Context, GlobalContext) ⇒ Context) extends Script {
  override def apply(in: Context, globalContext: GlobalContext): Context = script(in, globalContext)
}

object Script {
  def apply(descriptor: Fact, prerequisite: Fact = NoFact)(script: (Context, GlobalContext) ⇒ Context): Script =
    SimpleScript(SimpleContext(descriptor), prerequisite, script)
}