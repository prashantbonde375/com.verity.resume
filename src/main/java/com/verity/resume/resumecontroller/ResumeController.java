package com.verity.resume.resumecontroller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ResumeController {

    private static final String NAME_REGEX = "\\b([A-Z][a-zA-Z]*[.']?\\s?)+([A-Z][a-zA-Z]*[-' ]?)+\\b";
    private static final String EMAIL_REGEX = "([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})";
    private static final String MOBILE_REGEX = "\\b(\\+?\\d{1,4}[\\s-]?\\(?\\d{1,4}\\)?[\\s-]?\\d{1,4}[\\s-]?\\d{1,9})\\b";
    private static final String ADDRESS_REGEX = "\\b\\d{1,5} [A-Za-z0-9 ]+(?: Apt \\w+)?(?:, [A-Za-z ]+)?(?:, [A-Z]{2} \\d{5})?(?:, [A-Za-z ]+)?\\b";
    private static final String DOB_REGEX = "\\b(?:\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{2,4}[/-]\\d{1,2}[/-]\\d{1,2}|\\d{4}[./-]\\d{2}[./-]\\d{2}|\\d{1,2} [A-Za-z]+ \\d{4})\\b";
    private static final String MARITAL_STATUS_REGEX = "\\b(Single|Married|Divorced|Widowed|Separated)\\b";
    private static final String LANGUAGES_REGEX = "\\b(English|Spanish|French|German|Chinese|Japanese|Russian|Hindi|Arabic|Portuguese|Italian|Korean|Dutch|Turkish|Swedish|Norwegian|Danish|Finnish|Polish|Czech|Hungarian|Greek|Hebrew|Thai|Vietnamese)\\b";
    private static final String NATIONALITY_REGEX = "\\b(American|British|Canadian|Australian|Indian|Chinese|Japanese|German|French|Italian|Spanish|Russian|Brazilian|Mexican|South African|Nigerian|Egyptian|Italian|Irish|Scottish|Welsh|Dutch|Swedish|Norwegian|Danish|Finnish|Polish|Czech|Hungarian|Greek|Turkish|Saudi|Iranian|Israeli)\\b";
    private static final String GENDER_REGEX = "\\b(Male|Female|Non-binary|Other|Genderqueer|Genderfluid|Agender)\\b";

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("fileUpload") MultipartFile file, Model model) {
        try {
            String parsedText = parseResume(file);

            model.addAttribute("name", extractData(parsedText, NAME_REGEX, "Name not found"));
            model.addAttribute("email", extractData(parsedText, EMAIL_REGEX, "Email not found"));
            model.addAttribute("skills", extractSkills(parsedText));
            model.addAttribute("mobileNumber", extractData(parsedText, MOBILE_REGEX, "Mobile number not found"));
            model.addAttribute("address", extractData(parsedText, ADDRESS_REGEX, "Address not found"));
            model.addAttribute("dateOfBirth", extractData(parsedText, DOB_REGEX, "Date of Birth not found"));
            model.addAttribute("maritalStatus", extractData(parsedText, MARITAL_STATUS_REGEX, "Marital status not found"));
            model.addAttribute("languages", extractLanguages(parsedText));
            model.addAttribute("nationality", extractData(parsedText, NATIONALITY_REGEX, "Nationality not found"));
            model.addAttribute("gender", extractData(parsedText, GENDER_REGEX, "Gender not found"));

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

    private String extractData(String text, String regex, String defaultValue) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim();
        }
        return defaultValue;
    }

    private List<String> extractSkills(String text) {
        List<String> skillKeywords = List.of(
                "Java", "Python", "C++", "Spring", "Hibernate", "SQL",
                "JavaScript", "HTML", "CSS", "Angular", "React", "Node.js",
                "Docker", "Kubernetes", "Spring Boot");

        return skillKeywords.stream()
                .filter(skill -> text.toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> extractLanguages(String text) {
        return extractMultipleData(text, LANGUAGES_REGEX, "Languages not found");
    }

    private List<String> extractMultipleData(String text, String regex, String defaultValue) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        List<String> data = new ArrayList<>();
        while (matcher.find()) {
            data.add(matcher.group().trim());
        }
        return data.isEmpty() ? List.of(defaultValue) : data;
    }
}
