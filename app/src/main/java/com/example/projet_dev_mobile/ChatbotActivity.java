package com.example.projet_dev_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageContext;
import com.ibm.watson.assistant.v2.model.StatefulMessageResponse;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;

import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    // Watson Assistant configuration
    private static final String API_KEY = "***";
    private static final String ASSISTANT_URL = "https://api.au-syd.assistant.watson.cloud.ibm.com";
    private static final String ASSISTANT_ID = "***";
    private static final String VERSION_DATE = "2021-06-14";

    // UI components
    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    // Watson Assistant
    private Assistant assistant;
    private String sessionId;
    private MessageContext messageContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        initializeViews();
        setupRecyclerView();
        initializeWatsonAssistant();
        setupClickListeners();
    }

    private void initializeViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void initializeWatsonAssistant() {
        // Authenticate using IBM Cloud IAM
        IamAuthenticator authenticator = new IamAuthenticator(API_KEY);
        assistant = new Assistant(VERSION_DATE, authenticator);
        assistant.setServiceUrl(ASSISTANT_URL);

        // Create a session
        createSession();
    }

    private void createSession() {
        new Thread(() -> {
            try {
                // Log the configuration for debugging
                android.util.Log.d("Watson", "Assistant ID: " + ASSISTANT_ID);
                android.util.Log.d("Watson", "Service URL: " + ASSISTANT_URL);
                android.util.Log.d("Watson", "Version: " + VERSION_DATE);

                CreateSessionOptions createSessionOptions = new CreateSessionOptions.Builder()
                        .assistantId(ASSISTANT_ID)
                        .build();

                Response<SessionResponse> response = assistant.createSession(createSessionOptions).execute();
                sessionId = response.getResult().getSessionId();

                android.util.Log.d("Watson", "Session created successfully: " + sessionId);

                runOnUiThread(() -> {
                    // Session created successfully, enable send button
                    buttonSend.setEnabled(true);
                    // Send welcome message
                    sendMessage("");
                });
            } catch (com.ibm.cloud.sdk.core.service.exception.NotFoundException e) {
                android.util.Log.e("Watson", "Resource not found - Check Assistant ID and URL", e);
                runOnUiThread(() -> {
                    addMessageToChat("Error: Assistant not found. Please check your Assistant ID and URL configuration.", false);
                });
            } catch (com.ibm.cloud.sdk.core.service.exception.UnauthorizedException e) {
                android.util.Log.e("Watson", "Unauthorized - Check API Key", e);
                runOnUiThread(() -> {
                    addMessageToChat("Error: Unauthorized access. Please check your API key.", false);
                });
            } catch (Exception e) {
                android.util.Log.e("Watson", "Error creating session", e);
                runOnUiThread(() -> {
                    addMessageToChat("Error connecting to Watson Assistant: " + e.getMessage(), false);
                });
            }
        }).start();
    }

    private void setupClickListeners() {
        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessage(userMessage);
                editTextMessage.setText("");
            }
        });

        // Send message when Enter is pressed
        editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                sendMessage(userMessage);
                editTextMessage.setText("");
                return true;
            }
            return false;
        });
    }

    private void sendMessage(String userMessage) {
        // Add user message to chat (if not empty)
        if (!userMessage.isEmpty()) {
            addMessageToChat(userMessage, true);
        }

        // Disable send button while processing
        buttonSend.setEnabled(false);

        new Thread(() -> {
            try {
                // Create message input
                MessageInput input = new MessageInput.Builder()
                        .messageType(MessageInput.MessageType.TEXT)
                        .text(userMessage)
                        .build();

                // Create message options
                MessageOptions.Builder messageOptionsBuilder = new MessageOptions.Builder()
                        .assistantId(ASSISTANT_ID)
                        .sessionId(sessionId)
                        .input(input);

                // Add context if available for stateful responses
                if (messageContext != null) {
                    messageOptionsBuilder.context(messageContext);
                }

                MessageOptions messageOptions = messageOptionsBuilder.build();

                // Send message to Watson Assistant
                Response<StatefulMessageResponse> response = assistant.message(messageOptions).execute();
                StatefulMessageResponse messageResponse = response.getResult();

                runOnUiThread(() -> {
                    // Process the response
                    processWatsonResponse(messageResponse);

                    // Update context for stateful conversation
                    if (messageResponse.getContext() != null) {
                        messageContext = messageResponse.getContext();
                    }

                    buttonSend.setEnabled(true);
                });

            } catch (com.ibm.cloud.sdk.core.service.exception.NotFoundException e) {
                android.util.Log.e("Watson", "Resource not found during message send", e);
                runOnUiThread(() -> {
                    addMessageToChat("Error: Assistant not found. Please check your configuration.", false);
                    buttonSend.setEnabled(true);
                });
            } catch (com.ibm.cloud.sdk.core.service.exception.UnauthorizedException e) {
                android.util.Log.e("Watson", "Unauthorized during message send", e);
                runOnUiThread(() -> {
                    addMessageToChat("Error: Unauthorized access. Please check your API key.", false);
                    buttonSend.setEnabled(true);
                });
            } catch (Exception e) {
                e.printStackTrace();
                android.util.Log.e("Watson", "Error sending message", e);
                runOnUiThread(() -> {
                    addMessageToChat("Error sending message: " + e.getMessage(), false);
                    buttonSend.setEnabled(true);
                });
            }
        }).start();
    }

    private void processWatsonResponse(StatefulMessageResponse response) {
        if (response.getOutput() != null && response.getOutput().getGeneric() != null) {
            for (com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric genericResponse : response.getOutput().getGeneric()) {
                if (genericResponse.responseType().equals("text")) {
                    String botMessage = genericResponse.text();
                    addMessageToChat(botMessage, false);
                }
            }
        }
    }

    private void addMessageToChat(String message, boolean isUser) {
        chatMessages.add(new ChatMessage(message, isUser));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up session when activity is destroyed
        if (sessionId != null && assistant != null) {
            new Thread(() -> {
                try {
                    assistant.deleteSession(new com.ibm.watson.assistant.v2.model.DeleteSessionOptions.Builder()
                            .assistantId(ASSISTANT_ID)
                            .sessionId(sessionId)
                            .build()).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}