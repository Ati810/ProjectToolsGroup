package utilities;

/**
 * Class that contains the information of the user who is logging in.
 * @author Attila
 */
public class UserInfo {
    
    /**
     * The name of the user.
     */
    public String username;

    /**
     * The password of the user.
     */
    public String password;

    /**
     * Constructor for creating a new UserInfo.
     * @param username The name of the user.
     * @param password The password of the user.
     */
    public UserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
}
