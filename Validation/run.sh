#!/bin/bash
CERT_FILE="cert/selfsigned.crt"
PEM_FILE="cert/selfsigned.pem"

# b64 encode
CERT_BASE64=$(base64 -w 0 "$CERT_FILE")

# Extract public key, encode in b64 (DER format)
PUBKEY_BASE64=$(openssl x509 -in "$PEM_FILE" -pubkey -noout | openssl rsa -pubin -outform DER 2>/dev/null | base64 -w 0)
echo "🔐 Validating certificate..."
curl -s -X POST http://localhost:8080/validate \
  -H "Content-Type: application/json" \
  -d "{
    \"certificate\": \"$CERT_BASE64\",
    \"publicKey\": \"$PUBKEY_BASE64\"
  }" | jq
