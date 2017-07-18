package ru.kest.plugin.orika.entity

/**
 * Prepeared import statements
 * 
 * Created by KKharitonov on 14.07.2017.
 */
class Imports {

    private val imports = mutableSetOf<String>()

    fun add(className: String) : Imports {
        imports.add(import(className))
        return this
    }

    fun build() : String {
        return imports.joinToString(separator = "\n")
    }

    private fun import(className: String) : String = "import $className"

}