package org.steellemm.mdlpprops.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "org.steellemm.mdlpprops.settings.AppSettingsState",
    storages = [Storage("MDLPPropsPlugin.xml")]
)
class AppSettingsState: PersistentStateComponent<AppSettingsState> {

    var envMap: MutableMap<String, String> = mutableMapOf(
        "INT" to "values_int.yml",
        "QA01" to "values_qa01.yml",
        "LT02" to "values_lt02.yml",
        "RC" to "values_rc.yml",
        "SANDBOX01" to "values_sandbox01.yml",
        "PROD01" to "values_prod01.yml",
        "PROD02" to "values_prod02.yml"
    )

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this);
    }
}