package ru.kest.plugin.orika.test

import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassReferenceType
import ru.kest.plugin.orika.entity.Field
import ru.kest.plugin.orika.entity.MappingClasses
import ru.kest.plugin.orika.psi.PsiUtils

/**
 * Create groovy unit test
 *
 * Created by KKharitonov on 01.07.2017.
 */
class TestMethodCreator(val classes: MappingClasses, val project: Project) {

    private val log = Logger.getInstance(TestMethodCreator::class.java)

    private val METHOD_TEMPLATE = "TestMethod.groovy"

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
        params.put("FIELDS", getClassFields(classes.sourceClass))

        return params
    }

    private fun getClassFields(sourceClass: PsiClass): List<Field> {
        val result = ArrayList<Field>()

        for (property in sourceClass.allFields) {
            log.info("${sourceClass.name}: $property")
            result.add(calcFieldByType(property))
        }
        return result
    }

    private fun calcFieldByType(psiField: PsiField) : Field {
        val fieldName = psiField.name ?: ""
        val type = psiField.type
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
            else -> {
                if (isCollection(type)) {
                    if (type is PsiClassReferenceType && type.parameters.isNotEmpty()) {
                        val genericType = type.parameters[0]
                        return Field(
                                fieldName,
                                "[new ${genericType.canonicalText}(",
                                getClassFields(PsiUtils.getClass(genericType)!!),
                                ")]"
                        )
                    } else {
                        return defaultField(fieldName, fieldName)
                    }
                } else { // Object
                    return Field(
                            fieldName,
                            "new ${type.canonicalText}(",
                            getClassFields(PsiUtils.getClass(type)!!),
                            ")"
                    )
                }
            }
        }
    }

    private fun defaultField(name: String, value: String?) : Field{
        return Field(name, "'$value'")
    }

    private fun isCollection(type: PsiType) : Boolean {
        return PsiUtils.isImplements(type, "java.util.Collection")
    }

}