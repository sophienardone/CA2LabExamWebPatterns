package webpatterns.persistence;



import webpatterns.model.Friendship;

import java.util.ArrayList;

/**
 *
 * @author Michelle
 */
public interface FriendshipDao
{
    // Adding a Friendship
    // This will return 0 if the friendship was not added to the database
    // and 1 if the friendship was added to the database
    public int addFriendship(String username1, String username2);
    
    // Removing a specific Friendship - if the friendship was removed successfully 
    // this will return true, else it will return false
    public boolean removeFriendship(String username1, String username2);
    
    // Removing all Friendships for a specific User - if any friendships are removed successfully 
    // this will return true, else it will return false
    public boolean removeUserFriends(String username1);
    
    // Finding all Friendships for a specific user
    // This will return a list of Friendships for the specific User
    public ArrayList<Friendship> findFriendshipsByUsername(String username);
    
    // Confirming a Friendship between two users.
    // This will return a Friendship object containing both users if a friendship exists
    // If no friendship exists between them, it will return null.
    public Friendship checkFriendshipStatus(String username1, String username2);
}
