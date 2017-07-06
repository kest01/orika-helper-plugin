package ru.kest.plugin.orika.dialog

import com.intellij.ide.util.PackageUtil
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import ru.kest.plugin.orika.entity.MappingClasses

/**
 * Wrapper for SelectClassDialog
 *
 * Created by KKharitonov on 30.06.2017.
 */
class NewTestDestinationDialogWrapper(classes: MappingClasses, element: PsiElement, project: Project)
    : SelectClassDialog(
        "Unit test for classes ${classes.sourceClass.name} and ${classes.destClass.name}",
        Companion.getTestClassName(classes),
        Companion.findDefaultDirectory(element),
        project
) {

    companion object {
        private fun getTestClassName(classes: MappingClasses) : String {
            return "${classes.sourceClass.name}To${classes.destClass.name}MapperTest"
        }

        private fun getMapperPackage(element: PsiElement) : String? {
            val file = element.containingFile
            if (file is PsiJavaFile) {
                val packageArray = file.packageName.split('.')
                if (packageArray.size >= 4) {
                    return packageArray.slice(0..3).joinToString(separator = ".") + ".mapper"
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

}