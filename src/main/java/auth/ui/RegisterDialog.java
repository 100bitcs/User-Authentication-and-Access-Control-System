package auth.ui;

import auth.core.AuthService;
import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {
    public RegisterDialog(JFrame owner,AuthService service){
        super(owner,"Create account",true);
        JTextField username=new JTextField(), name=new JTextField(), email=new JTextField();
        JPasswordField password=new JPasswordField();
        JComboBox<String> role=new JComboBox<>(new String[]{"GUEST","STAFF"});
        JPanel p=new JPanel(new GridLayout(0,2,8,8)); p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        p.add(new JLabel("Full name"));p.add(name);p.add(new JLabel("Email"));p.add(email);
        p.add(new JLabel("Username"));p.add(username);p.add(new JLabel("Password"));p.add(password);
        p.add(new JLabel("Role"));p.add(role);
        JButton save=new JButton("Create account");p.add(new JLabel());p.add(save);add(p);
        save.addActionListener(e->{try{
            service.register(username.getText().trim(),new String(password.getPassword()),name.getText().trim(),
                email.getText().trim(),(String)role.getSelectedItem());
            JOptionPane.showMessageDialog(this,"Account created. You can now log in.");dispose();
        }catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Registration failed",JOptionPane.ERROR_MESSAGE);}});
        setSize(430,330);setLocationRelativeTo(owner);
    }
}