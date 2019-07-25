package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMessages(Model model){
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }
    @GetMapping("/add")
    public String newMessage(Model model){
        model.addAttribute("message", new Message());
        return "form";
    }
    @PostMapping("/add")
    public String processMessage(@ModelAttribute Message message,
                                 @RequestParam("file")MultipartFile file){
        if(file.isEmpty()){
            return "redirect:/add";
        }
        try{
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setHeadshot(uploadResult.get("url").toString());
            messageRepository.save(message);
        } catch (IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";
    }
}
