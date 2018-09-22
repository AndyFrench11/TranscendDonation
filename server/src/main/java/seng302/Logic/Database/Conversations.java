package seng302.Logic.Database;

import javafx.util.Pair;
import org.apache.commons.dbutils.DbUtils;
import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.ProfileType;
import seng302.Model.Conversation;
import seng302.Model.Message;
import seng302.Model.User;

import java.sql.*;
import java.util.*;

public class Conversations {
    /**
     * Gets all of the conversations for a specific user.
     *
     * @param id The id of the user to fetch information from
     * @param profileType The type of user
     * @return A list of the user's conversations
     * @throws SQLException If database interaction fails
     */
    public List<Conversation> getAllConversations(int id, ProfileType profileType) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                statement = connection.prepareStatement("SELECT conversation_id FROM CONVERSATION_MEMBER WHERE user_id = ?");
                statement.setInt(1, id);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    conversations.add(getSingleConversation(resultSet.getInt(1)));
                }
            } catch (SQLException ignored) {
                ignored.printStackTrace();
            } finally {
                DbUtils.closeQuietly(resultSet);
                DbUtils.closeQuietly(statement);
            }
        }
        return conversations;
    }


    /**
     * Gets a single conversation from the database.
     *
     * @param conversationId The id of the conversation to fetch
     * @return The conversation
     * @throws SQLException If database interaction fails
     */
    public Conversation getSingleConversation(int conversationId) throws SQLException {
        Conversation conversation = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                statement = connection.prepareStatement("SELECT * FROM MESSAGE WHERE conversation_id = ? ORDER BY MESSAGE.id;");
                statement.setInt(1, conversationId);
                resultSet = statement.executeQuery();
                List<Message> messages = new ArrayList<>();
                while (resultSet.next()) {
                    messages.add(new Message(
                            resultSet.getInt("id"),
                            resultSet.getString("text"),
                            resultSet.getTimestamp("date_time").toLocalDateTime(),
                            resultSet.getInt("user_id")));
                }
                DbUtils.closeQuietly(resultSet);
                DbUtils.closeQuietly(statement);

                statement = connection.prepareStatement("SELECT user_id FROM CONVERSATION_MEMBER WHERE conversation_id = ?;");
                statement.setInt(1, conversationId);
                resultSet = statement.executeQuery();

                List<Integer> participants = new ArrayList<>();
                while (resultSet.next()) {
                    participants.add(resultSet.getInt(1));
                }
                conversation = new Conversation(conversationId, messages, participants);
            } catch (SQLException ignored) {
                ignored.printStackTrace();
            } finally {
                DbUtils.closeQuietly(resultSet);
                DbUtils.closeQuietly(statement);
            }
        }
        if (conversation == null) {
            throw new SQLException("Unable to fetch conversation");
        } else {
            return conversation;
        }
    }

    /**
     * Adds a user to a conversation.
     *
     * @param id The id of the user to add
     * @param profileType The type of user
     */
    public void addConversationUser(int id, ProfileType profileType, int conversationId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement("INSERT INTO CONVERSATION_MEMBER VALUES(?, ?);");
                statement.setInt(1, conversationId);
                statement.setInt(2, id);
                statement.execute();
            } catch (SQLException ignored) {
            } finally {
                DbUtils.closeQuietly(statement);
            }
        }
    }

    /**
     * Adds a message to a conversation.
     *
     * @param conversationId The id of the conversation to add to
     * @param message The message to add
     * @throws SQLException If database interaction fails
     */
    public void addMessage(int conversationId, Message message) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement("INSERT INTO MESSAGE(conversation_id, text, date_time, user_id) VALUES(?, ?, ?, ?);");
                statement.setInt(1, conversationId);
                statement.setString(2, message.getText());
                statement.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));
                statement.setInt(4, message.getUserId());
                statement.execute();
            } catch (SQLException ignored) {
            } finally {
                DbUtils.closeQuietly(statement);
            }
        }
    }

    /**
     * Adds a new conversation to the database.
     *
     * @param participants The ids of all participants
     * @throws SQLException If database interaction fails
     */
    public int addConversation(List<Integer> participants) throws SQLException {
        int id = -1;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement("INSERT INTO CONVERSATION VALUES(0, ?)");
                String token = UUID.randomUUID().toString();
                statement.setString(1, token);
                statement.execute();
                DbUtils.closeQuietly(statement);
                statement = connection.prepareStatement("SELECT id FROM CONVERSATION WHERE token = ?");
                statement.setString(1, token);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                id = resultSet.getInt(1);
                DbUtils.closeQuietly(resultSet);
                DbUtils.closeQuietly(statement);

                for (Integer participant: participants) {
                    try {
                        statement = connection.prepareStatement("INSERT INTO CONVERSATION_MEMBER VALUES(?, ?);");
                        statement.setInt(1, id);
                        statement.setInt(2, participant);
                        statement.execute();
                    } catch (SQLIntegrityConstraintViolationException ignored) {
                    } finally {
                        DbUtils.closeQuietly(statement);
                    }
                }
            } catch (SQLException ignored) {
            } finally {
                DbUtils.closeQuietly(statement);
            }
        }
        return id;
    }

    /**
     * Deletes a conversation from the database.
     *
     * @param conversationId The id of the conversation to delete
     * @throws SQLException If database interaction fails
     */
    public void removeConversation(int conversationId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = null;

            try {
                statement = connection.prepareStatement("DELETE FROM CONVERSATION WHERE id = ?;");
                //This will cascade delete all associated messages and members
                statement.setInt(1, conversationId);
                statement.execute();
            } catch (SQLException ignored) {
            } finally {
                DbUtils.closeQuietly(statement);
            }
        }
    }
}
