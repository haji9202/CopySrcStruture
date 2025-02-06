package com.leve.aihelper

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyAction : AnAction("Copy Project Structure") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val baseDir = project.baseDir
            if (baseDir != null) {
                val filesAndFolders: MutableList<String> = ArrayList()
                filesAndFolders.add(baseDir.name + " (Project)") // 프로젝트 이름 추가
                filesAndFolders.addAll(getFilesAndFolders(baseDir, ""))
                val namesAsString = java.lang.String.join("\n", filesAndFolders)

                ApplicationManager.getApplication().invokeLater {
                    val dialogTitle = "Files and folders in Project"
                    val copyButtonText = "Copy to Clipboard"
                    val dialogMessage = """
                        $namesAsString
                        
                        $copyButtonText
                        """.trimIndent()
                    val answer = Messages.showYesNoDialog(
                        dialogMessage,
                        dialogTitle,
                        copyButtonText,
                        "Close",
                        Messages.getInformationIcon()
                    )
                    if (answer == Messages.YES) {
                        copyToClipboard(namesAsString)
                        Messages.showInfoMessage("Contents copied to clipboard", "Info")
                    }
                }
            }
        }
    }

    // 폴더 및 파일을 재귀적으로 가져오는 함수
    private fun getFilesAndFolders(folder: VirtualFile, prefix: String): List<String> {
        val result: MutableList<String> = ArrayList()
        val children = folder.children

        for (i in children.indices) {
            val child = children[i]
            val isLast = (i == children.size - 1)

            val displayName = child.name + (if (child.isDirectory) " (folder)" else "")
            val symbol = if (isLast) "└─ " else "├─ "
            result.add(prefix + symbol + displayName)

            if (child.isDirectory) {
                val newPrefix = if (isLast) "$prefix    " else "$prefix│   "
                result.addAll(getFilesAndFolders(child, newPrefix))
            }
        }
        return result
    }

    // 클립보드에 문자열 복사
    private fun copyToClipboard(content: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(content), null)
    }
}