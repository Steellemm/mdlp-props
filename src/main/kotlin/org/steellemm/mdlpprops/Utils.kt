package org.steellemm.mdlpprops

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScopeBuilderImpl
import org.steellemm.mdlpprops.settings.AppSettingsState
import org.steellemm.mdlpprops.ui.PropsNode
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Stream
import kotlin.streams.asSequence

fun getStateInstance(): AppSettingsState = ApplicationManager.getApplication().getService(AppSettingsState::class.java)

fun getValueMap(name: String, project: Project): Map<String, String> {
    var valuesFile: VirtualFile? = null
    ApplicationManager.getApplication().runReadAction {
        valuesFile =
            FilenameIndex.getVirtualFilesByName(name, GlobalSearchScope.projectScope(project))
                .firstOrNull() ?: throw IllegalArgumentException("File has not been found")
    }
    if (valuesFile == null) {
        throw IllegalStateException("File $name can't be read")
    }
    return getValueMap(valuesFile!!.inputStream)
}

fun getValueMap(inputStream: InputStream): Map<String, String> {
    readYamlFile(inputStream).use { stream ->
        return stream.asSequence().associate { it.first to it.second }
    }
}


fun getTreeFromTemplate(project: Project): Pair<PropsNode, MutableMap<String, PropsNode>> {
    val buildProjectScope = ProjectScopeBuilderImpl(project).buildProjectScope()
    val template =
        FilenameIndex.getVirtualFilesByName("template.yml", buildProjectScope)
            .firstOrNull() ?: return Pair(PropsNode(), mutableMapOf())
    return getTreeFromTemplate(template.inputStream)
}

fun getTreeFromTemplate(stream: InputStream): Pair<PropsNode, MutableMap<String, PropsNode>> {
    val nodesMap: MutableMap<String, PropsNode> = mutableMapOf("" to PropsNode())
    val leavesMap: MutableMap<String, PropsNode> = mutableMapOf()
    readYamlFile(stream).use {
        it.forEach { pair ->
            val nodes = pair.first.split("/")
            var lastNode = ""
            var lastSavedNode = PropsNode()
            for (node: String in nodes) {
                if (node == "") {
                    continue
                }
                val newNode  = "$lastNode/$node"
                if (!nodesMap.containsKey(newNode)) {
                    val root = nodesMap[lastNode] ?: throw IllegalStateException("Illegal word: ${pair.first}")
                    lastSavedNode = PropsNode(null, node)
                    nodesMap[newNode] = lastSavedNode
                    root.add(lastSavedNode)
                }
                lastNode = newNode
            }
            val leafNode = PropsNode(pair.second)
            lastSavedNode.add(leafNode)
            leavesMap[pair.second] = leafNode
        }
    }
    return Pair(nodesMap["/mdlp"] ?: throw IllegalStateException(), leavesMap)
}

fun readYamlFile(inputStream: InputStream): Stream<Pair<String, String>> {
    return BufferedReader(InputStreamReader(inputStream)).lines()
        .map { splitLine(it) }
}

fun splitLine(line: String): Pair<String, String> {
    val stringBuilder = StringBuilder()
    var key: String? = null
    var quote = false
    for(ch: Char in line.toCharArray()) {
        if (ch == '"' || ch == '\'') {
            quote = !quote
            continue
        }
        if (ch == '{' || ch == '}') {
            continue
        }
        if (ch == ' ' && !quote) {
            continue
        }
        if (ch == ':' && !quote) {
            key = stringBuilder.toString()
            stringBuilder.clear()
            continue
        }
        stringBuilder.append(ch)
    }
    val value: String = stringBuilder.toString()
    if (key == null) {
        throw IllegalStateException("Something went wrong: $line")
    }
    return Pair(key, value.trim())
}