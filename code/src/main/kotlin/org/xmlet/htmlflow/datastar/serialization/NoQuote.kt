package org.xmlet.htmlflow.datastar.serialization

/**
 * Marks a serializable String property to be emitted as raw JavaScript instead of a quoted string literal.
 *
 * This annotation is read with kotlin-reflect while formatting values for Datastar attributes.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class NoQuote
