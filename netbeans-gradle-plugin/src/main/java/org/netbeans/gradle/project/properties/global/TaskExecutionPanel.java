package org.netbeans.gradle.project.properties.global;

import javax.swing.JComponent;
import org.jtrim.property.PropertyFactory;
import org.jtrim.property.PropertySource;
import org.netbeans.gradle.project.properties.GlobalGradleSettings;

@SuppressWarnings("serial")
public class TaskExecutionPanel extends javax.swing.JPanel implements GlobalSettingsEditor {
    public TaskExecutionPanel() {
        initComponents();
    }

    @Override
    public void updateSettings() {
        jAlwayClearOutput.setSelected(GlobalGradleSettings.getAlwaysClearOutput().getValue());
        jDontAddInitScriptCheck.setSelected(GlobalGradleSettings.getOmitInitScript().getValue());
        jSkipTestsCheck.setSelected(GlobalGradleSettings.getSkipTests().getValue());
        jSkipCheckCheckBox.setSelected(GlobalGradleSettings.getSkipCheck().getValue());
    }

    @Override
    public void saveSettings() {
        GlobalGradleSettings.getSkipTests().setValue(jSkipTestsCheck.isSelected());
        GlobalGradleSettings.getSkipCheck().setValue(jSkipCheckCheckBox.isSelected());
        GlobalGradleSettings.getAlwaysClearOutput().setValue(jAlwayClearOutput.isSelected());
        GlobalGradleSettings.getOmitInitScript().setValue(jDontAddInitScriptCheck.isSelected());
    }

    @Override
    public PropertySource<Boolean> valid() {
        return PropertyFactory.constSource(true);
    }

    @Override
    public JComponent getEditorComponent() {
        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSkipTestsCheck = new javax.swing.JCheckBox();
        jSkipCheckCheckBox = new javax.swing.JCheckBox();
        jAlwayClearOutput = new javax.swing.JCheckBox();
        jDontAddInitScriptCheck = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(jSkipTestsCheck, org.openide.util.NbBundle.getMessage(TaskExecutionPanel.class, "TaskExecutionPanel.jSkipTestsCheck.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jSkipCheckCheckBox, org.openide.util.NbBundle.getMessage(TaskExecutionPanel.class, "TaskExecutionPanel.jSkipCheckCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jAlwayClearOutput, org.openide.util.NbBundle.getMessage(TaskExecutionPanel.class, "TaskExecutionPanel.jAlwayClearOutput.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jDontAddInitScriptCheck, org.openide.util.NbBundle.getMessage(TaskExecutionPanel.class, "TaskExecutionPanel.jDontAddInitScriptCheck.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSkipTestsCheck)
                    .addComponent(jAlwayClearOutput)
                    .addComponent(jDontAddInitScriptCheck)
                    .addComponent(jSkipCheckCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSkipTestsCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSkipCheckCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jAlwayClearOutput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDontAddInitScriptCheck)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jAlwayClearOutput;
    private javax.swing.JCheckBox jDontAddInitScriptCheck;
    private javax.swing.JCheckBox jSkipCheckCheckBox;
    private javax.swing.JCheckBox jSkipTestsCheck;
    // End of variables declaration//GEN-END:variables
}
