package com.mediBuddy.medicos.service;

import com.mediBuddy.medicos.Exceptions.ResourceAlreadyExist;
import com.mediBuddy.medicos.Exceptions.ResourceNotFoundException;
import com.mediBuddy.medicos.dto.CounslingSessionDTO;
import com.mediBuddy.medicos.dto.DomainDTO;
import com.mediBuddy.medicos.dto.QuestionDTO;
import com.mediBuddy.medicos.model.CounslingSession;
import com.mediBuddy.medicos.model.Question;
import com.mediBuddy.medicos.model.User;
import com.mediBuddy.medicos.repositories.counsilingSessionRepository;
import com.mediBuddy.medicos.repositories.questionRepository;
import com.mediBuddy.medicos.repositories.userRepository;
import com.mediBuddy.medicos.utils.AdditionalFeatures;
import com.mediBuddy.medicos.utils.GoogleGemini;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@AllArgsConstructor
@Getter
@Setter
@Service
public class counslingSessionService {

    private  final counsilingSessionRepository csrepo;
    private  final questionRepository qr;
    private  final userRepository ur;
    private  final AdditionalFeatures af;
    private final GoogleGemini gm;
    private final ModelMapper modelMapper;

    public CounslingSessionDTO createCounslingSession(CounslingSessionDTO s) {
        List<CounslingSession> list = csrepo.findBySessionNameAndUserId(s.getSessionName(), s.getUserId());
        if (!list.isEmpty()) {
            throw new ResourceAlreadyExist("Session With This Name Already Exists");
        }
        // Map DTO to entity
        CounslingSession c = modelMapper.map(s, CounslingSession.class);
        // Save the entity
        CounslingSession savedSession = csrepo.save(c);
        // Map the saved entity back to DTO
        return modelMapper.map(savedSession, CounslingSessionDTO.class);
    }



    public CounslingSessionDTO getDetails(String sessionId) {
        // Fetch session details from the repository
        CounslingSession session = csrepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with ID: " + sessionId));

        // Fetch related questions based on session ID
        List<QuestionDTO> questionList = qr.findBycounslingSessionId(sessionId).stream()
                .map(question -> {
                    QuestionDTO dto = new QuestionDTO();
                    dto.setId(question.getId());
                    dto.setQuestionTitle(question.getQuestionTitle());
                    dto.setAnswer(question.getAnswer());
                    dto.setDomainId(question.getDomainId());
                    dto.setChatId(question.getChatId());
                    dto.setCounslingSessionId(question.getCounslingSessionId());
                    return dto;
                })
                .collect(Collectors.toList());

        // Map session details to DTO
        CounslingSessionDTO dto = new CounslingSessionDTO();
        dto.setId(session.getId());
        dto.setDoctorName(session.getDoctorName());
        dto.setSessionName(session.getSessionName());
        dto.setSummary(session.getSummary());
        dto.setPrecautions(session.getPrecautions());
        dto.setUserId(session.getUserId());
        dto.setQuestionList(questionList);

        // Handle potential null for domain
        if (session.getDomain() != null) {
            dto.setDomain(session.getDomain().stream()
                    .map(domain -> {
                        DomainDTO domainDTO = new DomainDTO();
                        domainDTO.setId(domain.getId());
                        domainDTO.setDomainName(domain.getDomainName());
                        return domainDTO;
                    })
                    .collect(Collectors.toList()));
        } else {
            dto.setDomain(Collections.emptyList());
        }

        return dto;
    }




    public void generateReport(String sessionId){
    Optional<CounslingSession> cs = csrepo.findById(sessionId);
    if (cs.isEmpty()) {
        throw new ResourceNotFoundException("CounslingSession with ID " + sessionId + " not found.");
    }
    CounslingSession session = cs.get();

    // Fetch the user information
    String userId = session.getUserId();
        System.out.println("User Id"+userId);
    Optional<User> user = ur.findById(userId);

    if(user.isEmpty()){
        throw new ResourceNotFoundException("User with ID " + userId + " not found.");
    }

    User  u = user.get();

    af.generateAndSendReport(session,u);
}


    @Transactional
    public CounslingSessionDTO analyzeText(MultipartFile file, String sessionId,String language) {
        Optional<CounslingSession> cs = csrepo.findById(sessionId);
        if (cs.isEmpty()) {
throw new ResourceNotFoundException("Counsling Session Not found");
        }

        String content;
        try {
            content = new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file content", e);
        }

        String cleanedText = gm.preprocessText(content);
        System.out.println("File Text Is This :-" + cleanedText);

        String summary = gm.generateSummary(cleanedText,language);
        String requestPayload = gm.createRequestPayload(summary);
        List<Question> qaPairs = gm.fetchQuestionsFromGeminiSummary(requestPayload, sessionId);

        if (qaPairs == null || qaPairs.isEmpty()) {
            throw new IllegalStateException("QA Pairs are not generated or are empty.");
        }

        qaPairs=qr.saveAll(qaPairs);

        String precaution = gm.generatePrecautions(cleanedText,language);
        System.out.println(precaution);

        CounslingSession c = cs.get();
        c.setSummary(summary);
        c.setQuestionList(qaPairs); // Ensure proper mapping in the entity
        c.setPrecautions(precaution);
        try {
            csrepo.save(c);
            return modelMapper.map(c,CounslingSessionDTO.class);
        } catch (Exception e) {
            System.err.println("Error saving CounslingSession: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save CounslingSession", e);
        }

    }


    // get my session
    public  List<CounslingSessionDTO> getAllMySession(String userId){
        List<CounslingSession> s = csrepo.findByUserId(userId);
        if(s.size()==0){
            return null;
        }
        return s.stream().map(csseion->modelMapper.map(csseion,CounslingSessionDTO.class)).collect(Collectors.toList());
    }



}
