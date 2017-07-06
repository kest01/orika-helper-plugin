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
 * Creates groovy unit test
 *
 * Created by KKharitonov on 01.07.2017.
 */
class TestCreator(val classes: MappingClasses, val testFile: TestFile, val project: Project) {

//    private val LOG = Logger.getInstance(TestCreator::class.java)

    private val TEST_TEMPLATE = "NewTest.groovy"

    fun create() : PsiFile {
        val content = getTemplate().getText(generateTemplateParams())
        val file = createNewFile(content)
        return file
    }

    private fun createNewFile(content: String) : PsiFile {
        return PsiFileFactory.getInstance(project)
                .createFileFromText(testFile.className + ".groovy", GroovyFileType.GROOVY_FILE_TYPE, content)
    }

    private fun getTemplate() : FileTemplate {
        return FileTemplateManager.getInstance(project).getCodeTemplate(TEST_TEMPLATE)
    }

    private fun generateTemplateParams() : Map<String, String> {
        val params = HashMap<String, String>()
        params.put("PACKAGE_NAME", testFile.packageName)
        params.put("TEST_CLASSNAME", testFile.className)
        params.put("SOURCE_CLASS", classes.sourceClass.name!!)
        params.put("DEST_CLASS", classes.destClass.name!!)
        params.put("IMPORTS", getImports())
        params.put("METHODS", getTestContent())

        return params
    }

    private fun getTestContent(): String {
//        TODO("not implemented")
        return ""
    }

    private fun  getImports(): String {
//        TODO("not implemented")
        return ""
    }

}