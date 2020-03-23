package org.dmg.storyteller.fact

trait Context extends ContextLike {
  def :+(fact: Fact): Context

  def :-(fact: Fact): Context

  def ++(next: Context): Context
}