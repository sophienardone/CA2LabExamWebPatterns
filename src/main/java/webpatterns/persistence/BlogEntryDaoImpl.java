package webpatterns.persistence;


import model.BlogEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Michelle
 */
public class BlogEntryDaoImpl extends MySQLDao implements BlogEntryDao {
    public BlogEntryDaoImpl(String propertiesFile) {
        super(propertiesFile);
    }

    public BlogEntryDaoImpl(Connection conn){
        super(conn);
    }


    private static BlogEntry mapRow(ResultSet rs) throws SQLException {
        int entryID = rs.getInt("entryID");
        String username = rs.getString("username");
        String title = rs.getString("title");
        String content = rs.getString("content");
        return new BlogEntry(entryID, username, title, content);
    }

    /**
     * Add a new BlogEntry to the database.
     *
     * @param username Username of the <code>User</code> creating the new entry.
     * @param title    Title of the <code>BlogEntry</code> being added to the
     *                 database.
     * @param content  Content/text included in the <code>BlogEntry</code> being
     *                 added to the database.
     *
     * @return The id of the new <code>BlogEntry</code> in the database if it
     *         could be added successfully. Otherwise it will return -1.
     */
    @Override
    public int addBlogEntry(String username, String title, String content) {
        int newId = -1;
        Connection con = this.getConnection();

        String query = "INSERT INTO blog_entries(username, title, content) VALUES (?, ?, ?)";
        // Need to get the id back, so have to tell the database to return the id it generates
        try(PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, username);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.executeUpdate();

            // Find out what the id generated for this entry was
            try(ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newId = generatedKeys.getInt(1);
                }
            }catch (SQLException e){
                System.err.println(LocalDateTime.now() + ": An SQLException occurred while retrieving the generated " +
                        "primary key information in addBlogEntry()." +
                        ".");
                System.out.println("Error: " + e.getMessage());
            }
        }catch(SQLIntegrityConstraintViolationException e){
            System.err.println(LocalDateTime.now() + ": An integrity constraint failed while adding a BlogEntry." +
                    ".");
            System.out.println("Error: " + e.getMessage());
        }catch(SQLException e){
            System.err.println(LocalDateTime.now() + ": An SQLException occurred while adding a BlogEntry." +
                    ".");
            System.out.println("Error: " + e.getMessage());
        }

        this.freeConnection(con);
        return newId;
    }

    /**
     * Remove a <code>BlogEntry</code> from the database.
     *
     * @param id The id of the <code>BlogEntry</code> to be removed.
     *
     * @return 1 if the delete was successful, 0 otherwise.
     */
    @Override
    public int removeBlogEntry(int id) {
        Connection con = this.getConnection();
        int rowsAffected = 0;

        String query = "DELETE FROM blog_entries WHERE entryID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, id);
            rowsAffected = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + ": An SQLException occurred while removing a BlogEntry." +
                    ".");
            System.out.println("Error: " + e.getMessage());
        }

        this.freeConnection(con);
        return rowsAffected;
    }

    /**
     * Find all <code>BlogEntry</code> information written by a specific author
     *
     * @param author The username of the <code>User</code> who wrote the
     *               <code>BlogEntries</code>
     *
     * @return An <code>ArrayList</code> of all <code>BlogEntries</code>
     *         associated with the given username. If there were no
     *         <code>BlogEntries</code> found for that username, this list will
     *         be empty.
     */
    @Override
    public ArrayList<BlogEntry> findBlogEntriesByAuthor(String author) {
        Connection con = this.getConnection();
        ArrayList<BlogEntry> entries = new ArrayList<>();

        String query = "SELECT * FROM blog_entries WHERE username = ?";
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, author);

            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    BlogEntry b = mapRow(rs);
                    entries.add(b);
                }
            } catch (SQLException e) {
                System.err.println(LocalDateTime.now() + ": An SQLException occurred while running the query" +
                        " or processing the result in findBlogEntriesByAuthor().");
                System.out.println("Error: " + e.getMessage());
            }
        }  catch (SQLException e) {
            System.err.println(LocalDateTime.now() + ": An SQLException occurred while getting blog entries for a " +
                    "specific user" +
                    ".");
            System.out.println("Error: " + e.getMessage());
        }

        this.freeConnection(con);
        return entries;     // entries may be empty 
    }


    /**
     * Find a specific <code>BlogEntry</code> based on its entryId information
     *
     * @param id The entryId of the <code>BlogEntry</code> being searched for.
     *
     * @return The <code>BlogEntry</code> matching the supplied entryId,
     *         otherwise null.
     */
    @Override
    public BlogEntry findBlogEntryByID(int id) {
        Connection con = this.getConnection();
        String query = "SELECT * FROM blog_entries WHERE entryID = ?";

        BlogEntry b = null;
        try (PreparedStatement ps = con.prepareStatement(query)){
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    b = mapRow(rs);
                }
            }catch (SQLException e) {
                System.err.println(LocalDateTime.now() + ": An SQLException occurred while running the query" +
                        " or processing the result in findBlogEntryByID().");
                System.out.println("Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + ": An SQLException occurred while attempting to find a blog " +
                    "entry by id." +
                    ".");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return b;     // b may be null 
    }

    /**
     * Find the first <code>BlogEntry</code> matching a specified title.
     *
     * @param searchTitle The title to search for.
     *
     * @return The <code>BlogEntry</code> matching the specified title. If more
     *         than one <code>BlogEntry</code> match is found, the first
     *         <code>BlogEntry</code> is returned.
     */
    @Override
    public BlogEntry findBlogEntryByTitle(String searchTitle) {
        Connection con = this.getConnection();
        String query = "SELECT * FROM blog_entries WHERE title = ?";

        BlogEntry b = null;
        try(PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, searchTitle);

            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    b = mapRow(rs);
                }
            }catch (SQLException e) {
                System.err.println(LocalDateTime.now() + ": An SQLException occurred while running the query" +
                        " or processing the result in findBlogEntryByTitle()");
                System.out.println("Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in findBlogEntryByTitle()");
            System.out.println("Error: " + e.getMessage());
        }
        this.freeConnection(con);
        return b;     // b may be null 
    }

    /**
     * Retrieve all <code>BlogEntries</code> in the database
     *
     * @return An <code>ArrayList</code> of all <code>BlogEntry</code>
     *         information currently in the database.
     */
    @Override
    public ArrayList<BlogEntry> findAllBlogEntries() {
        Connection con = this.getConnection();
        ArrayList<BlogEntry> entries = new ArrayList<>();

        String query = "SELECT * FROM blog_entries ORDER BY entryID DESC";
        try (PreparedStatement ps = con.prepareStatement(query)){
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BlogEntry b = mapRow(rs);
                    entries.add(b);
                }
            }catch (SQLException e) {
                System.err.println(LocalDateTime.now() + ": An SQLException occurred while running the query" +
                        " or processing the result in findAllBlogEntries()");
                System.out.println("Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println(LocalDateTime.now() + ": An SQLException occurred in findAllBlogEntries()");
            System.out.println("Error: " + e.getMessage());
        }

        this.freeConnection(con);
        return entries;     // may be empty
    }

    // Sample code showing these methods in use.
    public static void main(String[] args) {
        BlogEntryDaoImpl blogDAO = new BlogEntryDaoImpl("database.properties");
        try {
            /*
            *   DEMONSTRATING SEARCH METHODS
                1) Searching by title
                2) Searching by author
                3) Searching by id
                4) Getting all entries in the database
             */
            // 1) Searching by title
            System.out.println("Checking database for blog entry titled \"My Test Entry\"...");
            BlogEntry b = blogDAO.findBlogEntryByTitle("My Test Entry");

            // Display results
            if (b != null) {
                System.out.println("Details found for \"My Test Entry\": ");
                System.out.println("ID: \t\t" + b.getEntryId());
                System.out.println("Author:\t\t" + b.getUsername());
                System.out.println("Title: \t\t" + b.getTitle());
                System.out.println("Content: \t" + b.getContent());
            } else {
                System.out.println("Cannot find a blog with that title");
            }

            System.out.println();
            System.out.println("Checking database for blog entry titled \"My Tester Entry\"...");
            b = blogDAO.findBlogEntryByTitle("My Tester Entry");
            // Display results
            if (b != null) {
                System.out.println("Details found for \"My Test Entry\": ");
                System.out.println("ID: \t\t" + b.getEntryId());
                System.out.println("Author:\t\t" + b.getUsername());
                System.out.println("Title: \t\t" + b.getTitle());
                System.out.println("Content: \t" + b.getContent());
            } else {
                System.out.println("Cannot find a blog with that title");
            }

            // 2) Searching by author
            System.out.println("\nChecking the blog entries by user with username \"Michelle\"...");
            ArrayList<BlogEntry> entries = blogDAO.findBlogEntriesByAuthor("Michelle");

            // Display results
            if (entries.isEmpty()) {
                System.out.println("Success! The following entries were found for that username:");
                System.out.println("==================================================");
                int i = 1;
                for (BlogEntry entry : entries) {
                    System.out.println("Entry #" + i + ":");
                    System.out.println("\tID: \t\t" + entry.getEntryId());
                    System.out.println("\tAuthor:\t\t" + entry.getUsername());
                    System.out.println("\tTitle: \t\t" + entry.getTitle());
                    System.out.println("\tContent: \t" + entry.getContent());
                    System.out.println("==================================================");
                    i++;
                }
            } else {
                System.out.println("There were no entries found by that username.");
            }

            // 3) Searching by id
            System.out.println("Checking database for blog entry with id = 1.");
            b = blogDAO.findBlogEntryByID(1);

            // Display results
            if (b != null) {
                System.out.println("Details for entry: ");
                System.out.println("ID: \t\t" + b.getEntryId());
                System.out.println("Author:\t\t" + b.getUsername());
                System.out.println("Title: \t\t" + b.getTitle());
                System.out.println("Content: \t" + b.getContent());
            } else {
                System.out.println("Cannot find a blog with that id");
            }

            // 4) Getting all entries in the database
            System.out.println("\nGetting all of the blog entries in the database...");
            entries = blogDAO.findAllBlogEntries();

            // Display results
            if (entries.isEmpty()) {
                System.out.println("Success! The following entries were found:");
                System.out.println("==================================================");
                int i = 1;
                for (BlogEntry entry : entries) {
                    System.out.println("Entry #" + i + ":");
                    System.out.println("\tID: \t\t" + entry.getEntryId());
                    System.out.println("\tAuthor:\t\t" + entry.getUsername());
                    System.out.println("\tTitle: \t\t" + entry.getTitle());
                    System.out.println("\tContent: \t" + entry.getContent());
                    System.out.println("==================================================");
                    i++;
                }
            } else {
                System.out.println("There were no entries found in the database.");
            }

            /*
            *   DEMONSTRATING ADDING A BLOG_ENTRY
             */
            BlogEntry test = new BlogEntry("Charles", "Hey there", "Allo, Allo");
            System.out.println("Attempting to add a test entry to the database: " + test);

            int newEntryId = blogDAO.addBlogEntry("Charles", "Hey there", "Allo, Allo");
            if (newEntryId != -1) {
                System.out.println("The addition was successful, the new entry has been added to the database with an id of " + newEntryId);
            }

            /*
            *   DEMONSTRATING REMOVING A BLOG_ENTRY
             */
            System.out.println("\nAttempting to remove the test entry from the database: " + test);

            int verdict = blogDAO.removeBlogEntry(newEntryId);
            if (verdict != 0) {
                System.out.println("The removal was successful, the entry has been removed from the database");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
