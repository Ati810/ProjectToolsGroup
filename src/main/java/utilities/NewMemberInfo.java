package utilities;

/**
 * Class that contains the information of a new Member.
 * @author Attila
 */
public class NewMemberInfo {
    
    /**
     * The name of the Member.
     */
    public String name;

    /**
     * The address of the Member.
     */
    public String address;

    /**
     * Constructor for creating a new NewMemberInfo.
     * @param name The name of the new Member.
     * @param address The address of the new Member.
     */
    public NewMemberInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }
    
}
