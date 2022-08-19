package org.steellemm.mdlpprops.tool

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import org.steellemm.mdlpprops.isAvailable

class PropsInitializer : StartupActivity.Background {

    override fun runActivity(project: Project) {
        if (project.isAvailable()) {
            val service = project.getService(PropsService::class.java)
            try {
                service.initialize()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

}