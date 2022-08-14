package org.steellemm.mdlpprops.ui

import javax.swing.tree.DefaultMutableTreeNode

class PropsNode(var path: String? = null, val nodeName: String, var alias: String? = null): DefaultMutableTreeNode(nodeName) {

    var badLeaf = true

}