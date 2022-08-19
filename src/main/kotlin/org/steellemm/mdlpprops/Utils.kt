package org.steellemm.mdlpprops

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import org.steellemm.mdlpprops.settings.AppSettingsState
import org.steellemm.mdlpprops.ui.PropsNode
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Stream
import kotlin.streams.asSequence

fun getStateInstance(): AppSettingsState = ApplicationManager.getApplication().getService(AppSettingsState::class.java)

fun PropsNode.addPropNode(nodeName: String): PropsNode {
    val child = this.children().asSequence().map { it as PropsNode }.map { it.nodeName }.find { it == nodeName }
    if (child == null) {
        val propsNode = PropsNode(this.path + '/' + nodeName, nodeName)
        this.add(propsNode)
        return propsNode
    }
    return child as PropsNode
}

fun VirtualFile.document(): Document {
    return FileDocumentManager.getInstance().getDocument(this)
        ?: throw IllegalArgumentException("File has not been found")
}

fun VirtualFile.changeValue(alias: String, newValue: String) {
    val document = this.document()
    val searchLine = "$alias:"
    val offset = searchLine.length
    val foundLine = IntRange(0, document.lineCount).find {
        val lineStartOffset = document.getLineStartOffset(it)
        document.getText(TextRange(lineStartOffset, lineStartOffset + offset)) == searchLine
    } ?: throw IllegalStateException("Line has not been found: $searchLine")

    val newContent = valueLine(alias, newValue)
    val toReplaceStart = document.getLineStartOffset(foundLine)
    val toReplaceEnd = document.getLineEndOffset(foundLine)
    document.replaceString(toReplaceStart, toReplaceEnd, newContent)
}

fun valueLine(alias: String, value: String) = "$alias: '$value'"

fun VirtualFile.sortFile() {
    val document = this.document()
    val lastLine = document.lineCount - 1
    val lines = arrayOfNulls<String>(lastLine + 1)
    for (i in 0..lastLine) {
        lines[i] = document.getText(TextRange(document.getLineStartOffset(i), document.getLineEndOffset(i)))
    }
    Arrays.parallelSort<String>(lines)
    val newContent = java.lang.String.join("\n", *lines).trimIndent()
    val toReplaceStart = document.getLineStartOffset(0)
    val toReplaceEnd = document.getLineEndOffset(lastLine)
    document.replaceString(toReplaceStart, toReplaceEnd, newContent)
}

fun addLineToFile(line: String, file: VirtualFile) {
    val document = file.document()
    document.insertString(0, line + '\n')
}

fun getValueMap(valuesFile: VirtualFile): Map<String, String> {
    return getValueMap(valuesFile.inputStream)
}

fun getValueMap(inputStream: InputStream): Map<String, String> {
    readYamlFile(inputStream).use { stream ->
        return stream.asSequence().associate { it.first to it.second }
    }
}

fun getTreeFromTemplate(template: VirtualFile): Pair<PropsNode, MutableMap<String, PropsNode>> {
    return getTreeFromTemplate(template.inputStream)
}

fun getTreeFromTemplate(stream: InputStream): Pair<PropsNode, MutableMap<String, PropsNode>> {
    var lastSavedNode = PropsNode(nodeName = "")
    val nodesMap: MutableMap<String, PropsNode> = mutableMapOf("" to lastSavedNode)
    val leavesMap: MutableMap<String, PropsNode> = mutableMapOf()
    readYamlFile(stream).use {
        it.forEach { pair ->
            val nodeNames = pair.first.split("/")
            var lastNodePath = ""
            for (nodeName: String in nodeNames) {
                if (nodeName == "") {
                    continue
                }
                val newNodePath  = "$lastNodePath/$nodeName"
                if (!nodesMap.containsKey(newNodePath)) {
                    val root = nodesMap[lastNodePath] ?: throw IllegalStateException("Illegal key: ${pair.first}")
                    lastSavedNode = PropsNode(newNodePath, nodeName)
                    nodesMap[newNodePath] = lastSavedNode
                    root.add(lastSavedNode)
                }
                lastNodePath = newNodePath
            }
            lastSavedNode.alias = pair.second
            leavesMap[pair.second] = lastSavedNode
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

fun Project.isAvailable() = this.name == "mdlp-apps-config"