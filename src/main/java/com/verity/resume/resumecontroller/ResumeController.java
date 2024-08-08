package com.verity.resume.resumecontroller;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ResumeController {

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("fileUpload") MultipartFile file, Model model) {
        try {
            // Parse the resume file
            String parsedText = parseResume(file);
            // Analyze the parsed text to extract required data
            String name = extractName(parsedText);
            String email = extractEmail(parsedText);

            System.out.println(name + " , " + email);
            
            // Add extracted data to the model
            model.addAttribute("name", name);
            model.addAttribute("email", email);

            // Return the same view with results
            return "index";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while processing the file.");
            return "index";
        }
    }

    private String parseResume(MultipartFile file) throws Exception {
        Tika tika = new Tika();
        try (InputStream stream = file.getInputStream()) {
            return tika.parseToString(stream);
        }
    }

    private String extractName(String text) {
        // Improved regex to capture names with middle names or initials
        String nameRegex = "\\b[A-Z][a-z]+(?: [A-Z][a-z]+){1,2}\\b";
        Pattern pattern = Pattern.compile(nameRegex);
        Matcher matcher = pattern.matcher(text);
    
        // Return the first match found
        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "Name not found";
    }

    private String extractEmail(String text) {
        String emailRegex = "([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Email not found";
    }

}
