package org.steellemm.mdlpprops.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import org.steellemm.mdlpprops.getStateInstance
import org.steellemm.mdlpprops.tool.PropsRefreshAction
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel


class MDLPPropsWindow {
    lateinit var actionBar: JPanel
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
        actionBar.add(getToolbar())
    }

    fun reload(node: DefaultMutableTreeNode) {
        zkTree.model = DefaultTreeModel(node)
    }

    private fun getToolbar(): JComponent {
        val group = DefaultActionGroup("ACTION_GROUP", false)
        val refreshButton = PropsRefreshAction() {
            zkTree.updateUI()
        }
        group.add(refreshButton)
        val actionToolBar = ActionManager.getInstance().createActionToolbar("PropsAction", group, true)
        actionToolBar.setOrientation(SwingConstants.HORIZONTAL);
        actionToolBar.setTargetComponent(content)
        return actionToolBar.component
    }

}