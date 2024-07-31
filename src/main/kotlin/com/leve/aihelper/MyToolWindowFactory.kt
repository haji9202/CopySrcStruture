package com.leve.aihelper

import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.treeStructure.Tree
import java.awt.datatransfer.StringSelection
import javax.swing.*

class MyToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val projectView = ProjectView.getInstance(project)

        // Get the tree from the project view
        val projectTree = projectView.currentProjectViewPane.tree

        // Create a new tree with the project tree model
        val tree = Tree(projectTree.model)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content: Content = contentFactory.createContent(JScrollPane(tree), "", false)
        toolWindow.contentManager.addContent(content)

    }

    override fun init(toolWindow: ToolWindow) {
        // Initialization code, if needed.
    }

    override fun isApplicable(project: Project): Boolean {
        return true
    }
}
