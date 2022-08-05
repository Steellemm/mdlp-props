package org.steellemm.mdlpprops.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel


class SettingsComponent {

    val mainJPanel: JPanel

    private var columnNames = arrayOf(
        "Environment",
        "File Name"
    )

    private val table: JBTable

    private val tableModel: DefaultTableModel = DefaultTableModel(columnNames, 1)

    init {
        table = JBTable(tableModel)
        mainJPanel = FormBuilder.createFormBuilder()
            .addComponent(JBLabel("Environments"))
            .addComponent(table, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getEnvMap(): Map<String, String> {
        return IntRange(0, table.model.rowCount - 1).associate {
            table.model.getValueAt(it, 0).toString() to table.model.getValueAt(it, 1).toString()
        }
    }

    fun setEnvMap(map: Map<String, String>) {
        tableModel.rowCount = 0
        map.forEach { (env, file) -> tableModel.addRow(arrayOf(env, file)) }
    }

}