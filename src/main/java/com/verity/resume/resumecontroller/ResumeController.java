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
            List<String> skills = extractSkills(parsedText);
            String mobileNumber = extractMobileNumber(parsedText);
            String address = extractAddress(parsedText);
            String dateOfBirth = extractDateOfBirth(parsedText);
            String maritalStatus = extractMaritalStatus(parsedText);
            List<String> languages = extractLanguages(parsedText);
            String nationality = extractNationality(parsedText);
            String gender = extractGender(parsedText);

            System.out.println(name + " , " + email + " , " + skills + " , " + mobileNumber + " , " + address + " , "
                    + dateOfBirth + " , " + maritalStatus + " , " + languages + " , " + nationality + " , " + gender);

            // Add extracted data to the model
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("skills", skills);
            model.addAttribute("mobileNumber", mobileNumber);
            model.addAttribute("address", address);
            model.addAttribute("dateOfBirth", dateOfBirth);
            model.addAttribute("maritalStatus", maritalStatus);
            model.addAttribute("languages", languages);
            model.addAttribute("nationality", nationality);
            model.addAttribute("gender", gender);

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
        String nameRegex = "\\b[A-Z][a-z]+(?: [A-Z][a-z]+)*([-' ][A-Z][a-z]+)*\\b";
        Pattern pattern = Pattern.compile(nameRegex);
        Matcher matcher = pattern.matcher(text);

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

    private List<String> extractSkills(String text) {
        // Manually defined list of skills
        List<String> skillKeywords = List.of(
                "Java", "Python", "C++", "Spring", "Hibernate", "SQL",
                "JavaScript", "HTML", "CSS", "Angular", "React", "Node.js",
                "Docker", "Kubernetes", "Spring Boot");

        // Use the list of skills to find matches in the resume text
        return skillKeywords.stream()
                .filter(skill -> text.toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());
    }

    private String extractMobileNumber(String text) {
        String mobileRegex = "\\b(\\+?\\d{1,4}[\\s-]?\\(?\\d{1,4}\\)?[\\s-]?\\d{1,4}[\\s-]?\\d{1,9})\\b";
        Pattern pattern = Pattern.compile(mobileRegex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "Mobile number not found";
    }

    private String extractAddress(String text) {
        // Example regex for address (can be more complex based on requirements)
        String addressRegex = "\\b\\d{1,5} [A-Za-z0-9 ]+(?: Apt \\w+)?(?:, [A-Za-z ]+)?(?:, [A-Z]{2} \\d{5})?(?:, [A-Za-z ]+)?\\b";

        Pattern pattern = Pattern.compile(addressRegex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "Address not found";
    }

    private String extractDateOfBirth(String text) {
        // Regex for different date formats (e.g., DD/MM/YYYY, MM-DD-YYYY, YYYY-MM-DD)
        String dateOfBirthRegex = "\\b(?:\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{2,4}[/-]\\d{1,2}[/-]\\d{1,2}|\\d{4}[./-]\\d{2}[./-]\\d{2}|\\d{1,2} [A-Za-z]+ \\d{4})\\b";

        Pattern pattern = Pattern.compile(dateOfBirthRegex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "Date of Birth not found";
    }

    private String extractMaritalStatus(String text) {
        String maritalStatusRegex = "\\b(Single|Married|Divorced|Widowed|Separated)\\b";
        Pattern pattern = Pattern.compile(maritalStatusRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "Marital status not found";
    }

    private List<String> extractLanguages(String text) {
        String languageKnownRegex = "\\b(English|Spanish|French|German|Chinese|Japanese|Russian|Hindi|Arabic|Portuguese|Italian|Korean|Dutch|Turkish|Swedish|Norwegian|Danish|Finnish|Polish|Czech|Hungarian|Greek|Hebrew|Thai|Vietnamese)\\b";
        Pattern pattern = Pattern.compile(languageKnownRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        List<String> languages = new ArrayList<>();
        while (matcher.find()) {
            languages.add(matcher.group().trim());
        }
        return languages.isEmpty() ? List.of("Languages not found") : languages;
    }

    private String extractNationality(String text) {
        String nationalityRegex = "\\b(American|British|Canadian|Australian|Indian|Chinese|Japanese|German|French|Italian|Spanish|Russian|Brazilian|Mexican|South African|Nigerian|Egyptian|Italian|Irish|Scottish|Welsh|Dutch|Swedish|Norwegian|Danish|Finnish|Polish|Czech|Hungarian|Greek|Turkish|Saudi|Iranian|Israeli)\\b";
        Pattern pattern = Pattern.compile(nationalityRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "Nationality not found";
    }

    private String extractGender(String text) {
        String genderRegex = "\\b(Male|Female|Non-binary|Other|Genderqueer|Genderfluid|Agender)\\b";
        Pattern pattern = Pattern.compile(genderRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim();
        }
        return "Gender not found";
    }

}
