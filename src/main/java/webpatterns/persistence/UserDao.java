package webpatterns.persistence;


import webpatterns.model.User;

/**
 *
 * @author Michelle
 */
public interface UserDao
{
    // Find User based on username and password
    public User findUserByUsernamePassword(String uname, String pword);
    
    // Find first user with that username
    public User findUserByUsername(String uname);
    
    // Check if a specific User has admin status
    // This will return true if this user has admin status,
    // and false otherwise
    public boolean checkIfUserIsAdmin(String uname);
    
    // Add a user to the database
    // This will return true if the user was added to the database
    // and false if the user couldn't be added.
    public boolean addUser(User u);
    
    // Remove user
    // This will return true if the user could be removed from the database
    // and false if the user couldn't be removed.
    public boolean removeUser(User u);

    public User login(String username, String password);
}
