package dev.ebube.complaintchatbot.controller;

import dev.ebube.complaintchatbot.entity.Complaint;
import dev.ebube.complaintchatbot.repository.ComplaintRepository;
import dev.ebube.complaintchatbot.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private AIService aiService;

    @PostMapping
    public ResponseEntity<Complaint> submitComplaint(@RequestBody ComplaintRequest request) {

        //Analyze complaint using AIService (not yet integrated)
        Map<String, String> analysis = aiService.analyzeComplaint(request.getMessage());

        //Create and save complaint entity
        Complaint complaint = new Complaint();
        complaint.setMessage(request.getMessage());
        complaint.setSentiment(analysis.get("sentiment")); // Placeholder for sentiment analysis
        complaint.setCategory(analysis.get("category")); // Placeholder for categorization
        complaint.setAiResponse(analysis.get("response")); // Placeholder for AI response

        Complaint saved = complaintRepository.save(complaint);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintRepository.findAll());
    }

    //Simple Data Transfer Object
    public static class ComplaintRequest {
        public String message;

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }
}