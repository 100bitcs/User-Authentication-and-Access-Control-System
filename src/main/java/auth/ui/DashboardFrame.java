package auth.ui;

import auth.core.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    public DashboardFrame(User user,AuthService service){
        super("Dashboard - "+user.getRoleName());setSize(720,420);setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel top=new JPanel(new BorderLayout());top.add(new JLabel("Welcome, "+user.getFullName()+" | Role: "+user.getRoleName()),BorderLayout.WEST);
        JButton logout=new JButton("Logout");top.add(logout,BorderLayout.EAST);add(top,BorderLayout.NORTH);
        JTextArea info=new JTextArea();info.setEditable(false);
        info.setText("Signed in as: "+user.getUsername()+"\nRole: "+user.getRoleName()+"\\n\\nPermissions:\\n - "+
            String.join("\n - ",user.getPermissions()));
        add(new JScrollPane(info),BorderLayout.CENTER);
        JPanel actions=new JPanel();
        JButton report=new JButton("View access report");report.setEnabled(user.hasPermission("ACCESS_REPORT"));
        JButton manage=new JButton("User management");manage.setEnabled(user.hasPermission("USER_MANAGE"));
        actions.add(report);actions.add(manage);add(actions,BorderLayout.SOUTH);
        report.addActionListener(e->showReport(service));
        manage.addActionListener(e->JOptionPane.showMessageDialog(this,"User management permission verified. Extend this module here."));
        logout.addActionListener(e->{new LoginFrame().setVisible(true);dispose();});
    }
    private void showReport(AuthService service){
        try{
            List<String[]> rows=service.getAccessReport();
            String[] cols={"Username","Full name","Role","Status","Created at"};
            DefaultTableModel model=new DefaultTableModel(cols,0);
            for(String[] row:rows)model.addRow(row);
            JTable table=new JTable(model);
            JOptionPane.showMessageDialog(this,new JScrollPane(table),"Access report",JOptionPane.PLAIN_MESSAGE);
        }catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Report error",JOptionPane.ERROR_MESSAGE);}
    }
}