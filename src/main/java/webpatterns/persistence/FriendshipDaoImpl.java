package webpatterns.persistence;

import model.Friendship;
import model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Michelle
 */
public class FriendshipDaoImpl extends MySQLDao implements FriendshipDao {
    public FriendshipDaoImpl(String propertiesFile) {
        super(propertiesFile);
    }

    public FriendshipDaoImpl(Connection conn){
        super(conn);
    }

    /**
     * Add a new <code>Friendship</code> to the database
     *
     * @param username1 User1 in the <code>Friendship</code> (order is
     *                  irrelevant)
     * @param username2 User2 in the <code>Friendship</code> (order is
     *                  irrelevant)
     *
     * @return 0 if the <code>Friendship</code> was not added to the database, 1
     *         if the add was successful
     */
    @Override
    public int addFriendship(String username1, String username2) {
        Connection con = this.getConnection();
        int rowsAffected = 0;
        String query = "INSERT INTO friends(friend1, friend2) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(query)){
            ps.setString(1, username1);
            ps.setString(2, username2);

            rowsAffected = ps.executeUpdate();
        } catch(SQLIntegrityConstraintViolationException e){
            System.err.println(LocalDateTime.now() + ": An integrity constraint failed in addFriendship().");
            System.out.println("Error: " + e.getMessage());
        }catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in addFriendship().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return rowsAffected;
    }

    /**
     * Remove a <code>Friendship</code> from the database
     *
     * @param username1 User1 in the <code>Friendship</code> (order is
     *                  irrelevant)
     * @param username2 User2 in the <code>Friendship</code> (order is
     *                  irrelevant)
     *
     * @return true if the <code>Friendship</code> was removed successfully,
     *         false otherwise
     */
    @Override
    public boolean removeFriendship(String username1, String username2) {
        Connection con = this.getConnection();
        String query = "DELETE FROM friends WHERE (friend1 = ? AND friend2 = ?) OR (friend1 = ? AND friend2 = ?)";

        boolean removed = false;

        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username1);
            ps.setString(2, username2);
            ps.setString(3, username2);
            ps.setString(4, username1);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 0) {
                removed = true;
            }
        } catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in removeFriendship().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return removed;
    }

    /**
     * Remove all <code>Friendships</code> for a specific user
     *
     * @param username1 The name of the user to remove all
     *                  <code>Friendships</code> for
     *
     * @return True if at least one <code>Friendship</code> was successfully
     *         removed from the database, false otherwise
     */
    @Override
    public boolean removeUserFriends(String username1) {
        Connection con = this.getConnection();
        String query = "DELETE FROM friends WHERE friend1 = ? OR friend2 = ? ";

        boolean removed = false;
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username1);
            ps.setString(2, username1);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 0) {
                removed = true;
            }
        } catch(SQLException e){
            System.out.println(LocalDateTime.now() + ": An SQLException occurred in removeUserFriends().");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        this.freeConnection(con);
        return removed;
    }

    /**
     * Retrieve all <code>Friendships</code> in the database for a specific
     * <code>User</code>
     *
     * @param username The name of the user whose <code>Friendships</code> are
     *                 being retrieved
     *
     * @return An <code>ArrayList</code> of <code>Friendship</code> objects
     *         attached to the supplied username. This will be empty if there
     *         were no <code>Friendships</code> found for the supplied username.
     */
    @Override
    public ArrayList<Friendship> findFriendshipsByUsername(String username) {
        Connection con = this.getConnection();
        ArrayList<Friendship> friends = new ArrayList<>();
        String query = "SELECT * FROM friends WHERE friend1 = ? OR friend2 = ?";

        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, username);

            try(ResultSet rs = ps.executeQuery()) {
                // Get the member's details
                UserDaoImpl userDao = new UserDaoImpl(getPropertiesFile());
                User user = userDao.findUserByUsername(username);

                while (rs.next()) {
                    // Get the username of the friend
                    // Need to make sure we're not looking at this user's username
                    String friend = rs.getString("friend1");
                    if (friend.equals(username)) {
                        friend = rs.getString("friend2");
                    }
                    // Get the details for the friend of this user
                    User userFriend = userDao.findUserByUsername(friend);

                    // Make a friendship & add it to the list.
                    Friendship f = new Friendship(user, userFriend);
                    friends.add(f);
                }
            }
        }  catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in findFriendshipsByUsername().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return friends;
    }

    /**
     * Check for the existance of a <code>Friendship</code> between two users
     *
     * @param username1 User1 in the <code>Friendship</code> (order is
     *                  irrelevant)
     * @param username2 User2 in the <code>Friendship</code> (order is
     *                  irrelevant)
     *
     * @return The <code>Friendship</code> object for that pairing if a
     *         <code>Friendship</code> match exists in the database, otherwise
     *         null.
     */
    @Override
    public Friendship checkFriendshipStatus(String username1, String username2) {
        Connection con = this.getConnection();

        String query = "SELECT * FROM friends WHERE (friend1 = ? AND friend2 = ?) OR (friend1 = ? AND friend2 = ?)";
        Friendship friends = null;
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username1);
            ps.setString(2, username2);
            ps.setString(3, username2);
            ps.setString(4, username1);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String uname1 = rs.getString("friend1");
                    String uname2 = rs.getString("friend2");
                    // Get the details of each User in the Friendship based on the usernames
                    UserDaoImpl userDao = new UserDaoImpl(getPropertiesFile());
                    User friend1 = userDao.findUserByUsername(uname1);
                    User friend2 = userDao.findUserByUsername(uname2);

                    friends = new Friendship(friend1, friend2);
                }
            }
        } catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in checkFriendshipStatus().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return friends;     // friends may be null 
    }

    public static void main(String[] args) {
        FriendshipDaoImpl friendsDao = new FriendshipDaoImpl("database.properties");
        try {
            /*
            *   DEMONSTRATING SEARCH METHODS
                1) Search for a specific user's friendships
                2) Search for a specific friendship based on the usernames of the Users involved.
             */

            // 1) Searching for a specific User's friendships
            System.out.println("\n++++++++++++++Demonstrating searching for a specific user's friends++++++++++++++");
            User user1 = new User("Jedwards", "password", "James", "Edwards", false);
            ArrayList<Friendship> friendsList = friendsDao.findFriendshipsByUsername(user1.getUsername());

            // Display results
            if (!friendsList.isEmpty()) {
                System.out.println("Success! The following friends were found for that user:");
                System.out.println("==================================================");
                int i = 1;
                for (Friendship friendship : friendsList) {
                    // Make sure we print out the friends of the user, not the user itself
                    User f = friendship.getUser1();
                    if (f.equals(user1)) {
                        f = friendship.getUser2();
                    }

                    System.out.println("Friendship #" + i + ":");
                    System.out.println("\tUsername:\t\t" + f.getUsername());
                    System.out.println("==================================================");
                    i++;
                }
            } else {
                System.out.println("There were no friends found for that User. (" + user1.getUsername() + ").");
            }

            System.out.println("\nTrying again with a different user.");
            User user2 = new User("Rick", "password", "Rick", "Riordan", false);
            friendsList = friendsDao.findFriendshipsByUsername(user2.getUsername());

            // Display results
            if (!friendsList.isEmpty()) {
                System.out.println("Success! The following friends were found for that user:");
                System.out.println("==================================================");
                int i = 1;
                for (Friendship friendship : friendsList) {
                    // Make sure we print out the friends of the user, not the user itself
                    User f = friendship.getUser1();
                    if (f.equals(user2)) {
                        f = friendship.getUser2();
                    }

                    System.out.println("Friendship #" + i + ":");
                    System.out.println("\tUsername:\t\t" + f.getUsername());
                    System.out.println("==================================================");
                    i++;
                }
            } else {
                System.out.println("There were no friends found for that User. (" + user2.getUsername() + ").");
            }

            // 2) Search for a specific friendship based on the usernames of the Users involved.
            System.out.println("\n++++++++++++++Demonstrating searching for a specific friendship++++++++++++++");
            System.out.println("\nChecking database to see if " + user2.getUsername() + " is friends with " + user1.getUsername() + "...");
            Friendship pairOfFriends = friendsDao.checkFriendshipStatus(user2.getUsername(), user1.getUsername());
            if (pairOfFriends == null) {
                System.out.println(user2.getUsername() + " is NOT friends with " + user1.getUsername() + ".");
            } else {
                System.out.println(user2.getUsername() + " IS friends with " + user1.getUsername() + ".");
            }

            System.out.println("\nTrying again with a different user...");

            User user3 = new User("Charles", "password", "Charles", "Dickens", false);
            pairOfFriends = friendsDao.checkFriendshipStatus(user2.getUsername(), user3.getUsername());
            if (pairOfFriends == null) {
                System.out.println(user2.getUsername() + " is NOT friends with " + user3.getUsername() + ".");
            } else {
                System.out.println(user2.getUsername() + " IS friends with " + user3.getUsername() + ".");
            }

            /*
            *
                Demonstrating ADDING a friendship to the database
            *
             */
            System.out.println("\n++++++++++++++Demonstrating adding a friendship++++++++++++++");
            // First add Jedwards to the database
            UserDaoImpl userDao = new UserDaoImpl("database.properties");
            boolean verdict = userDao.addUser(user1);
            // Check if the add was successful
            if (verdict) {
                // If the user was added to the database, try to add a friendship
                int result = friendsDao.addFriendship(user1.getUsername(), user2.getUsername());
                if (result != 0) {
                    System.out.println("The friendship was successfully added to the database - " + user1.getUsername() + " and " + user2.getUsername() + " are now friends");
                    System.out.println("\tDouble-checking by checking the database for a friendship between these users");
                    pairOfFriends = friendsDao.checkFriendshipStatus(user2.getUsername(), user1.getUsername());
                    if (pairOfFriends == null) {
                        System.out.println("\t" + user2.getUsername() + " is NOT friends with " + user1.getUsername() + ".");
                    } else {
                        System.out.println("\t" + user2.getUsername() + " IS friends with " + user1.getUsername() + ".");
                    }
                } else {
                    System.out.println("The friendship was not added to the database");
                }
            } else {
                System.out.println("User already in the database.");
            }
            System.out.println();

            System.out.println("\n++++++++++++++Demonstrating adding a friendships for a user when the friend doesn't exist++++++++++++++");
            System.out.println("Trying to make a friendship between " + user1.getUsername() + " and \"Technician\".");
            System.out.println("An exception will occur (but be handled within the addFriendship method.)");
            int result = friendsDao.addFriendship(user1.getUsername(), "Technician");
            if (result != 0) {
                System.out.println("The friendship was successfully added to the database - " + user1.getUsername() + " and " + user2.getUsername() + " are now friends");
                System.out.println("\tDouble-checking by checking the database for a friendship between these users");
                pairOfFriends = friendsDao.checkFriendshipStatus("Technician", user1.getUsername());
                if (pairOfFriends == null) {
                    System.out.println("\tTechnician is NOT friends with " + user1.getUsername() + ".");
                } else {
                    System.out.println("\tTechnician IS friends with " + user1.getUsername() + ".");
                }
            } else {
                System.out.println("The friendship was not added to the database");
            }

            /*
            *
                Demonstrating REMOVING a friendship between two users from the database
            *
             */
            System.out.println("\n++++++++++++++Demonstrating removing a friendship between two users++++++++++++++");

            verdict = friendsDao.removeFriendship(user1.getUsername(), user2.getUsername());
            if (verdict) {
                System.out.println("Friendship between " + user1.getUsername() + " and " + user2.getUsername() + " was successfully removed");
                System.out.println("\tDouble-checking by checking the database for a friendship between these users");
                pairOfFriends = friendsDao.checkFriendshipStatus(user1.getUsername(), user2.getUsername());
                if (pairOfFriends == null) {
                    System.out.println("\t" + user2.getUsername() + " is NOT friends with " + user1.getUsername() + ".");
                } else {
                    System.out.println("\t" + user2.getUsername() + " IS friends with " + user1.getUsername() + ".");
                }
            } else {
                System.out.println("Friendship between " + user1.getUsername() + " and " + user2.getUsername() + " was not removed.");
            }

            /*
            *
                Demonstrating REMOVING all friendship for a specific user
            *
             */
            System.out.println("\n++++++++++++++Demonstrating removing all friendships for a user++++++++++++++");
            verdict = friendsDao.removeUserFriends(user2.getUsername());

            if (verdict) {
                System.out.println("All friendships for " + user2.getUsername() + " were successfully removed");
                System.out.println("\tDouble-checking by checking the database for any friendship involving this user");
                friendsList = friendsDao.findFriendshipsByUsername(user2.getUsername());

                // Display results
                if (!friendsList.isEmpty()) {
                    System.out.println("Success! The following friends were found for that user:");
                    System.out.println("==================================================");
                    int i = 1;
                    for (Friendship friendship : friendsList) {
                        // Make sure we print out the friends of the user, not the user itself
                        User f = friendship.getUser1();
                        if (f.equals(user2)) {
                            f = friendship.getUser2();
                        }

                        System.out.println("Friendship #" + i + ":");
                        System.out.println("\tUsername:\t\t" + f.getUsername());
                        System.out.println("==================================================");
                        i++;
                    }
                } else {
                    System.out.println("\tThere were no friends found for that User. (" + user2.getUsername() + ").");
                }
            } else {
                System.out.println("No Friendships removed for " + user2.getUsername() + ".");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
