package webpatterns.persistence;



import webpatterns.model.BlogEntry;

import java.util.ArrayList;

/**
 *
 * @author Michelle
 */
public interface BlogEntryDao
{
    // Add a BlogEntry to the database
    // This will give back the id the new entry is inserted with if it's successful
    // If it's not successful, it will return -1.
    public int addBlogEntry(String username, String title, String content);
    
    // Remove BlogEntry from the database
    // Returns 0 if the delete was unsuccessful and 1 if it was successful.
    public int removeBlogEntry(int id);
    
    // Find BlogEntry based on author
    // Returns an arraylist of all entries written by that author
    public ArrayList<BlogEntry> findBlogEntriesByAuthor(String author);
    
    // Find BlogEntry based on entryID
    // Returns the entry with that id
    public BlogEntry findBlogEntryByID(int id);
    
    // Find first BlogEntry with this title
    // Returns the FIRST entry with this title in the database
    // If there are multiple matching entries, anything after the first title is ignored.
    public BlogEntry findBlogEntryByTitle(String searchTitle);
    
    // Find all BlogEntries
    // Returns an arraylist of all the entries in the database
    public ArrayList<BlogEntry> findAllBlogEntries();
    
}
