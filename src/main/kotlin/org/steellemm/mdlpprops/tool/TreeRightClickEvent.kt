package org.steellemm.mdlpprops.tool

import org.steellemm.mdlpprops.ui.NewLeafDialog
import org.steellemm.mdlpprops.ui.PropsNode
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class TreeRightClickEvent(private val tree: JTree, private val dialog: NewLeafDialog) : MouseAdapter() {

    private val menu: JPopupMenu

    init {
        menu = object : JPopupMenu() {
            fun add(name: String, task: (title: String) -> Unit) {
                add(JMenuItem(object : AbstractAction(name) {
                    override fun actionPerformed(e: ActionEvent) {
                        task(name)
                    }
                }))
            }
        }
        menu.add("Add node") {
            dialog.isVisible = true
        }
    }

    override fun mousePressed(e: MouseEvent) {
        if (SwingUtilities.isRightMouseButton(e)) {
            val paths = tree.selectionPaths
            if (paths.isNullOrEmpty()) {
                return
            }
            val node = paths.last().lastPathComponent as PropsNode
            dialog.loadNode(node)
            menu.show(tree, e.x, e.y)
        }
    }

}