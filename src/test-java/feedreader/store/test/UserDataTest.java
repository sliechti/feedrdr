package feedreader.store.test;

import java.util.Properties;

import feedreader.entities.ProfileData;
import feedreader.entities.UserData;
import feedreader.store.Database;
import feedreader.store.UsersTable;


public final class UserDataTest {

    public static boolean DEBUGSQL = true;

    static final Class<?> clz = UserDataTest.class;

    static {
        Properties p = new Properties();
        p.setProperty(Database.JDBC_DRIVER_PROP_KEY, "org.postgresql.Driver");
        p.setProperty(Database.USERNAME_PROP_KEY, "sliechti");
        p.setProperty(Database.PASSWORD_PROP_KEY, "sliechti");
        p.setProperty(Database.URL_PROP_KEY, "jdbc:postgresql://localhost/postgres");
        Database.start(p);
    }

    public static void getUserTest() {
        UserData user = UsersTable.get(20);
        System.out.println(user);
        for (ProfileData p : user.getProfileData()) {
            System.out.println(p);
        }
        
        user = UsersTable.get("steven@liechti.de");
        System.out.println(user);
        for (ProfileData p : user.getProfileData()) {
            System.out.println(p);
        }      
        
        user = UsersTable.get("test@feedrdr.co", "test");
        System.out.println(user);
        for (ProfileData p : user.getProfileData()) {
            System.out.println(p);
        }          
    }
    
    public static void main(String[] args) {
        getUserTest();
    }

}
