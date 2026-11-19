package auth.core;
import java.util.Set;
public class Guest extends User {
    public Guest(int id,String u,String h,String n,String e,Set<String> p){super(id,u,h,n,e,p);}
    @Override public String getRoleName(){return "GUEST";}
}