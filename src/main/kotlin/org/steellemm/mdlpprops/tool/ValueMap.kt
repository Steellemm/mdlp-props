package org.steellemm.mdlpprops.tool

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.steellemm.mdlpprops.*
import org.steellemm.mdlpprops.ui.PropsNode
import javax.swing.JTree

class ValueMap(
    private val project: Project,
    private val files: MutableMap<String, VirtualFile>,
    private val templateFile: VirtualFile,
    private val zkTree: JTree
) {

    /**
     * alias to (env to value)
     */
    private val values: MutableMap<String, MutableMap<String, String>> = mutableMapOf()
    private val leavesMap: MutableMap<String, PropsNode> = mutableMapOf()
    val root: PropsNode = PropsNode("mdlp")

    private val pathReg = Regex("[a-zA-Z/\\-_]*")
    private val aliasReg = Regex("[a-zA-Z_]*")

    init {
        reload()
    }

    fun reload() {
        leavesMap.clear()
        values.clear()
        root.removeAllChildren()
        leavesMap.putAll(getTreeFromTemplate(templateFile, root))
        getStateInstance().envMap.forEach { envToFileName ->
            files[envToFileName.key]?.let {
                getValueMap(it).forEach { aliasToVal ->
                    values.getOrPut(aliasToVal.key) { mutableMapOf() }[envToFileName.key] = aliasToVal.value
                }
            }
        }
    }

    fun getValues(alias: String): Map<String, String>? {
        return values[alias]
    }

    fun addNewValue(path: String, alias: String, newValues: Map<String, String>): String? {
        if (leavesMap.containsKey(path)) {
            return "This path already exist"
        }
        if (!pathReg.matches(path)) {
            return "Path contains illegal symbols"
        }
        if (!aliasReg.matches(alias)) {
            return "Alias contains illegal symbols"
        }
        WriteCommandAction.runWriteCommandAction(project) {
            addLineToFile("\"$path\": \"{{ $alias }}\"", templateFile)
            templateFile.sortFile()
            newValues.entries.filter {
                !values.contains(alias) || !values[alias]!!.contains(it.key)
            }.associate { it.key to it.value }
                .toMap().forEach {
                    addLineToFile(valueLine(alias, it.value), getVFile(it.key))
                    getVFile(it.key).sortFile()
                    this.values.putIfAbsent(alias, mutableMapOf())
                    this.values[alias]?.put(it.key, it.value)
                }
        }
        addNode(path, alias)
        zkTree.updateUI()
        return null
    }

    private fun addNode(path: String, alias: String) {
        var currentNode = root
        for (p in path.split('/')) {
            if (p.isBlank() || p == root.nodeName) {
                continue
            }
            currentNode = currentNode.addPropNode(p)
        }
        currentNode.alias = alias
        leavesMap[path] = currentNode
    }

    fun editValue(alias: String, env: String, newValue: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            getVFile(env).changeValue(alias, newValue)
        }
        values[alias]?.put(env, newValue)
    }

    private fun getVFile(env: String): VirtualFile {
        return files[env] ?: throw IllegalArgumentException("Illegal environment name: $env")
    }

}