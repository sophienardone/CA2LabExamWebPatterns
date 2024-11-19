package webpatterns.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping
    public String home(){ return "index";}
    @GetMapping("/messages_index")
    public String messageIndex(){ return "messages_index;"}
    @GetMapping("/user_index")
    public String userIndex(){return "user_index";}
    @GetMapping("friends_index")
    public String friendsIndex(){return "friends_index";}
    @GetMapping("blogentries_index")
    public String blogentriesIndex(){return "blogentriesIndex";}


}
