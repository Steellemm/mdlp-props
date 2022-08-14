package org.steellemm.mdlpprops.ui

import org.steellemm.mdlpprops.getStateInstance
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel


class MDLPPropsWindow {
    lateinit var content: JPanel
    lateinit var zkTree: JTree
    lateinit var alias: JTextField
    lateinit var envTable: JTable
    val envTableModel = EnvInfoTableModel()

    init {
        val envs = getStateInstance().envMap.keys
        zkTree.model = DefaultTreeModel(DefaultMutableTreeNode())
        envTableModel.envSet = envs.toList()
        envTable.model = envTableModel
        envTable.columnModel.getColumn(0).maxWidth = 240
        alias.isEditable = false
    }

    fun reload(node: DefaultMutableTreeNode) {
        zkTree.model = DefaultTreeModel(node)
    }

}