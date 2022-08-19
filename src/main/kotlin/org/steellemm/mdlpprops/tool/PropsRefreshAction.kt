package org.steellemm.mdlpprops.tool

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.IconLoader

class PropsRefreshAction(private val update: () -> Unit ): AnAction("Refresh", "Refresh", IconLoader.getIcon("/actions/refresh.png")) {
    override fun actionPerformed(e: AnActionEvent) {
        val service = e.project?.getService(PropsService::class.java)
        service?.reload()
        update.invoke()
    }
}