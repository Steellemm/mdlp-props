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

        toolWindow.zkTree.addMouseListener(TreeRightClickEvent(toolWindow.zkTree, leafDialog))
        toolWindow.reload(valueMap.root)
    }

    private fun addFileByNameToMap(fileName: String, env: String) {
        ApplicationManager.getApplication().runReadAction {
                val virtualFile = FilenameIndex.getVirtualFilesByName(fileName, GlobalSearchScope.projectScope(project))
                    .firstOrNull() ?: throw IllegalArgumentException("File has not been found")
                filesMap[env] = virtualFile
        }
    }

    private fun setInfo(alias: String) {
        toolWindow.envTableModel.setValues(valueMap.getValues(alias))
        toolWindow.alias.text = alias
    }

}