package com.mediBuddy.medicos.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediBuddy.medicos.dto.ApiResponse;
import com.mediBuddy.medicos.dto.QuestionAnswer;
import com.mediBuddy.medicos.model.Question;
import com.mediBuddy.medicos.model.questionType;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Component
public class GoogleGemini {

    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    private final String API_KEY = "AIzaSyAdrqWjPSR6LNOZwZLDo7naWvsKQ7gVbv0";

    private final RestTemplate restTemplate;
    public String createRequestPayload(String domainName) {
        return "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\"text\": \"Generate 25 medical questions and their answers related to " + domainName + " in Array of Object form like [{\\\"question\\\": \\\"What is the most common cause of osteoarthritis?\\\", \\\"answer\\\": \\\"Wear and tear on the joints due to aging.\\\"}, {\\\"question\\\": \\\"What is a common symptom of a rotator cuff tear?\\\", \\\"answer\\\": \\\"Shoulder pain, weakness, and limited range of motion.\\\"}] only give array of object not other text just array of object of questions and answers in this form [{ \\\"question\\\": \\\"Sample question\\\", \\\"answer\\\": \\\"Sample answer\\\" }]\"}\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    public String createChatRequestPayload(String domainName) {
        return "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\"text\": \"Generate repy of medical question or query  related to in This form Message :- " + domainName +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }




    public List<Question> fetchQuestionsFromGemini(String requestPayload, String domainId) {
        try
        {
            // Send POST request to Gemini API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL + "?key=" + API_KEY,
                    HttpMethod.POST,
                    entity,
                    String.class // Get raw response body as String
            );


            // Print the raw response body for debugging
            System.out.println("Raw response body: " + response.getBody());

            // Parse the JSON into ApiResponse object
            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);

            // Extract the text content from the first candidate's part
            List<ApiResponse.Candidate.Content.Part> parts = apiResponse.getCandidates().get(0).getContent().getParts();
            if (parts != null && !parts.isEmpty()) {
                String jsonText = parts.get(0).getText();
                // Extract the JSON array from the text content (remove ```json and surrounding backticks)
                String extractedJson = jsonText.replace("```json", "").replace("```", "").trim();

                // Parse the extracted JSON array into a List of Questions/Answers
                List<QuestionAnswer> questionAnswers = objectMapper.readValue(extractedJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, QuestionAnswer.class));
                List<Question> savedQuestions = new LinkedList<>();
                for (QuestionAnswer qa : questionAnswers) {
                    Question n = new Question();
                    n.setQuestionTitle(qa.getQuestion());
                    n.setAnswer(qa.getAnswer());
                    n.setType(questionType.FAQ);
                    n.setDomainId(domainId);
                    savedQuestions.add(n);
                }

                System.out.println(questionAnswers);
                return savedQuestions;

            }
        }catch (Exception e){
            System.out.println("error"+ e.getMessage());
            return null;
        }

        return  null;
    }





    public List<Question> fetchQuestionsFromGeminiSummary(String requestPayload, String sessionId) {
        try
        {
            // Send POST request to Gemini API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL + "?key=" + API_KEY,
                    HttpMethod.POST,
                    entity,
                    String.class // Get raw response body as String
            );



            // Parse the JSON into ApiResponse object
            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);

            // Extract the text content from the first candidate's part
            List<ApiResponse.Candidate.Content.Part> parts = apiResponse.getCandidates().get(0).getContent().getParts();
            if (parts != null && !parts.isEmpty()) {
                String jsonText = parts.get(0).getText();
                // Extract the JSON array from the text content (remove ```json and surrounding backticks)
                String extractedJson = jsonText.replace("```json", "").replace("```", "").trim();

                // Parse the extracted JSON array into a List of Questions/Answers
                List<QuestionAnswer> questionAnswers = objectMapper.readValue(extractedJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, QuestionAnswer.class));
                List<Question> savedQuestions = new LinkedList<>();
                for (QuestionAnswer qa : questionAnswers) {
                    Question n = new Question();
                    n.setQuestionTitle(qa.getQuestion());
                    n.setAnswer(qa.getAnswer());
                    n.setType(questionType.CSQ);
                    n.setCounslingSessionId(sessionId);
                    savedQuestions.add(n);
                }
                System.out.println(questionAnswers);
                return savedQuestions;
            }
        }catch (Exception e){
            System.out.println("error"+ e.getMessage());
            return null;
        }

        return  null;
    }








    public String generateSummary(String FileText,String Language) {
        try {
            // Create request payload
            String requestPayload = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"parts\": [\n" +
                    "        {\"text\": \"Summarize the following text: \\\"" + FileText + "in 500 words in this form Summary : dont use bold markers in"+Language+ "\\\"\"}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            // Send POST request to Gemini API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL + "?key=" + API_KEY,
                    HttpMethod.POST,
                    entity,
                    String.class // Get raw response body as String
            );

            // Print the raw response body for debugging

            // Parse the JSON into ApiResponse object
            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);

            // Extract the summarized text from the first candidate's part
            List<ApiResponse.Candidate.Content.Part> parts = apiResponse.getCandidates().get(0).getContent().getParts();
            if (parts != null && !parts.isEmpty()) {
                String summarizedText = parts.get(0).getText();
                System.out.println("Final Sended Summary");
                return summarizedText.trim(); // Return the summarized text
            }
        } catch (Exception e) {
            System.out.println("Error in generateSummary: " + e.getMessage());
            return null;
        }
        return null;
    }



    public String generatePrecautions(String FileText,String language) {
        try {
            // Create request payload
            String requestPayload = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"parts\": [\n" +
                    "        {\"text\": \"Create medical Precaution  the following text: \\\"" + FileText + "in 500 words in this form Precaution : dont use bold markers in "+language+ "\\\"\"}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            // Send POST request to Gemini API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL + "?key=" + API_KEY,
                    HttpMethod.POST,
                    entity,
                    String.class // Get raw response body as String
            );

            // Print the raw response body for debugging

            // Parse the JSON into ApiResponse object
            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);

            // Extract the summarized text from the first candidate's part
            List<ApiResponse.Candidate.Content.Part> parts = apiResponse.getCandidates().get(0).getContent().getParts();
            if (parts != null && !parts.isEmpty()) {
                String summarizedText = parts.get(0).getText();
                System.out.println("Final Sended Precaution");
                return summarizedText.trim(); // Return the summarized text
            }
        } catch (Exception e) {
            System.out.println("Error in generating Precaution: " + e.getMessage());
            return null;
        }
        return null;
    }


    public String preprocessText(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }






    public String generateChatResponse(String message,String language) {
        try {
            // Create request payload
            // Create request payload
            String requestPayload = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"parts\": [\n" +
                    "        {\"text\": \"You are a licensed medical professional. As a doctor, your task is to provide a well-informed and accurate response to the following query. Respond in a detailed, professional, and compassionate manner, as you would in a medical consultation. The patient’s question is: '\\\"" + message + "\\\"'\\nPlease answer in " + language + ". Be clear and precise, providing relevant medical advice, potential symptoms, and any necessary next steps for the patient.\"}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";


            // Send POST request to Gemini API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                      API_URL + "?key=" + API_KEY,
                    HttpMethod.POST,
                    entity,
                    String.class // Get raw response body as String
            );

            // Print the raw response body for debugging

            // Parse the JSON into ApiResponse object
            ObjectMapper objectMapper = new ObjectMapper();
            ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);

            // Extract the summarized text from the first candidate's part
            List<ApiResponse.Candidate.Content.Part> parts = apiResponse.getCandidates().get(0).getContent().getParts();
            if (parts != null && !parts.isEmpty()) {
                String summarizedText = parts.get(0).getText();
                System.out.println("Final Sended Precaution");
                return summarizedText.trim(); // Return the summarized text
            }
        } catch (Exception e) {
            System.out.println("Error in generating Precaution: " + e.getMessage());
            return null;
        }
        return null;
    }
}
