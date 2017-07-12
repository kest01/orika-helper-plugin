package ru.kest.plugin.orika.entity

/**
 * Model class for method template
 * Velocity do not working with Kotlin data classes
 *
 * Created by KKharitonov on 10.07.2017.
 */
class Field {

    val name: String
    val value: String
    val children: List<Field>?
    val suffix: String?

    constructor(name: String, value: String) {
        this.name = name
        this.value = value
        children = null
        suffix = null
    }

    constructor(name: String, value: String, children: List<Field>, suffix: String) {
        this.name = name
        this.value = value
        this.children = children
        this.suffix = suffix
    }

    override fun toString(): String {
        return "Field{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", children=" + children +
                ", suffix='" + suffix + '\'' +
                '}'
    }
}
