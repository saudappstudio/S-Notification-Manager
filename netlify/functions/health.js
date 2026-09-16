/**
 * Netlify Serverless Function: GET /health
 * Returns backend service status and verifies Firebase environment variable configuration.
 */
exports.handler = async (event, context) => {
  if (event.httpMethod !== "GET") {
    return {
      statusCode: 405,
      body: JSON.stringify({ error: "Method Not Allowed" })
    };
  }

  const configuredKeys = [];
  if (process.env.FIREBASE_DICTIONARY_SERVICE_ACCOUNT) configuredKeys.push("dictionary");
  if (process.env.FIREBASE_VOCABULARY_SERVICE_ACCOUNT) configuredKeys.push("vocabulary");
  if (process.env.FIREBASE_CALCULATOR_SERVICE_ACCOUNT) configuredKeys.push("calculator");

  return {
    statusCode: 200,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      status: "connected",
      version: "1.0.0",
      timestamp: Date.now(),
      service: "Saud Notification Serverless Backend",
      configuredFirebaseProjects: configuredKeys
    })
  };
};
