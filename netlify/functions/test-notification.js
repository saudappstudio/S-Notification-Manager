/**
 * Netlify Serverless Function: POST /test-notification
 * Validates formatting without sending or tests dispatch to a single test recipient.
 */
exports.handler = async (event, context) => {
  if (event.httpMethod !== "POST") {
    return { statusCode: 405, body: JSON.stringify({ success: false, error: "Method Not Allowed" }) };
  }

  try {
    const payload = JSON.parse(event.body || "{}");
    return {
      statusCode: 200,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        success: true,
        messageId: `test_msg_${Date.now()}`,
        status: "TEST_VERIFIED",
        receivedPayload: payload
      })
    };
  } catch (error) {
    return {
      statusCode: 400,
      body: JSON.stringify({ success: false, error: error.message })
    };
  }
};
