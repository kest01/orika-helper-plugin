package ru.kest.plugin.orika

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.ui.Messages

/**
 * Test Action
 * Created by KKharitonov on 23.06.2017.
 */
class SearchMappingAction : AnAction("Search orika mappings without tests") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(PlatformDataKeys.PROJECT)
//        val txt = Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon())
        Messages.showMessageDialog(project, "This feature not implemented yet", "Information", Messages.getInformationIcon())
    }
}