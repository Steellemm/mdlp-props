package org.steellemm.mdlpprops.tool

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.steellemm.mdlpprops.*
import org.steellemm.mdlpprops.ui.PropsNode
import javax.swing.JTree

class ValueMap(
    val project: Project,
    val files: MutableMap<String, VirtualFile>,
    val templateFile: VirtualFile,
    val zkTree: JTree
) {

    /**
     * alias to (env to value)
     */
    private val values: MutableMap<String, MutableMap<String, String>> = mutableMapOf()
    private val leavesMap: MutableMap<String, PropsNode>
    val root: PropsNode

    init {
        val ans = getTreeFromTemplate(templateFile)
        root = ans.first
        leavesMap = ans.second
        getStateInstance().envMap.forEach { envToFileName ->
            files[envToFileName.key]?.let {
                getValueMap(it).forEach { aliasToVal ->
                    values.getOrPut(aliasToVal.key) { mutableMapOf() }[envToFileName.key] = aliasToVal.value
                }
            }
        }
    }

    fun getValues(alias: String): Map<String, String> {
        return values[alias] ?: throw IllegalArgumentException("alias does not exist: $alias")
    }

    fun addNewValue(path: String, alias: String, values: Map<String, String>): String? {
        if (leavesMap.containsKey(path)) {
            return "This path already exist"
        }
        WriteCommandAction.runWriteCommandAction(project) {
            addLineToFile("\"$path\": \"{{ $alias }}\"", templateFile)
            templateFile.sortFile()
            values.entries.forEach {
                addLineToFile("$alias: '${it.value}'", getVFile(it.key))
                getVFile(it.key).sortFile()
            }
        }
        addNode(path, alias)
        this.values[alias] = values.toMutableMap()
        zkTree.updateUI()
        return null
    }

    private fun addNode(path: String, alias: String) {
        var currentNode = root
        for(p in path.split('/')) {
            if (p.isBlank() || p == root.nodeName) {
                continue
            }
            currentNode = currentNode.addPropNode(p)
        }
        currentNode.alias = alias
        leavesMap[path] = currentNode
    }

    fun editValue(alias: String, env: String, newValue: String) {

    }

    private fun getVFile(env: String): VirtualFile {
        return files[env] ?: throw IllegalArgumentException("Illegal enviroment name: $env")
    }



}