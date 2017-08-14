package ru.kest.plugin.orika.test

import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import ru.kest.plugin.orika.entity.*
import ru.kest.plugin.orika.psi.PsiUtils
import java.lang.IllegalStateException

/**
 * Create groovy unit test
 *
 * Created by KKharitonov on 01.07.2017.
 */
class TestMethodCreator(val classes: MappingClasses, val project: Project) {

    val imports = Imports()

    private val log = Logger.getInstance(TestMethodCreator::class.java)

    private val METHOD_TEMPLATE = "TestMethod.groovy"
    private val MAX_NESTED_OBJECTS = 100

    private val dataGenerator = MockDataGenerator()

    fun generate() : String {
        val content = getMethodTemplate().getText(generateTemplateParams(classes))
        return content
    }


    private fun getMethodTemplate() : FileTemplate {
        return FileTemplateManager.getInstance(project).getCodeTemplate(METHOD_TEMPLATE)
    }

    private fun generateTemplateParams(classes: MappingClasses) : Map<String, Any> {
        val params = HashMap<String, Any>()
        params.put("SOURCE_CLASS", classes.sourceClass.name!!)
        params.put("DEST_CLASS", classes.destClass.name!!)
        params.put("METHOD_NAME", "${classes.sourceClass.name} to ${classes.destClass.name}")
        params.put("FIELDS", getClassFields(classes.sourceClass, 0))

        return params
    }

    private fun getClassFields(sourceClass: PsiClass, recursionCounter: Int): List<Field> {
        val result = ArrayList<Field>()

        for (psiField in sourceClass.allFields) {
            log.info("${sourceClass.name}: $psiField")
            result.add(fieldByType(psiField.name, psiField.type, recursionCounter + 1))
        }
        return result
    }

    private fun fieldByType(fieldName: String?, type: PsiType, recursionCounter: Int): Field {
        if (recursionCounter > MAX_NESTED_OBJECTS) {
            throw IllegalStateException("Recursion detected on parsing class ${classes.sourceClass}")
        }
        when (type.canonicalText) {
            "byte", "java.lang.Byte",
            "int", "java.lang.Integer",
            "long", "java.lang.Long",
            "short", "java.lang.Short" -> return Field(fieldName, dataGenerator.getNextInt().toString())
            "char", "java.lang.Character" -> return Field(fieldName, "'c'")
            "float", "java.lang.Float",
            "double", "java.lang.Double" -> return Field(fieldName, dataGenerator.getNextDouble().toString())
            "boolean", "java.lang.Boolean" -> return Field(fieldName, dataGenerator.getNextBoolean().toString())
            "java.lang.String" -> return Field(fieldName, "'$fieldName'")
            "java.util.Date", "javax.xml.datatype.XMLGregorianCalendar" -> {
                imports.add("com.luxoft.mmc.test.utils.DateUtil")
                return Field(fieldName, dataGenerator.getNextDate())
            }
            // TODO add array, Map support
            else -> {
                if (PsiUtils.isCollection(type)) {
                    if (type is PsiClassReferenceType && type.parameters.isNotEmpty()) {
                        val genericType = type.parameters[0]
                        imports.add(genericType.canonicalText)
                        return Field(
                                fieldName,
                                "[",
                                children = listOf(fieldByType(null, genericType, recursionCounter + 1)),
                                suffix = "]"
                        )
                    } else {
                        return defaultField(fieldName, fieldName)
                    }
                } else if (PsiUtils.isEnum(type)) {
                    val enumFields = PsiUtils.getClass(type)?.fields
                    if (enumFields != null && enumFields.isNotEmpty()) {
                        imports.add(type.canonicalText)
                        return Field(fieldName, "${type.presentableText}.${enumFields[0].name}")
                    } else return defaultField(fieldName, fieldName)
                } else if (type.canonicalText.startsWith("java")) {
                    return defaultField(fieldName, fieldName)
                } else { // Object
                    imports.add(type.canonicalText)
                    return Field(
                            fieldName,
                            "new ${type.presentableText}(",
                            getClassFields(PsiUtils.getClass(type)!!, recursionCounter + 1),
                            ")"
                    )
                }
            }
        }
    }

    private fun defaultField(name: String?, value: String?) : Field{
        return Field(name, "'$value'")
    }

}