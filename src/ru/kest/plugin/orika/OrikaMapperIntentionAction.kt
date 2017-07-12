package ru.kest.plugin.orika

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import ru.kest.plugin.orika.dialog.NewTestDestinationDialogWrapper
import ru.kest.plugin.orika.psi.OrikaElementFinder
import ru.kest.plugin.orika.psi.OrikaElementParametersFinder
import ru.kest.plugin.orika.test.TestClassCreator

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
            ApplicationManager.getApplication().invokeLater {
                Messages.showMessageDialog(project, "Orika helper invoked on incorrect element", "Information", Messages.getErrorIcon())
            }
            return
        }
        val classes = OrikaElementParametersFinder.getParamClasses(element)
        if (classes == null) {
            LOG.info("Orika classes not found")
            ApplicationManager.getApplication().invokeLater {
                Messages.showMessageDialog(project, "Orika classes not found", "Information", Messages.getErrorIcon())
            }
            return
        }

        LOG.info("Orika: source class: ${classes.sourceClass} - destination class: ${classes.destClass}")
        ApplicationManager.getApplication().invokeLater {
            val dialog = NewTestDestinationDialogWrapper(classes, element, project)
            if (dialog.showAndGet()) {
                val testFile = dialog.testFile
                LOG.info("Orika: selected destinations $testFile")
                val testCreator = TestClassCreator(classes, testFile!!, project)
                val file = testCreator.create()
                WriteCommandAction.runWriteCommandAction(project) {
                    testFile.directory.add(file)
                    FileEditorManager.getInstance(project).openFile(
                            testFile.directory.findFile(file.name)!!.virtualFile, true)
                }
            }
        }

    }

    override fun startInWriteAction(): Boolean {
        return true
    }

}