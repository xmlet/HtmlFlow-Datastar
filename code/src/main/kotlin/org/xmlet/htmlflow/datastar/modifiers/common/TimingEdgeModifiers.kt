package org.xmlet.htmlflow.datastar.modifiers.common

import org.xmlet.htmlflow.datastar.modifiers.base.ModifierScope

class TimingEdgeModifiers(
    private val scope: ModifierScope,
) {
    fun leading() = scope.addModifier(".leading")

    fun noTrailing() = scope.addModifier(".notrailing")
}
