package auth.ui;

import auth.core.*;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService service=new AuthService();
    private final JTextField username=new JTextField(18);
    private final JPasswordField password=new JPasswordField(18);

    public LoginFrame(){
        super("User Authentication and Access Control System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480,300); setLocationRelativeTo(null);
        JPanel panel=new JPanel(new GridLayout(0,2,10,12)); panel.setBorder(BorderFactory.createEmptyBorder(25,30,25,30));
        panel.add(new JLabel("Username")); panel.add(username);
        panel.add(new JLabel("Password")); panel.add(password);
        JButton login=new JButton("Login"), register=new JButton("Register");
        panel.add(login); panel.add(register); add(panel);
        login.addActionListener(e->doLogin());
        register.addActionListener(e->new RegisterDialog(this,service).setVisible(true));
    }
    private void doLogin(){
        try{
            User user=service.login(username.getText().trim(),new String(password.getPassword()));
            JOptionPane.showMessageDialog(this,"Welcome, "+user.getFullName()+" ("+user.getRoleName()+")");
            new DashboardFrame(user,service).setVisible(true); dispose();
        }catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Login failed",JOptionPane.ERROR_MESSAGE);}
    }
    public static void main(String[] args){SwingUtilities.invokeLater(()->new LoginFrame().setVisible(true));}
}