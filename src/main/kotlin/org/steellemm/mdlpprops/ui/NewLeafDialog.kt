package org.steellemm.mdlpprops.ui

import org.steellemm.mdlpprops.tool.ValueMap
import java.awt.Dimension
import javax.swing.*

class NewLeafDialog(
    private val valueMap: ValueMap,
    private val envList: List<String>
) : JDialog() {
    lateinit var content: JPanel
    lateinit var buttonOK: JButton
    lateinit var buttonCancel: JButton
    lateinit var aliasField: JTextField
    lateinit var path: JTextField
    lateinit var table1: JTable

    private val charactersForChange = Regex("[\\[\\]/-]")
    private val dash = Regex("_+")

    init {
        contentPane = content
        isModal = true
        getRootPane().defaultButton = buttonOK
        buttonOK.addActionListener { onOK() }
        buttonCancel.addActionListener { dispose() }
        minimumSize = Dimension(300, 300)
        size = Dimension(600, 400)
        aliasField.isEditable = false
        path.document.addDocumentListener(SimpleDocumentListener {
            aliasField.text = pathToAlias(path.text)
        })
        setLocationRelativeTo(null)
        pack()
    }

    fun loadNode(node: PropsNode) {
        path.text = node.path
        aliasField.text = pathToAlias(path.text)
        table1.model = EnvInfoTableModel(envList)
        table1.columnModel.getColumn(0).maxWidth = 240
    }

    private fun pathToAlias(path: String): String {
        if (path.isBlank()) {
            return ""
        }
        var alias = path.replace(charactersForChange, "_").replace(dash, "_")
        if (alias.first() == '_') {
            alias = alias.substring(1)
        }
        if (alias.last() == '_') {
            alias = alias.substring(0, alias.length - 1)
        }
        return alias
    }

    private fun onOK() {
        val values = IntRange(0, table1.model.rowCount - 1)
            .associate { envList[it] to
                    if (table1.model.getValueAt(it, 1) == null)
                        "NOnode"
                    else table1.model.getValueAt(it, 1).toString()
        }
        val warn = valueMap.addNewValue(path.text, aliasField.text, values)
        if (warn != null) {
            JOptionPane.showMessageDialog(content, warn,
                "MDLP properties", JOptionPane.WARNING_MESSAGE)
            return
        }
        dispose()
    }
}