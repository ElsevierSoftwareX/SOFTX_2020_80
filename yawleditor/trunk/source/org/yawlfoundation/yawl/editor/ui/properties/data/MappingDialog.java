/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.properties.data;

import org.yawlfoundation.yawl.editor.core.YConnector;
import org.yawlfoundation.yawl.editor.core.data.YDataHandler;
import org.yawlfoundation.yawl.editor.ui.data.editorpane.XQueryEditorPane;
import org.yawlfoundation.yawl.editor.ui.data.editorpane.XQueryValidatingEditorPane;
import org.yawlfoundation.yawl.editor.ui.properties.data.validation.MappingTypeValidator;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.util.IconList;
import org.yawlfoundation.yawl.editor.ui.util.ResourceLoader;
import org.yawlfoundation.yawl.editor.ui.util.XMLUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 14/08/12
 */
public class MappingDialog extends JDialog implements ActionListener {

    private VariableRow _taskRow;                    // the task input or output row
    private VariableTablePanel _netTablePanel;
    private XQueryValidatingEditorPane _xQueryEditor;
    private XQueryEditorPane _miQueryEditor;
    private MappingTypeValidator _typeValidator;

    private JRadioButton netVarsButton;
    private JRadioButton gatewayButton;
    private JRadioButton expressionButton;
    private JComboBox netVarsCombo;
    private JComboBox gatewayCombo;
    private JPanel queryPanel;
    private JButton okButton;


    public MappingDialog(VariableTablePanel netTablePanel, VariableRow row,
                         java.util.List<VariableRow> mappedFromVariableList) {
        super();
        _taskRow = row;
        _netTablePanel = netTablePanel;
        setTitle(makeTitle());
        add(getContent(initTypeValidator(row, mappedFromVariableList)));
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setPreferredSize(new Dimension(426, _taskRow.isMultiInstance() ? 520 : 400));
        pack();
    }


    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        if (action.equals("netVarRadio")) {
            enableCombos(true, false);
            handleNetVarComboSelection();
        }
        else if (action.equals("gatewayRadio")) {
            enableCombos(false, true);
            handleGatewayComboSelection();
        }
        else if (action.equals("expressionRadio")) {
            enableCombos(false, false);
            _xQueryEditor.setText("");
        }
        else if (action.equals("netVarComboSelection")) {
            handleNetVarComboSelection();
        }
        else if (action.equals("gatewayComboSelection")) {
            handleGatewayComboSelection();
        }
        else {
            if (action.equals("OK")) {
                _taskRow.setMapping(formatQuery(_xQueryEditor.getText(), false));
                if (_taskRow.isMultiInstance()) {
                    _taskRow.setMIQuery(formatQuery(_miQueryEditor.getText(), false));
                }
                if (_taskRow.isOutput() && netVarsButton.isSelected()) {
                    _taskRow.setNetVarForOutputMapping(getSelectedNetVar());
                }
                _netTablePanel.getVariableDialog().enableApplyButton();
            }
            setVisible(false);
        }
    }


    private MappingTypeValidator initTypeValidator(VariableRow row,
                             java.util.List<VariableRow> mappedFromVariableList) {
        if (row.isMultiInstance()) return null;         // no type checks for MI queries

        String dataType = row.isInput() ? row.getDataType() :
                getDataTypeForNetVar(row.getNetVarForOutputMapping());
        _typeValidator = new MappingTypeValidator(mappedFromVariableList, row, dataType);
        return _typeValidator;
    }


    private String makeTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append(YDataHandler.getScopeName(_taskRow.getUsage()))
          .append(" Data Mapping for Task: ")
          .append(_taskRow.getDecompositionID());
        return sb.toString();
    }


    private JPanel getContent(MappingTypeValidator typeChecker) {
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(7, 7, 7, 7));
        content.add(buildIOPanel());
        content.add(createQueryPanel(typeChecker));
        if (_taskRow.isMultiInstance()) content.add(createMiQueryPanel());
        content.add(createButtonBar());
        initContent();
        return content;
    }


    private void initContent() {
        if (netVarsCombo.getItemCount() == 0) {
            netVarsButton.setEnabled(false);
            netVarsCombo.setEnabled(false);
        }
        if (gatewayCombo.getItemCount() == 0) {
            gatewayButton.setEnabled(false);
            gatewayCombo.setEnabled(false);
        }

        String mapping = _taskRow.getMapping();
        if (mapping == null ||
                ! (initNetVarSelection(mapping) || initExternalSelection(mapping))) {
            setExpressionButton();
        }

        if (expressionButton.isSelected() && ! expressionButton.isVisible()) {
            enableQueryEditor(false);
        }
        if (_miQueryEditor != null) {
            disableChoicesForMIVariable();
        }
    }


    private void enableCombos(boolean enableNetVarsCombo, boolean enableGatewayCombo) {
        netVarsCombo.setEnabled(enableNetVarsCombo);
        gatewayCombo.setEnabled(enableGatewayCombo);
    }


    private void setExpressionButton() {
        expressionButton.setSelected(true);
        enableCombos(false, false);
    }

    private boolean initExternalSelection(String mapping) {
        if (mapping == null || ! mapping.contains("#external:")) return false;
        int first = mapping.indexOf(':');
        int last = mapping.lastIndexOf(':');
        if (first < 0 || last < 0) return false;
        String gatewayName = mapping.substring(first, last);
        for (int i = 0; i < gatewayCombo.getItemCount(); i++) {
            if (gatewayName.equals(gatewayCombo.getItemAt(i))) {
                enableCombos(false, true);
                gatewayCombo.setSelectedIndex(i);
                gatewayButton.setSelected(true);
                return true;
            }
        }
        return false;          // gateway not in list
    }


    private boolean initNetVarSelection(String mapping) {
        if (mapping == null) return false;

        DefaultMapping defMapping = new DefaultMapping(mapping);
        if (defMapping.isCustomMapping()) return false;

        String netVarName = null;
        if (_taskRow.isInput()) {
            VariableRow netVarRow = getNetVariableRow(defMapping.getVariableName());
            if (netVarRow != null && netVarRow.getDecompositionID().equals(
                    defMapping.getContainerName())) {
                netVarName = netVarRow.getName();
            }
        }
        else if (_taskRow.isOutput()) {
            netVarName = _taskRow.getNetVarForOutputMapping();
        }

        if (netVarName != null) {
            enableCombos(true, false);
            netVarsCombo.setSelectedItem(netVarName);
            if (netVarsCombo.getSelectedIndex() > -1) {
                netVarsButton.setSelected(true);
                return true;
            }
        }
        return false;
    }


    private JPanel createQueryPanel(MappingTypeValidator typeChecker) {
        queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(new TitledBorder(makeQueryTitle()));
        queryPanel.add(createAutoFormatPanel(getXQueryEditor(typeChecker)),
                BorderLayout.NORTH);
        queryPanel.add(_xQueryEditor, BorderLayout.CENTER);
        return queryPanel;
    }

    private JPanel createMiQueryPanel() {
        String title = (_taskRow.isInput() ? "Splitting" : "Joining") + " Query";
        JPanel miQueryPanel = new JPanel(new BorderLayout());
        miQueryPanel.setBorder(new TitledBorder(title));
        miQueryPanel.add(getMiQueryEditor(), BorderLayout.CENTER);
        return miQueryPanel;
    }

    private JPanel createAutoFormatPanel(final XQueryEditorPane editorPane) {
        String iconPath = "/org/yawlfoundation/yawl/editor/ui/resources/";
        JPanel content = new JPanel(new BorderLayout());
        JButton btnFormat = new JButton(IconList.getInstance().getIcon("autoformat.png"));

        btnFormat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = editorPane.getText();
                editorPane.setText(formatQuery(text, true));
            }
        });

        btnFormat.setToolTipText(" Auto-format query ");
        content.add(btnFormat, BorderLayout.EAST);

        if (_taskRow.isOutput()) {
            JPanel outerContent = new JPanel(new BorderLayout());
            JButton btnReset = new JButton(ResourceLoader.getImageAsIcon(
                    iconPath + "menuicons/arrow_undo.png"));

            btnReset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editorPane.setText(resetMapping(_taskRow));
                }
            });

            btnReset.setToolTipText(" Reset query to default ");
            content.add(btnReset, BorderLayout.CENTER);
            outerContent.add(content, BorderLayout.EAST);
            return outerContent;
        }

        return content;
    }

    private String formatQuery(String query, boolean prettify) {
        return XMLUtilities.formatXML(query, prettify, true);
    }


    private String makeQueryTitle() {
        return "Mapping for Task " + YDataHandler.getScopeName(_taskRow.getUsage()) +
                " Variable: " + _taskRow.getName();
    }


    private XQueryEditorPane getXQueryEditor(MappingTypeValidator typeChecker) {
        _xQueryEditor = new XQueryValidatingEditorPane();
        _xQueryEditor.setPreferredSize(new Dimension(400, 150));
        _xQueryEditor.setTypeChecker(typeChecker);
        _xQueryEditor.setValidating(true);
        _xQueryEditor.setTargetVariableName(_taskRow.getName());
        _xQueryEditor.setText(formatQuery(_taskRow.getMapping(), true));
        return _xQueryEditor;
    }

    private XQueryEditorPane getMiQueryEditor() {
        _miQueryEditor = new XQueryEditorPane();
        _miQueryEditor.setPreferredSize(new Dimension(400, 90));
        _miQueryEditor.setValidating(true);
        _miQueryEditor.setTargetVariableName(_taskRow.getName());
        _miQueryEditor.setText(formatQuery(_taskRow.getMIQuery(), true));
        return _miQueryEditor;
    }

    private JPanel createButtonBar() {
        JPanel panel = new JPanel(new GridLayout(0,2,5,5));
        panel.setBorder(new EmptyBorder(10,0,0,0));
        panel.add(createButton("Cancel"));
        okButton = createButton("OK");
        panel.add(okButton);
        return panel;
    }


    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setActionCommand(label);
        button.setMnemonic(label.charAt(0));
        button.addActionListener(this);
        return button;
    }


    // task output only
    private String resetMapping(VariableRow row) {
        if (netVarsButton.isSelected()) {
            return createMapping(row);
        }

        StringBuilder s = new StringBuilder();
        s.append("#external:").append(gatewayCombo.getSelectedItem())
                .append(":").append(_taskRow.getName());
        return s.toString();
    }


    // input only
    private String createMapping(VariableRow row) {
        StringBuilder s = new StringBuilder();
        s.append('/').append(row.getDecompositionID()).append('/').append(row.getName())
                .append('/').append(getXQuerySuffix(row));
        return s.toString();
    }


    private String getXQuerySuffix(VariableRow row) {
        return SpecificationModel.getHandler().getDataHandler().getXQuerySuffix(
                row.getDataType());
    }


    private JPanel buildIOPanel() {
        String title = _taskRow.isInput() ? "Input From" : "Output To";
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(title));
        panel.add(buildSelectionPanel(), BorderLayout.CENTER);
        panel.add(buildRadioPanel(), BorderLayout.WEST);
        panel.setPreferredSize(new Dimension(410, 110));
        return panel;
    }

    private JPanel buildRadioPanel() {
        ButtonGroup buttonGroup = new ButtonGroup();
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.setBorder(new EmptyBorder(0,10,0,10));

        netVarsButton = new JRadioButton("Net Variable: ");
        netVarsButton.setMnemonic(KeyEvent.VK_N);
        netVarsButton.setActionCommand("netVarRadio");
        netVarsButton.addActionListener(this);
        buttonGroup.add(netVarsButton);
        radioPanel.add(netVarsButton);

        gatewayButton = new JRadioButton("Data Gateway: ");
        gatewayButton.setMnemonic(KeyEvent.VK_D);
        gatewayButton.setActionCommand("gatewayRadio");
        gatewayButton.addActionListener(this);
        buttonGroup.add(gatewayButton);
        radioPanel.add(gatewayButton);

        // build even if output so spacing is consistent
        expressionButton = new JRadioButton("Expression");
        expressionButton.setMnemonic(KeyEvent.VK_E);
        expressionButton.setActionCommand("expressionRadio");
        expressionButton.addActionListener(this);
        buttonGroup.add(expressionButton);
        radioPanel.add(expressionButton);

        // output vars must map to a net var or data gateway
        if (_taskRow.isOutput()) expressionButton.setVisible(false);

        return radioPanel;
    }


    private JPanel buildSelectionPanel() {
        JPanel panel = new JPanel();
        netVarsCombo = buildComboBox(getNetVarNames());
        netVarsCombo.setActionCommand("netVarComboSelection");
        panel.add(netVarsCombo);
        gatewayCombo = buildComboBox(getDataGatewayNames());
        gatewayCombo.setActionCommand("gatewayComboSelection");
        panel.add(gatewayCombo);
        return panel;
    }


    private Vector<String> getNetVarNames() {
        Vector<String> names = new Vector<String>();
        for (VariableRow row : _netTablePanel.getTable().getVariables()) {
            names.add(row.getName());
        }
        return names;
    }


    private Vector<String> getDataGatewayNames() {
        try {
            return new Vector<String>(YConnector.getExternalDataGateways().keySet());
        }
        catch (IOException ioe) {
            return new Vector<String>();
        }
    }

    private JComboBox buildComboBox(Vector<String> items) {
        JComboBox comboBox = new JComboBox(items);
        comboBox.setPreferredSize(new Dimension(250,
                (int) comboBox.getPreferredSize().getHeight()));
        comboBox.addActionListener(this);
        return comboBox;
    }


    private void handleNetVarComboSelection() {
        String selectedNetVarName = getSelectedNetVar();

        // the target net var has changed, and thus maybe the target data type too
        if (_taskRow.isOutput()) {
            _typeValidator.setDataType(getDataTypeForNetVar(selectedNetVarName));
        }

        // update the mapping text based on the new net var selection
        VariableRow row = _taskRow.isInput() ?
                getNetVariableRow(selectedNetVarName) : // input query based on net row
                _taskRow;                               // output query based on task row

        if (row != null) {
             if (_taskRow.isOutput()) enableQueryEditor(true);
            _xQueryEditor.setText(createMapping(row));
        }
    }


    private void handleGatewayComboSelection() {
        VariableRow row = _taskRow.isInput() ?
                _taskRow :                                 // input query uses task row
                getNetVariableRow(getSelectedNetVar());    // output query uses net row

        if (row != null) {
            if (_taskRow.isOutput()) enableQueryEditor(true);
            String expression ="#external:" + gatewayCombo.getSelectedItem() + ":" +
                                              row.getName();
            _xQueryEditor.setText(expression);
        }
    }

    private VariableRow getNetVariableRow(String name) {
        for (VariableRow row : _netTablePanel.getTable().getVariables()) {
             if (row.getName().equals(name)) return row;
        }
        return null;
    }

    private String getDataTypeForNetVar(String netVarName) {
        if (netVarName == null) return null;
        VariableRow row = getNetVariableRow(netVarName);
        return row != null ? row.getDataType() : null;
    }


    private void enableContents(JPanel panel, boolean enable) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JPanel) {
                enableContents((JPanel) component, enable);
            }
            component.setEnabled(enable);
        }
    }


    private String getSelectedNetVar() {
        return (String) netVarsCombo.getSelectedItem();
    }

    private void enableQueryEditor(boolean enable) {
        enableContents(queryPanel, enable);
        _xQueryEditor.getEditor().setEnabled(enable);
        okButton.setEnabled(enable);
    }


    private void disableChoicesForMIVariable() {
        netVarsButton.setEnabled(false);
        gatewayButton.setEnabled(false);
        expressionButton.setEnabled(false);
        netVarsCombo.setEnabled(false);
        gatewayCombo.setEnabled(false);
    }


}