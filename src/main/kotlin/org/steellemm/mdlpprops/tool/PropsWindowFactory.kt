package org.steellemm.mdlpprops.tool

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import org.steellemm.mdlpprops.isAvailable


class PropsWindowFactory: ToolWindowFactory {

    override fun isApplicable(project: Project): Boolean {
        return project.isAvailable()
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val service = project.getService(PropsService::class.java)
        val contentManager = toolWindow.contentManager
        val content: Content = contentManager.factory
            .createContent(service.toolWindow.content, "", false)
        contentManager.addContent(content)
    }
}