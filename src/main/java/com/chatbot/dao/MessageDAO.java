package com.chatbot.dao;

import com.chatbot.config.DatabaseConfig;
import com.chatbot.model.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDAO {

    private static final Logger log = LoggerFactory.getLogger(MessageDAO.class);

    public void createMessage(Message message) {
        String sql = "INSERT INTO messages (id, conversation_id, role, content, created_at) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, message.getId());
            stmt.setString(2, message.getConversationId());
            stmt.setString(3, message.getRole());
            stmt.setString(4, message.getContent());
            stmt.setTimestamp(5, Timestamp.valueOf(message.getCreatedAt()));

            stmt.executeUpdate();
            log.debug("Created message: {}", message.getId());

        } catch (SQLException e) {
            log.error("Failed to create message: {}", e.getMessage());
            throw new RuntimeException("Failed to create message", e);
        }
    }

    public List<Message> findMessagesByConversationId(String conversationId, int limit) {
        String sql = "SELECT id, conversation_id, role, content, created_at FROM messages "
                    + "WHERE conversation_id = ? ORDER BY created_at ASC LIMIT ?";
        List<Message> messages = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, conversationId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs));
                }
            }

            log.debug("Found {} messages for conversation {}", messages.size(), conversationId);

        } catch (SQLException e) {
            log.error("Failed to find messages by conversation id: {}", e.getMessage());
            throw new RuntimeException("Failed to find messages by conversation id", e);
        }

        return messages;
    }

    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getString("id"));
        message.setConversationId(rs.getString("conversation_id"));
        message.setRole(rs.getString("role"));
        message.setContent(rs.getString("content"));
        message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return message;
    }

    public List<String> getLastMessages(String conversationId, int limit) {

        String sql = "SELECT content FROM messages " +
                    "WHERE conversation_id = ? " +
                    "ORDER BY created_at ASC LIMIT ?";

        List<String> messages = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, conversationId);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(rs.getString("content"));
                }
            }

        } catch (SQLException e) {
            log.error("Failed to get last messages for conversation {}", conversationId, e);
            throw new RuntimeException("Failed to get last messages", e);
        }

        return messages;
    }

}
