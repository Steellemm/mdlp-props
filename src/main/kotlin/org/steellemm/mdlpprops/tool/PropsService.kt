package org.steellemm.mdlpprops.tool

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.steellemm.mdlpprops.getStateInstance
import org.steellemm.mdlpprops.ui.MDLPPropsWindow
import org.steellemm.mdlpprops.ui.NewLeafDialog
import org.steellemm.mdlpprops.ui.PropsNode
import javax.swing.event.TableModelEvent


class PropsService(private val project: Project) {

    val toolWindow: MDLPPropsWindow = MDLPPropsWindow()

    /**
     * alias to (env to value)
     */
    private lateinit var valueMap: ValueMap
    private lateinit var leafDialog: NewLeafDialog
    private val filesMap: MutableMap<String, VirtualFile> = mutableMapOf()
    private lateinit var templateFile: VirtualFile


    fun initialize() {
        ApplicationManager.getApplication().runReadAction {
            templateFile =
                FilenameIndex.getVirtualFilesByName("template.yml", GlobalSearchScope.projectScope(project))
                    .firstOrNull() ?: throw IllegalArgumentException("File has not been found")
        }

        getStateInstance().envMap.forEach { envToFileName ->
            addFileByNameToMap(envToFileName.value, envToFileName.key)
        }

        valueMap = ValueMap(project, filesMap, templateFile, toolWindow.zkTree)
        leafDialog = NewLeafDialog(valueMap, getStateInstance().getEnvs())

        toolWindow.zkTree.addTreeSelectionListener {
            val leaf = it?.newLeadSelectionPath?.lastPathComponent
            if (leaf != null && leaf is PropsNode && leaf.alias != null) {
                setInfo(leaf.alias!!)
            }
        }

        toolWindow.envTableModel.addTableModelListener {
            if (it.type == TableModelEvent.UPDATE) {
                val newValue = toolWindow.envTableModel.getValueAt(it.firstRow, it.column).toString()
                val env = toolWindow.envTableModel.getValueAt(it.firstRow, 0).toString()
                val alias = toolWindow.alias.text
                valueMap.editValue(alias, env, newValue)
            }
        }

        toolWindow.zkTree.addMouseListener(TreeRightClickEvent(toolWindow.zkTree, leafDialog))
        toolWindow.reload(valueMap.root)
    }

    fun reload() {
        valueMap.reload()
    }

    private fun addFileByNameToMap(fileName: String, env: String) {
        ApplicationManager.getApplication().runReadAction {
            val virtualFile = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.projectScope(project))
                .firstOrNull() ?: throw IllegalArgumentException("File has not been found")
            filesMap[env] = virtualFile
        }
    }

    private fun setInfo(alias: String) {
        valueMap.getValues(alias)?.let {
            toolWindow.envTableModel.setValues(it)
            toolWindow.alias.text = alias
        }
    }

}