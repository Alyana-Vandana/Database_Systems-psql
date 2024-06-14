package ApnaBank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AccountDetails extends JFrame {
    private JLabel label1;
    private JLabel txt_name;
    private JLabel lbl_timer;
    private JLabel label3;
    private JLabel txt_balance;
    private JTable datadrid_view;
    private JLabel label5;
    private JButton button1;

    public AccountDetails() {
        initComponents();
    }

    private void initComponents() {
        label1 = new JLabel();
        txt_name = new JLabel();
        lbl_timer = new JLabel();
        label3 = new JLabel();
        txt_balance = new JLabel();
        datadrid_view = new JTable();
        label5 = new JLabel();
        button1 = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("AccountDetails");
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        label1.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12));
        label1.setText("Welcome ,");

        txt_name.setText("name");

        lbl_timer.setText("Time");

        label3.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12));
        label3.setText("Balance :");

        txt_balance.setText("bal");

        datadrid_view.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Column 1", "Column 2", "Column 3"
                }
        ));
        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.setViewportView(datadrid_view);

        label5.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 12));
        label5.setText("Last 5 Transactions");

        button1.setText("Refresh");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(label1)
                                                        .addComponent(label3))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(txt_name)
                                                        .addComponent(txt_balance))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lbl_timer))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(label5)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(button1)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label1)
                                        .addComponent(txt_name)
                                        .addComponent(lbl_timer))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label3)
                                        .addComponent(txt_balance))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(label5)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button1)
                                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {
        // Refresh button action
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AccountDetails().setVisible(true);
            }
        });
    }
}
