package ru.kest.plugin.orika

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import ru.kest.plugin.orika.dialog.TestDestinationDialog
import ru.kest.plugin.orika.psi.OrikaElementFinder
import ru.kest.plugin.orika.psi.OrikaElementParametersFinder

/**
 * IntentionAction to propose creating unit-test for Orika mapping
 *
 * Created by KKharitonov on 24.06.2017.
 */
class OrikaMapperIntentionAction : PsiElementBaseIntentionAction() {

    private val LOG = Logger.getInstance(OrikaMapperIntentionAction::class.java)


    override fun getFamilyName(): String {
        return text
    }

    override fun getText(): String {
        return "Create unit-test for Orika mapping"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {

        return OrikaElementFinder.isOrikaElement(element)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        if (!OrikaElementFinder.isOrikaElement(element)) {
            LOG.info("Orika helper invoked on incorrect element")
            Messages.showMessageDialog(project, "Orika helper invoked on incorrect element", "Information", Messages.getInformationIcon())
            return
        }
        val classes = OrikaElementParametersFinder.getParamClasses(element)
        if (classes == null) {
            LOG.info("Orika classes not found")
            return
        }
        val (sourceClass, destClass) = classes

        LOG.info("Orika: source class: $sourceClass  - destination class: $destClass")
/*
        ApplicationManager.getApplication().invokeLater {
            Messages.showMessageDialog(project, "Stab: Create test for Orika mapping", "Information", Messages.getInformationIcon())
        }
*/
        ApplicationManager.getApplication().invokeLater {
            val dialog = TestDestinationDialog(sourceClass, null, project)
            dialog.showAndGet()
        }

    }

    override fun startInWriteAction(): Boolean {
        return true
    }


}