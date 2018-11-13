package thedankdevs.tcss450.uw.edu.tddevschat.utils;

/**
 * This class holds all the information of the
 * currently logged in user. Since it's a class that contains
 * static fields and methods, the methods can be accessed without the
 * need of instantiating a new object. Also, because the fields are static,
 * the fields are initialized during execution and are shared by the class
 * rather by objects.
 *
 * @author Bryan Santos
 * @version 11/12/2018
 */
public class UserInfo {

    private static int memberId;
    private static String firstName;
    private static String lastName;
    private static String username;
    private static String email;

    static {
        memberId = -1;
        firstName = null;
        lastName = null;
        username = null;
        email = null;
    }

    // client does not need to initialize a new object of this class.
    // call setAllFields() instead to change the values of the fields
    private UserInfo() {
        // left empty on purpose
    }

    /**
     * Acts as a pseudo-constructor to set all the fields of the user.
     *
     * @param theMemberId  memberId of user
     * @param theFirstName first name of user
     * @param theLastName  last name of user
     * @param theUsername  username of user
     * @param theEmail     email of user
     * @precondition All the fields passed in must match the info in the database
     */
    public static void setAllFields(int theMemberId, String theFirstName, String theLastName,
                                    String theUsername, String theEmail) {


        memberId = theMemberId;
        firstName = theFirstName;
        lastName = theLastName;
        username = theUsername;
        email = theEmail;

    }

    // getters for all the fields
    public static int getMemberId() {
        return memberId;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

}
