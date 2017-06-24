package ru.kest.plugin.orika

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*

/**
 * IntentionAction to propose creating unit-test for Orika mapping
 *
 * Created by KKharitonov on 24.06.2017.
 */
class OrikaMapperIntentionAction : PsiElementBaseIntentionAction() {

//    private val LOG = Logger.getInstance(OrikaMapperIntentionAction::class.java)

    val ORIKA_MAPPER_INTERFACE = "ma.glasnost.orika.MapperFacade"
    val OBJECT = "java.lang.Object"


    override fun getFamilyName(): String {
        return text
    }

    override fun getText(): String {
        return "Create unit-test for Orika mapping"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (!element.isWritable()) return false

        if (element is PsiIdentifier) {
            val parent = element.parent
            if (parent is PsiReferenceExpression) {
                val grandpa = parent.parent
                if (grandpa is PsiMethodCallExpression) {
                    val psiType = grandpa.methodExpression.qualifierExpression?.type
                    if (psiType != null) {
                        return isImplements(psiType, ORIKA_MAPPER_INTERFACE)
                    }
                }
            }
        }
        return false
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        Messages.showMessageDialog(project, "Stab: Create test for Orika mapping", "Information", Messages.getInformationIcon())
    }

    override fun startInWriteAction(): Boolean {
        return true
    }

    private fun isImplements(type: PsiType, className: String) : Boolean {
        for (superType in type.superTypes) {
            if (superType.canonicalText == className) {
                return true
            } else if (superType.canonicalText == OBJECT) {
                continue
            } else {
                if (isImplements(superType, className)) {
                    return true
                }
            }
        }
        return false
    }

}