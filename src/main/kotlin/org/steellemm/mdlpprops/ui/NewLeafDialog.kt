package org.steellemm.mdlpprops.ui

import java.awt.Dimension
import javax.swing.*

class NewLeafDialog : JDialog() {
    lateinit var content: JPanel
    lateinit var buttonOK: JButton
    lateinit var buttonCancel: JButton
    lateinit var alias: JTextField
    lateinit var path: JTextField
    lateinit var table1: JTable

    init {
        contentPane = content
        isModal = true
        getRootPane().defaultButton = buttonOK
        buttonOK.addActionListener { onOK() }
        minimumSize = Dimension(300, 250)
        setLocationRelativeTo(null)
        pack()
    }

    private fun onOK() {
        // add your code here
        dispose()
    }
}