package org.steellemm.mdlpprops.ui

import javax.swing.table.DefaultTableModel

class EnvInfoTableModel(var envSet: List<String> = emptyList()): DefaultTableModel(arrayOf("Environment", "Value"), 0) {

    init {
        setValues(emptyMap())
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return column == 1
    }

    fun setValues(values: Map<String, String>) {
        for (i: Int in rowCount - 1 downTo 0) {
            removeRow(i)
        }
        envSet.forEach {env ->
            addRow(arrayOf(env, values[env]))
        }
    }

}