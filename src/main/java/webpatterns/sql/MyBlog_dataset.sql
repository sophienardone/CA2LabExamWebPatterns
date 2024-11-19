/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  michelle
 * Created: 7 Dec 2022
 */

use myBlog;
-- ****************************** USERS ********************************
INSERT INTO users(username, password, firstName, lastName) 
VALUES ("Charles", "password", "Charles", "Dickens"), 
("Steph", "password", "Stephenie", "Meyer"), 
("Rick", "password", "Rick", "Riordan"),
("Heidi", "password", "Johanna", "Spyri");

INSERT INTO users VALUES ("Michelle", "password", "Michelle", "Graham", TRUE);
-- ****************************** blog_entries ********************************
INSERT INTO blog_entries (username, title, content)
VALUES ("Michelle", "My Test Entry", "This is the first entry I have written in my blog!"), 
("Michelle", "Second Entry", "I'm not quite sure why I set up this blog"), 
("Michelle", "Retirement entry", "I think I'll give up my blog."), 
("Rick", "Schedule of upcoming releases", "To be added"), 
("Heidi", "Hello world", "Hello world");

-- ****************************** Friendships ********************************
INSERT INTO friends 
VALUES ("Rick", "Michelle"), 
("Charles", "Rick"), 
("Heidi", "Steph");

-- ****************************** Messages ********************************
insert into messages (sender, recipient, subject, body, dateSent) values
    ("Charles", "Rick", "Hey!", "Wanna hang out later?", '2018-08-28 13:12:44'),
    ("Rick", "Charles", "Re: Hey!", "Nah, I'm busy tonight. Tomorrow?", '2018-08-28 16:27:41'),
    ("Heidi", "Steph", "Lost book", "Do you have my copy of JC for class?", '2018-09-24 13:12:00'),
    ("Rick", "Michelle", "Boooooooored", "This is so boring!!", '2018-06-14 18:03:41'),
    ("Michelle", "Rick", "Cinema?", "Avengers tonight?", '2018-07-28 11:07:44');

insert into messages (sender, recipient, subject, body, deletedForSender, deletedForRecipient, dateSent) values
    ("Charles", "Rick", "Did you see that?!", "Jeez, wouldn't want to be in their shoes...", FALSE, TRUE, '2018-05-15 10:12:44'),
    ("Steph", "Heidi", "Details for tomorrow", "See you outside the shop at 7:30?", TRUE, FALSE, '2018-08-11 17:07:41');