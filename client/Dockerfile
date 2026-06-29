# --- build stage: Angular -> statički fajlovi ---
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# --- runtime stage: nginx servira SPA i proxy-uje /api ---
FROM nginx:1.27-alpine
COPY --from=build /app/dist/client/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
