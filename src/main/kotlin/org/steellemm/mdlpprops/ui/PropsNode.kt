package org.steellemm.mdlpprops.ui

import javax.swing.tree.DefaultMutableTreeNode

class PropsNode(var id: String? = null, value: String? = id): DefaultMutableTreeNode(value) {

    var badLeaf = true

}