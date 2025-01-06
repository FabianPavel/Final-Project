#include "esp_camera.h"
#include <WiFi.h>
#include <esp_http_server.h>
#include <WebSocketsServer.h>

#define CAMERA_MODEL_AI_THINKER
#include "camera_pins.h"
#include "security.h"

// WiFi Credentials
//saved in security.h
const char* ssid = SSID; 
const char* password = passwd;

// WebSocket Server
WebSocketsServer webSocket(81);

// HTTP Server
httpd_handle_t stream_httpd = NULL;

// Flag to check if IP address is printed
bool ipPrinted = false;

// WebSocket event handler
void handleWebSocketMessage(uint8_t num, WStype_t type, uint8_t *payload, size_t length) {
  if (type == WStype_CONNECTED) {
    Serial.println("WebSocket: Client connected");
  } else if (type == WStype_DISCONNECTED) {
    Serial.println("WebSocket: Client disconnected");
  } else if (type == WStype_TEXT) {
    String message = String((char*)payload);
    Serial.println("WebSocket: Received message: " + message);

    // Add motor control logic here
    if (message == "forward") {
      Serial.println("Moving forward");
    } else if (message == "backward") {
      Serial.println("Moving backward");
    } else if (message == "left") {
      Serial.println("Turning left");
    } else if (message == "right") {
      Serial.println("Turning right");
    } else {
      Serial.println("Unknown command");
    }
  }
}

// Camera stream handler
esp_err_t stream_handler(httpd_req_t *req) {
  camera_fb_t *fb = NULL;
  char *part_buf[64];
  static const char *boundary = "frame";

  httpd_resp_set_type(req, "multipart/x-mixed-replace; boundary=frame");

  while (true) {
    fb = esp_camera_fb_get();
    if (!fb) {
      Serial.println("Camera capture failed");
      httpd_resp_send_500(req); // Send HTTP 500 error
      return ESP_FAIL;
    }

    size_t hlen = snprintf((char*)part_buf, 64, "--%s\r\nContent-Type: image/jpeg\r\nContent-Length: %u\r\n\r\n",
                           boundary, fb->len);

    if (httpd_resp_send_chunk(req, (const char*)part_buf, hlen) != ESP_OK) {
      esp_camera_fb_return(fb);
      break;
    }

    if (httpd_resp_send_chunk(req, (const char*)fb->buf, fb->len) != ESP_OK) {
      esp_camera_fb_return(fb);
      break;
    }

    if (httpd_resp_send_chunk(req, "\r\n", 2) != ESP_OK) {
      esp_camera_fb_return(fb);
      break;
    }

    esp_camera_fb_return(fb);
    vTaskDelay(10 / portTICK_PERIOD_MS); // Control frame rate
  }
  return ESP_OK;
}

// Start the camera server
void startCameraServer() {
  httpd_config_t config = HTTPD_DEFAULT_CONFIG();
  config.server_port = 80;

  httpd_uri_t stream_uri = {
      .uri = "/",
      .method = HTTP_GET,
      .handler = stream_handler,
      .user_ctx = NULL};

  if (httpd_start(&stream_httpd, &config) == ESP_OK) {
    httpd_register_uri_handler(stream_httpd, &stream_uri);
  }
}

void setup() {
  Serial.begin(115200);

  // Camera initialization
  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sccb_sda = SIOD_GPIO_NUM;
  config.pin_sccb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG;
  config.frame_size = FRAMESIZE_UXGA;
  config.jpeg_quality = 25; // 0 to 63, lower better quality
  config.fb_count = 1;

  if (esp_camera_init(&config) != ESP_OK) {
    Serial.println("Camera initialization failed");
    return;
  }

  // Connect to WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.println("WiFi connected");

  // Print the IP address only once
  if (!ipPrinted) {
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());
    ipPrinted = true; // Mark IP as printed
  }

  // Start camera server and WebSocket server
  startCameraServer();
  webSocket.begin();
  webSocket.onEvent(handleWebSocketMessage);

  // Only print the IP address once
  Serial.println("Setup complete");
}

void loop() {
  webSocket.loop();
}
