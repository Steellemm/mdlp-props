package org.steellemm.mdlpprops.ui

import javax.swing.tree.DefaultMutableTreeNode

class PropsNode(val nodeName: String, var path: String = "/$nodeName", var alias: String? = null): DefaultMutableTreeNode(nodeName) {

    var badLeaf = true

}