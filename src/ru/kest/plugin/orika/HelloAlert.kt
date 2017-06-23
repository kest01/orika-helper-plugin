package ru.kest.plugin.orika

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.Messages

/**
 * Test Action
 * Created by KKharitonov on 23.06.2017.
 */
class HelloAlert : AnAction("Hello _Alert") {

    override fun actionPerformed(e: AnActionEvent?) {
        val project = e!!.getData(PlatformDataKeys.PROJECT)
        val txt = Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon())
        Messages.showMessageDialog(project, "Hello, $txt!\n I am glad to see you.", "Information", Messages.getInformationIcon())
    }
}