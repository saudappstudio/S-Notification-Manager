# Netlify Serverless Backend Specification

## 1. Overview
The Saud Notification Manager Android application delegates all Firebase Admin SDK push operations to a secure serverless backend hosted on Netlify Functions. 

**Critical Security Guarantee:** No Firebase Admin service-account private keys are stored inside the Android APK. Credentials remain strictly isolated inside Netlify environment variables.

---

## 2. Directory Structure
```
netlify/
├── package.json
└── functions/
    ├── health.js
    ├── send-notification.js
    └── test-notification.js
```

---

## 3. Environment Variables Configuration (Netlify Dashboard)

Set the following environment variables in your Netlify site under:
**Site configuration > Environment variables**

| Variable Name | Description | Example Content |
| :--- | :--- | :--- |
| `API_SECRET` | Secret token required in `Authorization: Bearer <API_SECRET>` header | `saud_sec_99381029481` |
| `FIREBASE_DICTIONARY_SERVICE_ACCOUNT` | JSON string of Firebase Admin Service Account for Advanced English Dictionary | `{"type":"service_account","project_id":"saud-dictionary-prod",...}` |
| `FIREBASE_VOCABULARY_SERVICE_ACCOUNT` | JSON string of Firebase Admin Service Account for Vocabulary Builder | `{"type":"service_account","project_id":"saud-vocabulary-prod",...}` |
| `FIREBASE_CALCULATOR_SERVICE_ACCOUNT` | JSON string of Firebase Admin Service Account for Calculator | `{"type":"service_account","project_id":"saud-calculator-prod",...}` |

---

## 4. API Endpoints

### 4.1 Health Check
- **Endpoint:** `GET /.netlify/functions/health`
- **Headers:** Optional
- **Response (200 OK):**
```json
{
  "status": "connected",
  "version": "1.0.0",
  "timestamp": 1726521600000,
  "service": "Saud Notification Serverless Backend",
  "configuredFirebaseProjects": ["dictionary", "vocabulary", "calculator"]
}
```

### 4.2 Send Notification
- **Endpoint:** `POST /.netlify/functions/send-notification`
- **Headers:**
  - `Authorization: Bearer <API_SECRET>`
  - `Content-Type: application/json`
- **Request Body:**
```json
{
  "appId": "app_dictionary",
  "backendKey": "dictionary",
  "environment": "PRODUCTION",
  "targetType": "TOPIC",
  "target": "dictionary_all",
  "title": "Word of the Day",
  "message": "Discover today's word: Serendipity",
  "imageUrl": "https://example.com/banner.png",
  "clickAction": "DEEP_LINK",
  "deepLink": "sauddictionary://word/serendipity",
  "channelId": "general_notifications",
  "priority": "HIGH",
  "ttl": 86400,
  "collapseKey": "word_of_day",
  "badge": 1,
  "customData": {
    "screen": "word_detail",
    "word": "serendipity"
  }
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "messageId": "projects/saud-dictionary-prod/messages/fcm_883210941",
  "dispatchedAt": 1726521605000
}
```
- **Error Response (401 / 400 / 500):**
```json
{
  "success": false,
  "error": "Detailed error message"
}
```

### 4.3 Test Notification
- **Endpoint:** `POST /.netlify/functions/test-notification`
- **Headers:** `Content-Type: application/json`
- **Response (200 OK):**
```json
{
  "success": true,
  "messageId": "test_msg_1726521610000",
  "status": "TEST_VERIFIED"
}
```

---

## 5. Deployment Instructions

1. Install Netlify CLI:
   ```bash
   npm install -g netlify-cli
   ```
2. Navigate to the `netlify/` directory and deploy:
   ```bash
   cd netlify
   npm install
   netlify deploy --prod
   ```
3. Copy your deployed Netlify function URL (e.g., `https://your-site.netlify.app/.netlify/functions`) and paste it into the **Settings > Backend & API** screen inside the Android app.
