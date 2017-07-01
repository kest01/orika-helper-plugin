package ru.kest.plugin.orika.dialog

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.JavaProjectRootsUtil
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Pass
import com.intellij.psi.*
import com.intellij.refactoring.PackageWrapper
import com.intellij.refactoring.RefactoringBundle
import com.intellij.refactoring.move.moveClassesOrPackages.DestinationFolderComboBox
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo
import com.intellij.refactoring.util.RefactoringMessageUtil
import com.intellij.ui.*
import com.intellij.util.IncorrectOperationException
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UIUtil
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.*

/**
 * Dialog for choose destionation of new test. Copied from CopyClassDialog
 *
 * Created by KKharitonov on 29.06.2017.
 */
open class SelectClassDialog : DialogWrapper {

    private val LOG = Logger.getInstance(SelectClassDialog::class.java)

    @NonNls private val RECENTS_KEY = "TestDestinationDialog.RECENTS_KEY"

    private val informationLabel = JLabel()
    private val testClassNameField = EditorTextField("")
    private val project: Project
    private val defaultTargetDirectory: PsiDirectory?

    private val destinationRootComboBox = object : DestinationFolderComboBox() {
        override fun getTargetPackage(): String {
            return packageComboField!!.text.trim { it <= ' ' }
        }

        override fun reportBaseInTestSelectionInSource(): Boolean {
            return true
        }
    }

    private var packageComboField: ReferenceEditorComboWithBrowseButton? = null
    var clazzName: String? = null
    var packageName: String? = null

    constructor(description: String, className: String, defaultTargetDirectory: PsiDirectory?, project: Project) : super(project, true) {
        this.project = project
        this.defaultTargetDirectory = defaultTargetDirectory
        title = "Create new unit test for Orika mapper"
        informationLabel.text = description
        informationLabel.font = informationLabel.font.deriveFont(Font.BOLD)

        init()
        destinationRootComboBox.setData(this.project, defaultTargetDirectory,
                object : Pass<String>() {
                    override fun pass(s: String?) {
                        setErrorText(s, destinationRootComboBox)
                    }
                }, packageComboField!!.childComponent)
        testClassNameField.text = className
        testClassNameField.selectAll()
    }

    override fun createActions(): Array<Action> {
        return arrayOf(okAction, cancelAction)
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return testClassNameField
    }

    override fun createCenterPanel(): JComponent? {
        return JPanel(BorderLayout())
    }

    override fun createNorthPanel(): JComponent? {
        val qualifiedName = getQualifiedName()
        val localPackageComboField = PackageNameReferenceEditorCombo(qualifiedName, project, RECENTS_KEY, RefactoringBundle.message("choose.destination.package"))
        packageComboField = localPackageComboField
        localPackageComboField.setTextFieldPreferredWidth(Math.max(qualifiedName.length + 5, 40))
        val packageLabel = JLabel(RefactoringBundle.message("destination.package"))
        packageLabel.labelFor = localPackageComboField

        val label = JLabel(RefactoringBundle.message("target.destination.folder"))
        val isMultipleSourceRoots = JavaProjectRootsUtil.getSuitableDestinationSourceRoots(project).size > 1
        destinationRootComboBox.isVisible = isMultipleSourceRoots
        label.isVisible = isMultipleSourceRoots
        label.labelFor = destinationRootComboBox

        return FormBuilder.createFormBuilder()
                .addComponent(informationLabel)
                .addLabeledComponent("Test name", testClassNameField, UIUtil.LARGE_VGAP)
                .addLabeledComponent(packageLabel, localPackageComboField)
                .addLabeledComponent(label, destinationRootComboBox)
                .panel
    }

    private fun getQualifiedName(): String {
        var qualifiedName = ""
        if (defaultTargetDirectory != null) {
            val aPackage = JavaDirectoryService.getInstance().getPackage(defaultTargetDirectory)
            if (aPackage != null) {
                qualifiedName = aPackage.qualifiedName
            }
        }
        return qualifiedName
    }

    fun getClassName(): String? {
        return testClassNameField.text
    }

    override fun doOKAction() {
        val packageName = packageComboField!!.text
        val className = getClassName()

        val errorString = arrayOfNulls<String>(1)
        val manager = PsiManager.getInstance(project)
        val nameHelper = PsiNameHelper.getInstance(manager.project)
        if (packageName.isNotEmpty() && !nameHelper.isQualifiedName(packageName)) {
            errorString[0] = RefactoringBundle.message("invalid.target.package.name.specified")
        } else if (className != null && className.isEmpty()) {
            errorString[0] = RefactoringBundle.message("no.class.name.specified")
        } else {
            if (!nameHelper.isIdentifier(className)) {
                errorString[0] = RefactoringMessageUtil.getIncorrectIdentifierMessage(className)
            } else  {
                try {
                    val targetPackage = PackageWrapper(manager, packageName)
                    val destination = destinationRootComboBox.selectDirectory(targetPackage, false)
                    if (destination == null) return
//                    val sourceRoot = destination.getTargetDirectory(defaultTargetDirectory)
                    this.clazzName = className
                    this.packageName = packageName
                } catch (e: IncorrectOperationException) {
                    errorString[0] = e.message
                }

            }
            RecentsManager.getInstance(project).registerRecentEntry(RECENTS_KEY, packageName)
        }

        if (errorString[0] != null) {
            if (errorString[0]!!.isNotEmpty()) {
                Messages.showMessageDialog(project, errorString[0], RefactoringBundle.message("error.title"), Messages.getErrorIcon())
            }
            testClassNameField.requestFocusInWindow()
            return
        }
        super.doOKAction()
    }
}