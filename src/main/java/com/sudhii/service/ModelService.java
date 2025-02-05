package com.sudhii.service;

import com.sudhii.model.DeepSeekDBModel;
import com.sudhii.repository.DeepSeekRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ModelService {

    @Autowired
    private DeepSeekRepository deepSeekRepository;

    public DeepSeekDBModel createChat() {
        DeepSeekDBModel deepSeekDBModel = new DeepSeekDBModel();
        deepSeekDBModel = deepSeekRepository.insert(deepSeekDBModel);
        return deepSeekDBModel;
    }

    public DeepSeekDBModel addNewRecord(String id, String request, String response) {
        Optional<DeepSeekDBModel> dbRecord = deepSeekRepository.findById(id);
        if (!dbRecord.isPresent()) {
            DeepSeekDBModel chat = createChat();
            dbRecord = deepSeekRepository.findById(chat.getId());
        }
        if (dbRecord.isPresent()) {
            DeepSeekDBModel latestRecord = dbRecord.get();
            List<DeepSeekDBModel.Conversation> conv;
            if (Objects.nonNull(latestRecord.getConverstionList()) && !latestRecord.getConverstionList().isEmpty()) {
                conv = latestRecord.getConverstionList();
            } else {
                conv = new ArrayList<>();
            }
            DeepSeekDBModel.Conversation newConv = new DeepSeekDBModel.Conversation();
            newConv.setQuestion(request);
            newConv.setAnswer(response);
            Queue<DeepSeekDBModel.Conversation> convQueue = new LinkedList<>(conv);
            convQueue.add(newConv);
            while (convQueue.size() > 10) {
                convQueue.poll(); // Remove the oldest
            }
            conv = new ArrayList<>(convQueue);
            latestRecord.setConverstionList(conv);

            // Generate title if missing
            if (latestRecord.getTitle() == null || latestRecord.getTitle().isEmpty()) {
                String generatedTitle = request.length() > 30 ? request.substring(0, 30) + "..." : request;
                latestRecord.setTitle(generatedTitle);
            }

            return deepSeekRepository.save(latestRecord);
        }
        return null;
    }

    public void deleteChat(String id) {
        log.info("Deleting chat with ID: {}", id);
        deepSeekRepository.deleteById(id);
    }

    public DeepSeekDBModel getRequest(String id) {
        Optional<DeepSeekDBModel> dbRecord = deepSeekRepository.findById(id);
        if (!dbRecord.isPresent()) {
            return createChat();
        } else {
            return dbRecord.get();
        }
    }

    public List<DeepSeekDBModel> getRequests() {
        List<DeepSeekDBModel> dbRecords = deepSeekRepository.findAll();
        return dbRecords;
    }
}
