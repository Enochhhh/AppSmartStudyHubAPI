package com.focusedapp.smartstudyhub.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.focusedapp.smartstudyhub.model.ChatMessage;

@Repository
public interface ChatMessageDAO extends JpaRepository<ChatMessage, Integer> {

}
