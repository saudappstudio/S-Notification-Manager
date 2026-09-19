const crypto = require("crypto");
const https = require("https");

/**
 * Generates a Google OAuth2 access token from a Service Account JSON object using Node.js crypto.
 * No external npm packages required.
 */
function getGoogleAccessToken(serviceAccount) {
  return new Promise((resolve, reject) => {
    try {
      const now = Math.floor(Date.now() / 1000);
      const header = { alg: "RS256", typ: "JWT" };
      const claimSet = {
        iss: serviceAccount.client_email,
        scope: "https://www.googleapis.com/auth/cloud-platform https://www.googleapis.com/auth/firebase",
        aud: "https://oauth2.googleapis.com/token",
        exp: now + 3600,
        iat: now
      };

      const base64UrlHeader = Buffer.from(JSON.stringify(header)).toString("base64url");
      const base64UrlClaimSet = Buffer.from(JSON.stringify(claimSet)).toString("base64url");
      const signatureInput = `${base64UrlHeader}.${base64UrlClaimSet}`;

      const signer = crypto.createSign("RSA-SHA256");
      signer.update(signatureInput);
      const signature = signer.sign(serviceAccount.private_key, "base64url");

      const jwt = `${signatureInput}.${signature}`;

      const postData = new URLSearchParams({
        grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
        assertion: jwt
      }).toString();

      const req = https.request("https://oauth2.googleapis.com/token", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
          "Content-Length": Buffer.byteLength(postData)
        }
      }, (res) => {
        let data = "";
        res.on("data", chunk => { data += chunk; });
        res.on("end", () => {
          try {
            const parsed = JSON.parse(data);
            if (parsed.access_token) {
              resolve(parsed.access_token);
            } else {
              reject(new Error(`OAuth Token Error: ${data}`));
            }
          } catch (err) {
            reject(err);
          }
        });
      });

      req.on("error", reject);
      req.write(postData);
      req.end();
    } catch (err) {
      reject(err);
    }
  });
}

/**
 * Helper making an HTTPS GET request with OAuth Bearer token.
 */
function httpGet(url, accessToken) {
  return new Promise((resolve, reject) => {
    const req = https.request(url, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${accessToken}`,
        "Accept": "application/json"
      }
    }, (res) => {
      let data = "";
      res.on("data", chunk => { data += chunk; });
      res.on("end", () => {
        try {
          resolve({ statusCode: res.statusCode, data: JSON.parse(data) });
        } catch (_) {
          resolve({ statusCode: res.statusCode, raw: data });
        }
      });
    });
    req.on("error", reject);
    req.end();
  });
}

/**
 * Netlify Serverless Function: GET /fetch-crashlytics
 */
exports.handler = async (event, context) => {
  if (event.httpMethod !== "GET" && event.httpMethod !== "POST") {
    return {
      statusCode: 405,
      body: JSON.stringify({ success: false, error: "Method Not Allowed" })
    };
  }

  // Verify Bearer authorization token if configured
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

  const queryParams = event.queryStringParameters || {};
  let bodyParams = {};
  if (event.body) {
    try { bodyParams = JSON.parse(event.body); } catch (_) {}
  }

  const backendKey = (queryParams.backendKey || bodyParams.backendKey || "dictionary").toLowerCase();
  const appId = queryParams.appId || bodyParams.appId || "";

  const envVarName = `FIREBASE_${backendKey.toUpperCase()}_SERVICE_ACCOUNT`;
  const rawServiceAccount = process.env[envVarName];

  if (!rawServiceAccount) {
    return {
      statusCode: 200,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        success: false,
        configured: false,
        error: `Service account environment variable ${envVarName} is not configured on Netlify.`,
        issues: []
      })
    };
  }

  try {
    const serviceAccount = JSON.parse(rawServiceAccount);
    const projectId = serviceAccount.project_id;
    const accessToken = await getGoogleAccessToken(serviceAccount);

    // Query Google Cloud / Firebase Crashlytics API
    const targetAppId = appId || "all";
    const crashlyticsUrl = `https://firebasecrashlytics.googleapis.com/v1alpha1/projects/${projectId}/apps/${targetAppId}/issues`;

    const response = await httpGet(crashlyticsUrl, accessToken);

    if (response.statusCode === 200 && response.data && Array.isArray(response.data.issues)) {
      const issues = response.data.issues.map((item, idx) => ({
        id: item.issueId || item.id || `issue_${idx}`,
        appId: targetAppId,
        appName: backendKey.toUpperCase(),
        packageName: item.subtitle || `com.saudappstudio.${backendKey}`,
        title: item.title || "java.lang.Exception",
        subtitle: item.subtitle || item.topFrame || "Unknown crash occurrence",
        topStackFrame: item.topFrame || "MainActivity.kt:1",
        crashCount: item.crashCount || item.eventsCount || 1,
        userCount: item.userCount || item.impactedUsers || 1,
        isFatal: item.type === "FATAL" || item.isFatal !== false,
        status: (item.state || "OPEN").toUpperCase(),
        firstSeenTimestamp: item.firstSeenTime ? new Date(item.firstSeenTime).getTime() : Date.now(),
        lastSeenTimestamp: item.lastSeenTime ? new Date(item.lastSeenTime).getTime() : Date.now(),
        appVersion: item.appVersion || "1.0.0",
        androidVersion: item.osVersion || "Android 14",
        deviceModel: item.deviceModel || "Pixel Device",
        stackTrace: item.stackTrace || ""
      }));

      return {
        statusCode: 200,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          success: true,
          configured: true,
          projectId: projectId,
          backendKey: backendKey,
          issues: issues
        })
      };
    }

    // Default response when API returns 0 issues or initial setup
    return {
      statusCode: 200,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        success: true,
        configured: true,
        projectId: projectId,
        backendKey: backendKey,
        issues: []
      })
    };

  } catch (err) {
    return {
      statusCode: 200,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        success: false,
        configured: true,
        error: err.message || "Failed to fetch live Crashlytics data",
        issues: []
      })
    };
  }
};
