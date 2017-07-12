package ru.kest.plugin.orika.test

import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import org.jetbrains.plugins.groovy.GroovyFileType
import ru.kest.plugin.orika.entity.MappingClasses
import ru.kest.plugin.orika.entity.TestFile

/**
 * Create groovy unit test
 *
 * Created by KKharitonov on 01.07.2017.
 */
class TestClassCreator(val classes: MappingClasses, val testFile: TestFile, val project: Project) {

    val methodCreator = TestMethodCreator(classes, project)
//    private val LOG = Logger.getInstance(TestCreator::class.java)

    private val TEST_TEMPLATE = "TestClass.groovy"

    fun create() : PsiFile {
        val content = getClassTemplate().getText(generateTemplateParams())
        val file = createNewFile(content)
        return file
    }

    private fun createNewFile(content: String) : PsiFile {
        return PsiFileFactory.getInstance(project)
                .createFileFromText(testFile.className + ".groovy", GroovyFileType.GROOVY_FILE_TYPE, content)
    }

    private fun getClassTemplate() : FileTemplate {
        return FileTemplateManager.getInstance(project).getCodeTemplate(TEST_TEMPLATE)
    }

    private fun generateTemplateParams() : Map<String, String> {
        val params = HashMap<String, String>()
        params.put("PACKAGE_NAME", testFile.packageName)
        params.put("TEST_CLASSNAME", testFile.className)
        params.put("SOURCE_CLASS", classes.sourceClass.name!!)
        params.put("DEST_CLASS", classes.destClass.name!!)
        params.put("MAPPER_CLASS", classes.mapperClass.qualifiedName!!)
        params.put("IMPORTS", getImports())
        params.put("TEST_METHOD", getTestMethodContent())

        return params
    }

    private fun getTestMethodContent(): String {
        return methodCreator.generate()
    }

    private fun  getImports(): String {
//        TODO("not implemented")
//        val imports = listOf<String>()
        return ""
    }

}