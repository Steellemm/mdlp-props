package org.steellemm.mdlpprops.tool

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.yaml.psi.YAMLFile
import org.steellemm.mdlpprops.getStateInstance
import org.steellemm.mdlpprops.getTreeFromTemplate
import org.steellemm.mdlpprops.getValueMap
import org.steellemm.mdlpprops.ui.MDLPPropsWindow
import org.steellemm.mdlpprops.ui.NewLeafDialog
import org.steellemm.mdlpprops.ui.PropsNode
import java.awt.event.ItemEvent
import javax.swing.tree.DefaultMutableTreeNode


class PropsService(private val project: Project) {

    val toolWindow: MDLPPropsWindow = MDLPPropsWindow()
    val leafDialog: NewLeafDialog = NewLeafDialog()
    private lateinit var leavesMap: MutableMap<String, PropsNode>

    /**
     * alias to (env to value)
     */
    private val values: MutableMap<String, MutableMap<String, String>> = mutableMapOf()


    fun initialize() {
        var treeFromTemplate: DefaultMutableTreeNode? = null

        ApplicationManager.getApplication().runReadAction {
            val ans = getTreeFromTemplate(project)
            treeFromTemplate = ans.first
            leavesMap = ans.second
        }

        getStateInstance().envMap.forEach { envToFileName ->
            getValueMap(envToFileName.value, project).forEach { aliasToVal ->
                values.getOrPut(aliasToVal.key) { mutableMapOf() }[envToFileName.key] = aliasToVal.value
            }
        }

        toolWindow.envComboBox.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                setLeaves(e.item as String)
            }
        }

        toolWindow.zkTree.addTreeSelectionListener {
            val leaf = it?.newLeadSelectionPath?.lastPathComponent
            if (leaf != null && leaf is PropsNode && leaf.id != null) {
                setInfo(leaf.id!!)
            }
        }

        toolWindow.zkTree.addMouseListener(TreeRightClickEvent(toolWindow.zkTree, leafDialog))

        val selectedEnv = toolWindow.envComboBox.selectedItem as String
        setLeaves(selectedEnv)
        toolWindow.reload(treeFromTemplate ?: throw IllegalStateException())
    }

    private fun setInfo(alias: String) {
        toolWindow.alias.text = alias
        toolWindow.envTableModel.setValues(values[alias] ?: emptyMap())
    }

    private fun setLeaves(selectedEnv: String) {
        leavesMap.forEach {
            if (values.containsKey(it.key)) {
                it.value.userObject = values[it.key]!![selectedEnv]
                it.value.badLeaf = false
            }
        }
        toolWindow.zkTree.updateUI()
    }

    private fun getYamlFile(name: String): YAMLFile {
        var files: Array<PsiFile>? = null
        ApplicationManager.getApplication().runReadAction {
            files = FilenameIndex.getFilesByName(project, name, GlobalSearchScope.projectScope(project))
        }
        if (files == null || files!!.isEmpty()) {
            throw IllegalArgumentException("File has not been found: $name")
        }
        val file = files!![0]
        if (file !is YAMLFile) {
            throw IllegalArgumentException("File is not yaml: $name")
        }
        return file
    }

}