package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    /*This is to upload the image file*/
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
        System.out.println("am in get-add");
        return "form";
    }
    @PostMapping("/add")
    public String processMessage(@ModelAttribute Message message,
                                 @RequestParam("file")MultipartFile file){
        System.out.println("am in post-add");
        if(file.isEmpty()){
            System.out.println("am in add-if");
            return "redirect:/add";
        }
        try{

            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setHeadshot(uploadResult.get("url").toString());
            messageRepository.save(message);
            System.out.println("am in add-try");
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("am in add-catch");
            return "redirect:/add";
        }

        return "redirect:/";
    }

    @PostMapping("/process")
    public String processForm(@Valid Message message, BindingResult result){
        if(result.hasErrors()){
            System.out.println("am in process");
            return "form";
        }
        messageRepository.save(message);
        System.out.println("am in process-else");
        return "redirect:/";
    }


    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        System.out.println("am in update-id");

        return "form";
    }
    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }


}
