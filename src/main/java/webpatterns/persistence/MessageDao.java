package webpatterns.persistence;



import webpatterns.model.Message;
import webpatterns.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michelle
 */
public interface MessageDao {
    
    // Get a specific Message from the database
    // This will return a Message object matching the specified id if a match is 
    // found in the database, otherwise it will return null
    public Message getMessageById(int messageID);
    
    // Getting the list of all (undeleted) messages sent by a specific user
    // This will return an Arraylist of Message objects sent by the specified user
    public ArrayList<Message> getSentMessagesForUser(String senderName);
    
    // Getting the list of all (undeleted) messages received by a specific user
    // This will return an Arraylist of Message objects received by the specified user
    public ArrayList<Message> getReceivedMessagesForUser(String recipientName);
    
    // Sending a new message
    // This will return:
    //                  1 if the message was successfully sent
    //                 -1 if the sender and recipient are not friends and the message cannot be sent
    //                 -2 if the sender or recipient do not exist on the system and the message cannot be sent
    //                  0 if the message cannot be sent for any other reason
    public int sendMessage(String sender, String recipient, String subject, String body);
    
    // Marking a message as read
    // This will return true if the message could be marked as read, false otherwise
    public boolean markMessageAsRead(int messageID, String recipient);
    
    // Marking a sent message as deleted
    // This will set the corresponding message sent by a specific user to "deleted"
    // and return true
    // If there is no message with that id or if the specified message was not 
    // sent by the supplied user, it will return false
    public boolean deleteMessageForSender(int messageID, String sender);
    
    // Marking a received message as deleted
    // This will set the corresponding message received by a specific user to "deleted"
    // and return true
    // If there is no message with that id or if the specified message was not 
    // received by the supplied user, it will return false
    public boolean deleteMessageForRecipient(int messageID, String recipient);

//    public ArrayList<Message> searchAllMessages(String username);

    public List<Message> getAllMessages();

//    public Message viewMessageDetails(String message);
}
