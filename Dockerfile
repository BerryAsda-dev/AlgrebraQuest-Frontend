# Frontend Web de AlgebraQuest (Kotlin/Wasm) como servicio propio en Railway.
# Sirve el bundle ya compilado de web-dist/ con nginx. El bundle habla con el backend:
#   https://algebraquest-backend-production.up.railway.app  (RAILWAY_BASE_URL en ApiClient.kt)
# Para regenerar web-dist/:  .\scripts\publish-web.ps1
FROM nginx:1.27-alpine

# Railway inyecta $PORT; nginx lo sustituye en la plantilla al arrancar (por defecto 8080).
ENV PORT=8080
COPY deploy/nginx.conf.template /etc/nginx/templates/default.conf.template
COPY web-dist/ /usr/share/nginx/html/

EXPOSE 8080
