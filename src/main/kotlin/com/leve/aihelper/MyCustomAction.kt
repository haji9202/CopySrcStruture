package com.leve.aihelper

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class MyCustomAction : AnAction("Copy Project Structure") {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        project?.let {
            // 프로젝트의 src 폴더 가져오기
            val srcFolder: VirtualFile? = findSrcFolder(project.baseDir)

            // src 폴더가 존재하면 하위 파일 및 폴더 이름 가져오기
            srcFolder?.let { folder ->
                // 재귀적으로 폴더를 탐색하고 이름들을 가져오는 함수 호출
                val filesAndFolders: List<String> = getFilesAndFolders(folder, "")

                // 가져온 이름들을 하나의 문자열로 합치기
                val namesAsString: String = filesAndFolders.joinToString("\n")

                // 팝업으로 표시
                val dialogTitle = "Files and folders in src"
                val copyButtonText = "Copy to Clipboard"
                val dialogMessage = "$namesAsString\n\n$copyButtonText"
                val messageType = Messages.getInformationIcon()

                val answer = Messages.showYesNoDialog(dialogMessage, dialogTitle, copyButtonText, "Close", messageType)
                if (answer == Messages.YES) {
                    // 클립보드에 내용 복사
                    copyToClipboard(namesAsString)
                    Messages.showInfoMessage("Contents copied to clipboard", "Info")
                }
            }
        }
    }

    // 재귀적으로 폴더를 탐색하고 이름들을 가져오는 함수
    private fun getFilesAndFolders(folder: VirtualFile, prefix: String): List<String> {
        val result = mutableListOf<String>()

        for (i in folder.children.indices) {
            val child = folder.children[i]
            val isLast = i == folder.children.size - 1

            val displayName = if (child.isDirectory) {
                val folderSymbol = if (isLast) "└─ " else "├─ "
                "$prefix$folderSymbol${child.name} (folder)"
            } else {
                val fileSymbol = if (isLast) "└─ " else "├─ "
                "$prefix$fileSymbol${child.name}"
            }

            result.add(displayName)

            if (child.isDirectory) {
                // 하위 폴더가 있다면 재귀적으로 호출하고 prefix에 추가
                val newPrefix = if (isLast) "$prefix    " else "$prefix│   "
                result.addAll(getFilesAndFolders(child, newPrefix))
            }
        }

        return result
    }


    // 클립보드에 내용 복사
    private fun copyToClipboard(content: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(content)
        clipboard.setContents(selection, null)
    }

    // 프로젝트에서 "src" 폴더 찾기 (재귀적으로 모든 하위 폴더 확인)
    private fun findSrcFolder(baseDir: VirtualFile): VirtualFile? {
        val srcFolder: VirtualFile? = VfsUtil.findRelativeFile("src", baseDir)
        if (srcFolder != null && srcFolder.isDirectory) {
            return srcFolder
        }

        // "src" 폴더가 없으면 모든 하위 폴더에서 찾기
        for (child in baseDir.children) {
            if (child.isDirectory) {
                val srcInChild = findSrcFolder(child)
                if (srcInChild != null) {
                    return srcInChild
                }
            }
        }

        return null
    }
}
