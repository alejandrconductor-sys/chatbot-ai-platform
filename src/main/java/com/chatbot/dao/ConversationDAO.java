package com.chatbot.dao;

import com.chatbot.config.DatabaseConfig;
import com.chatbot.model.Conversation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationDAO {

    private static final Logger log = LoggerFactory.getLogger(ConversationDAO.class);

    public void createConversation(Conversation conversation) {
        String sql = "INSERT INTO conversations (id, title, created_at, updated_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, conversation.getId());
            stmt.setString(2, conversation.getTitle());
            stmt.setTimestamp(3, Timestamp.valueOf(conversation.getCreatedAt()));
            stmt.setTimestamp(4, Timestamp.valueOf(conversation.getUpdatedAt()));

            stmt.executeUpdate();
            log.debug("Created conversation: {}", conversation.getId());

        } catch (SQLException e) {
            log.error("Failed to create conversation: {}", e.getMessage());
            throw new RuntimeException("Failed to create conversation", e);
        }
    }

    public List<Conversation> findAllConversations() {
        String sql = "SELECT id, title, created_at, updated_at FROM conversations ORDER BY created_at DESC";
        List<Conversation> conversations = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                conversations.add(mapRowToConversation(rs));
            }

            log.debug("Found {} conversations", conversations.size());

        } catch (SQLException e) {
            log.error("Failed to find all conversations: {}", e.getMessage());
            throw new RuntimeException("Failed to find all conversations", e);
        }

        return conversations;
    }

    public Optional<Conversation> findConversationById(String id) {
        String sql = "SELECT id, title, created_at, updated_at FROM conversations WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Conversation conversation = mapRowToConversation(rs);
                    log.debug("Found conversation: {}", id);
                    return Optional.of(conversation);
                }
            }

            log.debug("Conversation not found: {}", id);
            return Optional.empty();

        } catch (SQLException e) {
            log.error("Failed to find conversation by id: {}", e.getMessage());
            throw new RuntimeException("Failed to find conversation by id", e);
        }
    }

    public void updateConversationTitle(String id, String title) {
        String sql = "UPDATE conversations SET title = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, id);

            int rows = stmt.executeUpdate();
            log.debug("Updated conversation {} ({} rows affected)", id, rows);

        } catch (SQLException e) {
            log.error("Failed to update conversation title: {}", e.getMessage());
            throw new RuntimeException("Failed to update conversation title", e);
        }
    }

    private Conversation mapRowToConversation(ResultSet rs) throws SQLException {
        Conversation conversation = new Conversation();
        conversation.setId(rs.getString("id"));
        conversation.setTitle(rs.getString("title"));
        conversation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        conversation.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return conversation;
    }

}
