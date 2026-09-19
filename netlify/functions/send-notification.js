const admin = require("firebase-admin");

/**
 * Initializes or retrieves a named Firebase Admin app instance based on backendKey.
 */
function getFirebaseApp(backendKey) {
  const appName = `firebase_app_${backendKey}`;
  const existingApp = admin.apps.find(app => app.name === appName);
  if (existingApp) return existingApp;

  // Resolve service account from Netlify environment variable
  const envVarName = `FIREBASE_${backendKey.toUpperCase()}_SERVICE_ACCOUNT`;
  const rawServiceAccount = process.env[envVarName];

  if (!rawServiceAccount) {
    throw new Error(`Service account credentials missing for backendKey '${backendKey}'. Configure ${envVarName} in Netlify.`);
  }

  let serviceAccount;
  try {
    serviceAccount = JSON.parse(rawServiceAccount);
  } catch (err) {
    throw new Error(`Failed to parse JSON credentials for ${envVarName}: ${err.message}`);
  }

  return admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  }, appName);
}

/**
 * Netlify Serverless Function: POST /send-notification
 */
exports.handler = async (event, context) => {
  if (event.httpMethod !== "POST") {
    return {
      statusCode: 405,
      body: JSON.stringify({ success: false, error: "Method Not Allowed" })
    };
  }

  // Verify Bearer authentication token
  const authHeader = event.headers.authorization || event.headers.Authorization || "";
  const expectedToken = process.env.API_SECRET;
  if (expectedToken) {
    const token = authHeader.replace(/^Bearer\s+/i, "").trim();
    if (token !== expectedToken) {
      return {
        statusCode: 401,
        body: JSON.stringify({ success: false, error: "Unauthorized: Invalid API secret token" })
      };
    }
  }

  try {
    const payload = JSON.parse(event.body || "{}");
    const {
      backendKey = "dictionary",
      targetType = "TOPIC",
      target,
      title,
      message,
      imageUrl,
      clickAction,
      deepLink,
      channelId,
      priority = "HIGH",
      notificationType = "PUSH",
      eventTrigger = "timer_1_min",
      customData = {}
    } = payload;

    if (!target || !title || !message) {
      return {
        statusCode: 400,
        body: JSON.stringify({ success: false, error: "Missing required fields (target, title, message)" })
      };
    }

    const firebaseApp = getFirebaseApp(backendKey);
    const messaging = firebaseApp.messaging();

    const isFiam = notificationType === "IN_APP";

    // Prepare FCM message payload
    const fcmMessage = {
      data: {
        notification_type: notificationType,
        event_trigger: eventTrigger,
        clickAction: clickAction || "OPEN_APP",
        deepLink: deepLink || "",
        title: title,
        body: message,
        ...(imageUrl ? { image: imageUrl, imageUrl: imageUrl } : {}),
        ...customData
      },
      android: {
        priority: priority.toLowerCase() === "high" ? "high" : "normal"
      }
    };

    // Only include top-level system notification block for standard PUSH notifications
    if (!isFiam) {
      fcmMessage.notification = {
        title: title,
        body: message,
        ...(imageUrl ? { image: imageUrl } : {})
      };
      fcmMessage.android.notification = {
        channelId: channelId || "general_notifications",
        sound: "default",
        ...(imageUrl ? { image: imageUrl } : {})
      };
    }

    if (targetType === "TOPIC") {
      fcmMessage.topic = target;
    } else {
      fcmMessage.token = target;
    }

    const messageId = await messaging.send(fcmMessage);

    return {
      statusCode: 200,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        success: true,
        messageId: messageId,
        dispatchedAt: Date.now()
      })
    };
  } catch (error) {
    return {
      statusCode: 500,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        success: false,
        error: error.message || "Failed to dispatch notification via Firebase Admin SDK"
      })
    };
  }
};
