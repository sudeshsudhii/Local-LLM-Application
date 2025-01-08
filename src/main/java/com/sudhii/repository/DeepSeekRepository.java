package com.sudhii.repository;

import com.sudhii.model.DeepSeekDBModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeepSeekRepository extends MongoRepository<DeepSeekDBModel, String> {
}
