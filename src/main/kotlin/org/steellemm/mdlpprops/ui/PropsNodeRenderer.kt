package org.steellemm.mdlpprops.ui

import java.awt.Component
import javax.swing.JTree
import javax.swing.tree.DefaultTreeCellRenderer

class PropsNodeRenderer: DefaultTreeCellRenderer() {

    override fun getTreeCellRendererComponent(
        tree: JTree?,
        value: Any?,
        sel: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
    }

}