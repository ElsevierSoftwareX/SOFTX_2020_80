package org.yawlfoundation.yawl.editor.ui.actions.element;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.actions.net.PreviewConfigurationProcessAction;
import org.yawlfoundation.yawl.editor.ui.elements.model.CPort;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraph;
import org.yawlfoundation.yawl.editor.ui.swing.TooltipTogglingWidget;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.util.List;


public class OutputPortConfigurationAction extends YAWLBaseAction implements TooltipTogglingWidget {

	
	
private static final long serialVersionUID = 1L;	
	{
	    putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
	    putValue(Action.NAME, "Output Ports...");
	    putValue(Action.LONG_DESCRIPTION, "Configure the output ports for this task.");    
      putValue(Action.SMALL_ICON, getPNGIcon("arrow_out"));
	  }
	
	private NetGraph net;
	private YAWLTask task;
	 
	 public OutputPortConfigurationAction( YAWLTask task, NetGraph net) {
		  super();
		  this.task = task;
		  this.net = net;
	  }  
	
	public void actionPerformed(ActionEvent event) {
		final YAWLTask task = this.task;
		
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                OutputConfigurationJDialog dialog = new OutputConfigurationJDialog(new javax.swing.JFrame(), true, task, net);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    
                });
                dialog.setLocationRelativeTo(YAWLEditor.getInstance());
                dialog.setSize(dialog.getSize());
                dialog.setResizable(false);
                dialog.setVisible(true);
            }
        });
	  }

	public String getDisabledTooltipText() {
		return null;
	}


	public String getEnabledTooltipText() {
		return "Configure the output ports for this task.";
	}
    
	/**
	 *
	 * @author jingxin
	 */
	private class OutputConfigurationJDialog extends javax.swing.JDialog {
		
		private List<CPort> OutputPorts;
		
		private YAWLTask task;
		
		private int size;
		
		private TableColumn column = null;
		private TableColumn IDcolumn = null;
		private NetGraph net;
		private ActionEvent simulateEvent = new ActionEvent(this,1001,"Preview Process Configuration");
		private PreviewConfigurationProcessAction simulateAction =
            PreviewConfigurationProcessAction.getInstance();
		
		/** Creates new form NewJDialog */
	    public OutputConfigurationJDialog(java.awt.Frame parent, boolean modal,YAWLTask task, NetGraph net) {
	        super(parent, modal);
	        this.task = task;
	        this.net = net;
	        size = this.task.getOutputCPorts().size();
	        initOutputPorts();
	        initComponents();
	    }
	    
	    public void  initOutputPorts(){
	    	this.OutputPorts = this.task.getOutputCPorts();
	    }
	    public Object[][] getPortsInformation(){
	    	Object[][] rowInfor = new Object[size][3];
	    	
	    	
	    	for(int i=0; i<size ; i++){	
	    			rowInfor[i][0] = this.OutputPorts.get(i).getID();
	    			rowInfor[i][1] = this.OutputPorts.get(i).getTargetTasksLabels();
	    			rowInfor[i][2] = this.OutputPorts.get(i).getConfigurationSetting();		
	    	}
	    	return rowInfor;
	    }
	    /** This method is called from within the constructor to
	     * initialize the form.
	     * WARNING: Do NOT modify this code. The content of this method is
	     * always regenerated by the Form Editor.
	     */
	    @SuppressWarnings("unchecked")
	    // <editor-fold defaultstate="collapsed" desc="Generated Code">
	    private void initComponents() {

	        jLabel1 = new javax.swing.JLabel();
	        jScrollPane1 = new javax.swing.JScrollPane();
	        outputPortsConfigurationTable = new javax.swing.JTable();
	        ActivateButton = new javax.swing.JButton();
	        BlockButton = new javax.swing.JButton();
	        SetDefaultButton = new javax.swing.JButton();
	        DefaultButton = new javax.swing.JButton();

	        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	        this.setTitle("Output Ports Configuration");
	        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14));
	        jLabel1.setText("Please select which ports you want to configure.");

	        outputPortsConfigurationTable.setModel(new javax.swing.table.DefaultTableModel(
	        		 this.getPortsInformation(),
	                 new String [] {
	        				 "Port ID", "Target Nodes",  "Configuration"
	                 }
	        ) {
	            Class[] types = new Class [] {
	                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
	            };
	            boolean[] canEdit = new boolean [] {
	                false, false, false
	            };

	            public Class getColumnClass(int columnIndex) {
	                return types [columnIndex];
	            }

	            public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit [columnIndex];
	            }
	        });
	        
	       
	        for (int i = 0; i < 3; i++) {
	            column = outputPortsConfigurationTable.getColumnModel().getColumn(i);
	            if (i == 0) {
	                column.setPreferredWidth(15); 
	                IDcolumn = column;
	            } 
	            if (i == 1) {
	                column.setPreferredWidth(160);
	            } 
	            if (i == 2) {
	                column.setPreferredWidth(50); 
	            } 
	        }
	        
	        for(int i=0; i<this.outputPortsConfigurationTable.getRowCount(); i++){
	        	column.setCellRenderer(new ConfigurationTableCellRenderer());
	        }
	        IDcolumn.setCellRenderer(new PortIDRenderer());
	        jScrollPane1.setViewportView(outputPortsConfigurationTable);

	        ActivateButton.setText("Activate");
	        ActivateButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                ActivateButtonActionPerformed(evt);
	            }
	        });

	        BlockButton.setText("Block");
	        BlockButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                BlockButtonActionPerformed(evt);
	            }
	        });

	       

	        DefaultButton.setText("Use Default");
	        DefaultButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                DefaultButtonActionPerformed(evt);
	            }
	        });

	        SetDefaultButton.setText("Set Defaults");
	        SetDefaultButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                SetDefaultButtonActionPerformed(evt);
	            }
	        });
	        
	        GroupLayout layout = new GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(GroupLayout.LEADING)
	            .add(layout.createSequentialGroup()
	                .addContainerGap()
	                .add(layout.createParallelGroup(GroupLayout.LEADING)
	                    .add(layout.createSequentialGroup()
	                        .add(jScrollPane1, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
	                        .addContainerGap())
	                    .add(layout.createSequentialGroup()
	                        .add(4, 4, 4)
	                        .add(ActivateButton)
	                        .addPreferredGap(LayoutStyle.RELATED)
	                        .add(BlockButton)
	                        .addPreferredGap(LayoutStyle.RELATED)
	                        .add(DefaultButton)
	                        .addPreferredGap(LayoutStyle.RELATED, 61, Short.MAX_VALUE)
	                        .add(SetDefaultButton)
	                        .add(21, 21, 21))))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(GroupLayout.LEADING)
	            .add(layout.createSequentialGroup()
	                .add(19, 19, 19)
	                .add(jScrollPane1, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.RELATED, 41, Short.MAX_VALUE)
	                .add(layout.createParallelGroup(GroupLayout.BASELINE)
	                    .add(SetDefaultButton)
	                    .add(DefaultButton)
	                    .add(BlockButton)
	                    .add(ActivateButton))
	                .add(34, 34, 34))
	        );

	       // bindingGroup.bind();
	        if(this.net.getConfigurationSettings().isAllowChangingDefaultConfiguration()){
	        	this.SetDefaultButton.setEnabled(true);
	        }else{
	        	this.SetDefaultButton.setEnabled(false);
	        }
	        this.outputPortsConfigurationTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	        this.jScrollPane1.setAutoscrolls(true);
	        this.outputPortsConfigurationTable.addMouseListener(new java.awt.event.MouseAdapter() {
	        	public void mouseReleased(java.awt.event.MouseEvent evt) {
	                jTableMouseReleased(evt);
	            }
	        });
	        //this.jScrollPane1.setMaximumSize(new Dimension(this.jScrollPane1.getWidth()*6,this.jScrollPane1.getHeight()*6));
	        
	        pack();
	    }// </editor-fold>

	    private void ActivateButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
	        // TODO add your handling code here:
	        int length = this.outputPortsConfigurationTable.getSelectedRowCount();
	        int[] selectedRows = this.outputPortsConfigurationTable.getSelectedRows();
	        for(int i=0; i<length; i++){
	        	ActivateAPort(selectedRows, i);
	        }
	        if(this.net.getConfigurationSettings().isApplyAutoGreyOut()){
	        	this.simulateAction.actionPerformed(simulateEvent);
	        }
          toggleEnabled(ActivateButton);
	    }

		private void ActivateAPort(int[] selectedRows, int i) {
			int portId = (Integer) this.outputPortsConfigurationTable.getValueAt(selectedRows[i], 0);
			this.OutputPorts.get(portId).setConfigurationSetting("activated");
			this.outputPortsConfigurationTable.setValueAt("activated", selectedRows[i], 2);
			if(this.net.getServiceAutonomous() != null){
				this.net.getServiceAutonomous().changeCurrentStateAfterActivate(task, "OUTPUT", portId);
			}
		}                                            

	    private void BlockButtonActionPerformed(java.awt.event.ActionEvent evt) {
	        // TODO add your handling code here:
	    	int length = this.outputPortsConfigurationTable.getSelectedRowCount();
	        int[] selectedRows = this.outputPortsConfigurationTable.getSelectedRows();
	        for(int i=0; i<length; i++){
	        	BlockAPort(selectedRows, i);
	        }
	        if(this.net.getConfigurationSettings().isApplyAutoGreyOut()){
	        	this.simulateAction.actionPerformed(simulateEvent);
	        }
	        toggleEnabled(BlockButton);
	    }

		private void BlockAPort(int[] selectedRows, int i) {
			int portId = (Integer) this.outputPortsConfigurationTable.getValueAt(selectedRows[i], 0);
			this.OutputPorts.get(portId).setConfigurationSetting("blocked");
			this.outputPortsConfigurationTable.setValueAt("blocked", selectedRows[i], 2);
			if(this.net.getServiceAutonomous() != null ){
				this.net.getServiceAutonomous().changeCurrentStateAfterBlock(task, "OUTPUT", portId);
			}
		}

	    private void DefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {
	        // TODO add your handling code here:
	    	int length = this.outputPortsConfigurationTable.getSelectedRowCount();
	        int[] selectedRows = this.outputPortsConfigurationTable.getSelectedRows();
	        for(int i=0; i<length; i++){
	        	int portId = (Integer) this.outputPortsConfigurationTable.getValueAt(selectedRows[i], 0);
	        	if(this.OutputPorts.get(portId).getDefaultValue() != null){
		        	if(this.OutputPorts.get(portId).getDefaultValue().equals(CPort.ACTIVATED)){
		        		ActivateAPort(selectedRows, i);
		        	} else if (this.OutputPorts.get(portId).getDefaultValue().equals(CPort.BLOCKED)){
		        		BlockAPort(selectedRows, i);
		        	}
	        	}
	        }
	        if(this.net.getConfigurationSettings().isApplyAutoGreyOut()){
	        	this.simulateAction.actionPerformed(simulateEvent);
	        }
	    }
	    
	    private void SetDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {
	        // TODO add your handling code here:
		// TODO add your handling code here:
	    	 final YAWLTask task = this.task;
	    	 java.awt.EventQueue.invokeLater(new Runnable() {
		            public void run() {
		                SetOutputPortDefaultConfigurationJDialog dialog = new SetOutputPortDefaultConfigurationJDialog(new javax.swing.JFrame(), true, net, task);
		                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
		                });
		                dialog.setLocationRelativeTo(YAWLEditor.getInstance());
		                dialog.setSize(dialog.getSize());
		                dialog.setVisible(true);
		            }
		        });
	    }
	    private void jTableMouseReleased(java.awt.event.MouseEvent evt) {
	        // TODO add your handling code here:
	        boolean DefaultButtonFlag = false;
	        boolean defaultBlockFlag = true;
	        boolean defaultActivateFlag = true;
	        boolean blockFlag = true;
	        boolean activateFlag = true;
          String s = null;
	        int length = this.outputPortsConfigurationTable.getSelectedRowCount();
	        int[] selectedRows = this.outputPortsConfigurationTable.getSelectedRows();
	        for(int i=0; i<length; i++){
	        	int portId = (Integer) this.outputPortsConfigurationTable.getValueAt(selectedRows[i], 0);
	        	if(this.OutputPorts.get(portId).getDefaultValue() != null){
	        		DefaultButtonFlag = true;
	        	}
	        	if(this.net.getServiceAutonomous() != null){
	        		blockFlag = blockFlag && (this.net.getServiceAutonomous().processCorrectnessCheckingForBlock(task, "OUTPUT", portId));
	        		
	        		if(this.OutputPorts.get(portId).getConfigurationSetting().equals(CPort.BLOCKED)){
	        			activateFlag = activateFlag 
	        							&& (this.net.getServiceAutonomous().processCorrectnessCheckingForActivate(task, "OUTPUT", portId));
	        		}
	        		if(this.OutputPorts.get(portId).getDefaultValue() != null){
	        			if(this.OutputPorts.get(portId).getDefaultValue().equals(CPort.BLOCKED)){
	        				defaultBlockFlag = defaultBlockFlag && (this.net.getServiceAutonomous().processCorrectnessCheckingForBlock(task, "OUTPUT", portId));
	        			} else if (this.OutputPorts.get(portId).getDefaultValue().equals(CPort.ACTIVATED)){
	        				defaultActivateFlag = defaultActivateFlag &&
							(this.net.getServiceAutonomous().processCorrectnessCheckingForActivate(task, "OUTPUT", portId));
	        			}
	        		}
	        	}
              s = (String) outputPortsConfigurationTable.getValueAt(selectedRows[i], 2);

	        }
	        this.DefaultButton.setEnabled(DefaultButtonFlag && defaultBlockFlag && defaultActivateFlag);
	        this.BlockButton.setEnabled(blockFlag && ! matchText(s, "blocked"));
	        this.ActivateButton.setEnabled(activateFlag && ! matchText(s, "activated"));
	    }

      private boolean matchText(String s, String other) {
          return (s != null) && (s.equals(other));
      }


      private void toggleEnabled(JButton btn) {
          ActivateButton.setEnabled(btn != ActivateButton);
          BlockButton.setEnabled(btn != BlockButton);
      }
	    
	    
	    // Variables declaration - do not modify
	    private javax.swing.JButton ActivateButton;
	    private javax.swing.JButton BlockButton;
	    private javax.swing.JButton DefaultButton;
	    private javax.swing.JButton SetDefaultButton;
	    private javax.swing.JTable outputPortsConfigurationTable;
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JScrollPane jScrollPane1;
	    
	}

private class SetOutputPortDefaultConfigurationJDialog extends javax.swing.JDialog {
	    
		private NetGraph net;
		private YAWLTask task;
		private Object[][] rowInfor;
		private List<CPort> outputPorts;
		
		public void  initOutputPorts(){
	    	this.outputPorts = this.task.getOutputCPorts();   
	    	System.out.println(this.outputPorts.size());
	    }
	    public Object[][] getPortsInformation(){
	    	int size = this.outputPorts.size();
			Object[][] rowInfor = new Object[size ][3];
	    	for(int i=0; i<size ; i++){	
	    			rowInfor[i][0] = this.outputPorts.get(i).getID();
	    			rowInfor[i][1] = this.outputPorts.get(i).getTargetTasksLabels();
	    			rowInfor[i][2] = this.outputPorts.get(i).getDefaultValue();		
	    	}
	    	return rowInfor;
	    }
	    
		
	    /** Creates new form NewJDialog  */
	    public SetOutputPortDefaultConfigurationJDialog(java.awt.Frame parent, boolean modal, NetGraph net, YAWLTask task) {
	        super(parent, modal);
	        this.net = net;
	        this.task = task;
	        initOutputPorts();
	        this.rowInfor = this.getPortsInformation();
	        initComponents();
	        
	    }
	    
	    /** This method is called from within the constructor to
	     * initialize the form.
	     * WARNING: Do NOT modify this code. The content of this method is
	     * always regenerated by the Form Editor.
	     */
	    @SuppressWarnings("unchecked")
	    // <editor-fold defaultstate="collapsed" desc="Generated Code">
	    private void initComponents() {
	    	
	        jScrollPane1 = new javax.swing.JScrollPane();
	        jTable1 = new javax.swing.JTable();
	        AllowButton = new javax.swing.JButton();
	        BlockButton = new javax.swing.JButton();
	        EmptyButton = new javax.swing.JButton();
	        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	        this.setTitle("Output Ports Default Configuration Setting");

	        jTable1.setModel(new javax.swing.table.DefaultTableModel(
	            rowInfor,
	            new String [] {
	                "Port ID", "Target Nodes", "Default Configuration"
	            }
	        ) {
	            Class[] types = new Class [] {
	                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
	            };
	            boolean[] canEdit = new boolean [] {
	                false, false, false
	            };

	            public Class getColumnClass(int columnIndex) {
	                return types [columnIndex];
	            }

	            public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit [columnIndex];
	            }
	        });
	        
	        jTable1.getColumnModel().getColumn(0).setCellRenderer(new PortIDRenderer());
	        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ConfigurationTableCellRenderer());
	        jScrollPane1.setViewportView(jTable1);
	        jTable1.getColumnModel().getColumn(0).setPreferredWidth(15);
	        jTable1.getColumnModel().getColumn(2).setPreferredWidth(50);
          jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
              public void mouseReleased(java.awt.event.MouseEvent evt) {
                  String s = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 2);
                  AllowButton.setEnabled(! matchText(s, "activated"));
                  BlockButton.setEnabled(! matchText(s, "blocked"));
              }
          });

	        
	        AllowButton.setText("Activate");
	        AllowButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                AllowButtonActionPerformed(evt);
	            }
	        });

	        BlockButton.setText("Block ");
	        BlockButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                BlockButtonActionPerformed(evt);
	            }
	        });

	        EmptyButton.setText("No Default");
	        EmptyButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                EmptyButtonActionPerformed(evt);
	            }
	        });

	        GroupLayout layout = new GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(GroupLayout.LEADING)
	            .add(layout.createSequentialGroup()
	                .addContainerGap()
	                .add(layout.createParallelGroup(GroupLayout.LEADING)
	                    .add(layout.createSequentialGroup()
	                        .add(AllowButton)
	                        .add(88, 88, 88)
	                        .add(BlockButton)
	                        .addPreferredGap(LayoutStyle.RELATED, 88, Short.MAX_VALUE)
	                        .add(EmptyButton))
	                    .add(GroupLayout.TRAILING, jScrollPane1, 
                          GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
	                .addContainerGap())
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(GroupLayout.LEADING)
	            .add(GroupLayout.TRAILING, layout.createSequentialGroup()
	                .add(33, 33, 33)
	                .add(jScrollPane1, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.RELATED, 48, Short.MAX_VALUE)
	                .add(layout.createParallelGroup(GroupLayout.BASELINE)
	                    .add(EmptyButton)
	                    .add(AllowButton)
	                    .add(BlockButton))
	                .add(20, 20, 20))
	        );
	     
	        this.jScrollPane1.setAutoscrolls(true);
	        this.jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	       
	        pack();
	    }// </editor-fold>

	private void AllowButtonActionPerformed(java.awt.event.ActionEvent evt) {
	// TODO add your handling code here:
		int length = jTable1.getSelectedRowCount();
	        int[] selectedRows = this.jTable1.getSelectedRows();
	        for(int i=0; i<length; i++){
	        	int portId = (Integer) this.jTable1.getValueAt(selectedRows[i], 0);
	        	this.outputPorts.get(portId).setDefaultValue(CPort.ACTIVATED);
	        	this.jTable1.setValueAt("activated", selectedRows[i], 2);	
	        }
      toggleEnabled(AllowButton);
	}

	private void BlockButtonActionPerformed(java.awt.event.ActionEvent evt) {
	// TODO add your handling code here:
		int length = jTable1.getSelectedRowCount();
        int[] selectedRows = this.jTable1.getSelectedRows();
        for(int i=0; i<length; i++){
        	int portId = (Integer) this.jTable1.getValueAt(selectedRows[i], 0);
        	this.outputPorts.get(portId).setDefaultValue(CPort.BLOCKED);
        	this.jTable1.setValueAt(CPort.BLOCKED, selectedRows[i], 2);	
        }
      toggleEnabled(BlockButton);
	}

	 private void EmptyButtonActionPerformed(java.awt.event.ActionEvent evt) {
	        // TODO add your handling code here:
		 int length = jTable1.getSelectedRowCount();
	        int[] selectedRows = this.jTable1.getSelectedRows();
	        for(int i=0; i<length; i++){
	        	int portId = (Integer) this.jTable1.getValueAt(selectedRows[i], 0);
	        	this.outputPorts.get(portId).setDefaultValue(null);
	        	this.jTable1.setValueAt(null, selectedRows[i], 2);	
	        }
       toggleEnabled(EmptyButton);
	    }

      private boolean matchText(String s, String other) {
          return (s != null) && (s.equals(other));
      }


      private void toggleEnabled(JButton btn) {
          AllowButton.setEnabled(btn != AllowButton);
          BlockButton.setEnabled(btn != BlockButton);
      }

	    // Variables declaration - do not modify
	    private javax.swing.JButton AllowButton;
	    private javax.swing.JButton BlockButton;
	    private javax.swing.JButton EmptyButton;
	    private javax.swing.JScrollPane jScrollPane1;
	    private javax.swing.JTable jTable1;
	    
	    // End of variables declaration
	    
	}

}
