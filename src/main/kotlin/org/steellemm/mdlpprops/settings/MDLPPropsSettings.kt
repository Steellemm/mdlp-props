package org.steellemm.mdlpprops.settings

import com.intellij.openapi.options.Configurable
import org.steellemm.mdlpprops.getStateInstance
import javax.swing.JComponent

class MDLPPropsSettings: Configurable {

    private var component: SettingsComponent = SettingsComponent()

    override fun createComponent(): JComponent {
        component = SettingsComponent()
        return component.mainJPanel
    }

    override fun isModified(): Boolean {
        val state: AppSettingsState = getStateInstance()
        return state.envMap == component.getEnvMap()
    }

    override fun apply() {
        val state: AppSettingsState = getStateInstance()
        val envMap = component.getEnvMap()
        state.envMap.clear()
        envMap.forEach { (t, u) -> state.envMap[t] = u }
    }

    override fun reset() {
        component.setEnvMap(getStateInstance().envMap)
    }

    override fun getDisplayName() = "MDLP Properties"
}