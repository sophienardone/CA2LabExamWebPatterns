package webpatterns.persistence;

import model.User;

import java.sql.*;
import java.time.LocalDateTime;

public class UserDaoImpl extends MySQLDao implements UserDao {
    public UserDaoImpl(String propertiesFile) {
        super(propertiesFile);
    }

    public UserDaoImpl(Connection conn){
        super(conn);
    }

    private User mapRow(ResultSet rs) throws SQLException{
        String firstname = rs.getString("firstName");
        String lastname = rs.getString("lastName");
        String username = rs.getString("username");
        String password = rs.getString("password");
        boolean isAdmin = rs.getBoolean("isAdmin");
        return new User(username, password, firstname, lastname, isAdmin);
    }

    /**
     * Find a specific <code>User</code> in the database matching a supplied
     * username and password.
     *
     * @param uname The username of the <code>User</code> being searched for
     * @param pword The password of the <code>User</code> being searched for
     *
     * @return The <code>User</code> object matching the supplied information.
     *         If no match is found for the supplied information, then the
     *         object will be null.
     */
    @Override
    public User findUserByUsernamePassword(String uname, String pword) {
        Connection con = this.getConnection();
        User u = null;
        String query = "SELECT * FROM users WHERE USERNAME = ? AND PASSWORD = ?";
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, uname);
            ps.setString(2, pword);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = mapRow(rs);
                }
            }
        } catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in findUserByUsernamePassword().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return u;     // u may be null 
    }

    /**
     * Find the first <code>User</code> matching a specified username. If more
     * than one <code>User</code> matching the username is found, only the first
     * match will be returned.
     *
     * @param uname The username of the <code>User</code> being searched for.
     *
     * @return A <code>User</code> matching the specified username, otherwise
     *         null.
     */
    @Override
    public User findUserByUsername(String uname) {
        Connection con = this.getConnection();
        User u = null;

        String query = "SELECT * FROM users WHERE USERNAME = ?";
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, uname);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = mapRow(rs);
                }
            }
        } catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in findUserByUsername().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return u;     // u may be null 
    }

    /**
     * Check if the <code>User</code> matching a specified username has admin
     * status.
     *
     * @param uname The username of the <code>User</code> being searched for.
     *
     * @return True if the <code>User</code> matching the specified username is
     *         marked as an admin, otherwise false.
     */
    @Override
    public boolean checkIfUserIsAdmin(String uname) {
        Connection con = this.getConnection();
        boolean isAdmin = false;

        String query = "SELECT * FROM users WHERE USERNAME = ? AND isAdmin = TRUE";
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, uname);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    isAdmin = true;
                }
            }
        }  catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in checkIfUserIsAdmin().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return isAdmin;     // true if the user is an admin, false otherwise 
    }

    /**
     * Add a new <code>User</code> to the database.
     *
     * @param u The <code>User</code> to be added to the database.
     *
     * @return True if the <code>User</code> was successfully added to the
     *         database, false otherwise.
     */
    @Override
    public boolean addUser(User u) {
        if (findUserByUsername(u.getUsername()) != null) {
            return false;
        }

        Connection con = this.getConnection();

        String query = "INSERT INTO users(username, password, firstName, lastName, isAdmin) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFirstName());
            ps.setString(4, u.getLastName());
            ps.setBoolean(5, u.isAdmin());

            ps.execute();
        } catch(SQLIntegrityConstraintViolationException e){
            System.err.println(LocalDateTime.now() + ": An integrity constraint failed in addUser().");
            System.out.println("Error: " + e.getMessage());
        }catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in addUser().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return true;
    }

    /**
     * Remove a <code>User</code> from the database.
     *
     * @param u The <code>User</code> to be removed from the database.
     *
     * @return True if the <code>User</code> could be removed, false otherwise.
     */
    @Override
    public boolean removeUser(User u) {
        Connection con = this.getConnection();

        String query = "DELETE FROM users WHERE username = ?";
        boolean removed = false;
        try(PreparedStatement ps = con.prepareStatement(query)){
            ps.setString(1, u.getUsername());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 0) {
                removed = true;
            }
        } catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in removeUser().");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return removed;
    }

    // Sample code showing these methods in use.
    public static void main(String[] args) {
        UserDaoImpl uDAO = new UserDaoImpl("database.properties");
        try {
            // Try to find an existing user
            User u = uDAO.findUserByUsernamePassword("Steph", "password");
            System.out.println(u);

            // Try to find a user that doesn't exist
            u = uDAO.findUserByUsernamePassword("zzzzzzzzz", "password");
            System.out.println(u);

            u = new User("alexis", "password", "Michelle", "Addison", false);
            uDAO.addUser(u);

            u = uDAO.findUserByUsername("alexis");
            System.out.println(u);

            u = uDAO.findUserByUsername("Charles");
            System.out.println(u);

            // Check if a User is an admin
            boolean isAdmin = uDAO.checkIfUserIsAdmin("Michelle");
            System.out.println("Michelle is an admin: " + isAdmin);
            isAdmin = uDAO.checkIfUserIsAdmin("Charles");
            System.out.println("Charles is an admin: " + isAdmin);

            // Try to remove a user that doesn't exist
            User noUser = new User("Hannah", "pass", "Hannah", "Abbott", false);
            boolean removed = uDAO.removeUser(noUser);
            if (removed) {
                System.out.println("User " + noUser.getUsername() + " was removed successfully.");
            } else {
                System.out.println("User " + noUser.getUsername() + " could not be removed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
