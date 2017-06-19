package de.yadrone.apps.cdio.gui;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * AboutPanel
 * @author Lasse
 * @version 13-06-2017
 */
public class AboutPanel extends javax.swing.JPanel
{
    private int start = 0;
    private int end = 0;
    
    public AboutPanel() {
        initComponents();
        
        DefaultCaret caret = (DefaultCaret) jtpContent.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        
        StyledDocument doc = jtpContent.getStyledDocument();
        
        SimpleAttributeSet title = new SimpleAttributeSet();
        StyleConstants.setAlignment(title, StyleConstants.ALIGN_CENTER);
        StyleConstants.setBold(title, true);
        StyleConstants.setFontSize(title, 13);
        
        SimpleAttributeSet highlight = new SimpleAttributeSet();
        StyleConstants.setAlignment(highlight, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(highlight, 12);
        
        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setAlignment(normal, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(normal, 11);
        
        try {
            addText(getAboutProject(), highlight, doc);
            addText("\n-- Creators --\n", title, doc);
            addText(getAboutUs(), normal, doc);
            addText("\n-- License --\n", title, doc);
            addText(getAboutLicense(), normal, doc);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    private void addText(String text, SimpleAttributeSet style, StyledDocument doc) throws BadLocationException {
        end =+ text.length() + 1;
        doc.insertString(doc.getLength(), text, style);
        doc.setParagraphAttributes(start, end, style, true);
        start = end;
    }
    
    private static String getAboutProject() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("DFM Drone Client version: 1.0\n");
        sb.append("Drone client for the Parrot AR2.0 Drone\n");
        sb.append("This project is a result of a group project at the Technical Univercity of Denmark (DTU)\n");
        
        return sb.toString();
    }
    
    private static String getAboutUs() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Mathias Kvisgaard Hansen\n");
        sb.append("Magnus Erengaard Enevoldsen\n");
        sb.append("Martin Rødgaard\n");
        sb.append("Asger Stage\n");
        sb.append("Frank Thomsen\n");
        sb.append("Lasse Holm Nielsen\n");
        
        sb.append("\nSupervisors:\n");
        sb.append("Paul FischerPaul\n");
        sb.append("Henrik Bechmann\n");
        sb.append("Kurt Jeritslev\n");
        
        return sb.toString();
    }
    
    private static String getAboutLicense() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Icons:\n");
        sb.append("Artist: Icons8\nWebsite: icons8.com\n");
        sb.append("\nCode:\n");
        sb.append("Licensed under Creative Commons BY-NC-SA 4.0\nWebsite: creativecommons.org/licenses/by-nc-sa/4.0/\n");
        
        return sb.toString();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtpContent = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jbExit = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("About");
        jLabel1.setFocusable(false);

        jtpContent.setEditable(false);
        jtpContent.setBackground(new java.awt.Color(240, 240, 240));
        jtpContent.setFocusable(false);
        jScrollPane2.setViewportView(jtpContent);

        jPanel1.setLayout(new java.awt.GridLayout());
        jPanel1.add(jLabel3);

        jbExit.setText("OK");
        jbExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbExitActionPerformed(evt);
            }
        });
        jPanel1.add(jbExit);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dfmDrone/gui/symbols/License.png"))); // NOI18N
        jPanel1.add(jLabel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbExitActionPerformed
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.dispose();
    }//GEN-LAST:event_jbExitActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbExit;
    private javax.swing.JTextPane jtpContent;
    // End of variables declaration//GEN-END:variables
}