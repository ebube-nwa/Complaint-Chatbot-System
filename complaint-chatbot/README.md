# Complaint Chatbot Backend

Spring Boot REST API for AI-powered complaint handling.

## API Endpoints

### Chat Operations
- `POST /api/chat/start` - Start new conversation
- `POST /api/chat/message` - Send message in conversation
- `GET /api/chat/history/{sessionId}` - Get conversation history

### Legacy Complaint Endpoint
- `POST /api/complaints` - Submit standalone complaint
- `GET /api/complaints` - Get all complaints

## Running the Application
```bash
./mvnw spring-boot:run
```

## Database Schema

- `conversations` - Chat sessions
- `messages` - Individual messages
- `complaints` - Legacy standalone complaints