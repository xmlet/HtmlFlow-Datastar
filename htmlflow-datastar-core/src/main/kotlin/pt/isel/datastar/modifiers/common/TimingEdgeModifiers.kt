package pt.isel.datastar.modifiers.common

import pt.isel.datastar.modifiers.base.ModifierScope

class TimingEdgeModifiers(
    private val scope: ModifierScope,
) {
    fun leading() = scope.addModifier(".leading")

    fun noTrailing() = scope.addModifier(".notrailing")
}
