package auth.core;
import java.util.Set;
public class Admin extends User {
    public Admin(int id,String u,String h,String n,String e,Set<String> p){super(id,u,h,n,e,p);}
    @Override public String getRoleName(){return "ADMIN";}
}