package ru.kest.plugin.orika.dialog

import com.intellij.ide.util.PackageUtil
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import ru.kest.plugin.orika.entity.TestFile

/**
 * Wrapper for SelectClassDialog
 *
 * Created by KKharitonov on 30.06.2017.
 */
class NewTestDestinationDialogWrapper(sourceClass: PsiClass, destClass: PsiClass, element: PsiElement, project: Project)
    : SelectClassDialog(
        "Unit test for classes ${sourceClass.name} and ${destClass.name}",
        Companion.getTestClassName(sourceClass, destClass),
        Companion.findDefaultDirectory(element),
        project
) {

    companion object {
        private fun getTestClassName(sourceClass: PsiClass, destClass: PsiClass) : String {
            return "${sourceClass.name}To${destClass.name}MapperTest"
        }

        private fun getMapperPackage(element: PsiElement) : String? {
            val file = element.containingFile
            if (file is PsiJavaFile) {
                val packageArray = file.packageName.split('.')
                if (packageArray.size >= 4) {
                    return packageArray.slice(0..3).joinToString(separator = ".") + ".mapperz"
                }
            }
            return ""
        }

        private fun findDefaultDirectory(element: PsiElement) : PsiDirectory? {
            return PackageUtil.findPossiblePackageDirectoryInModule(
                    ModuleUtil.findModuleForPsiElement(element),
                    Companion.getMapperPackage(element)
            )
        }
    }

    fun getTestFile() : TestFile? {
        val clazzName = this.clazzName
        val packageName = this.packageName
        if (clazzName != null && packageName != null) {
            return TestFile(clazzName, packageName)
        }
        return null
    }
}