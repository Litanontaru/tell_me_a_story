package org.dmg.storyteller.fact

trait Context extends ContextLike {
  def add(fact: Fact): Context

  def remove(fact: Fact): Context

  def link(next: Context): Context

  def /(base: Context): Context = new ViewContext(base, this)
}