# Dockerfile frontend Angular — GestorInventario (Angular 22 + Nginx)
# Construye la SPA y la sirve con Nginx, proxy /api al backend

FROM node:24-alpine AS build
WORKDIR /build
COPY package*.json ./
RUN npm ci
COPY . .
RUN npx ng build

FROM nginx:alpine
COPY --from=build /build/dist/frontend-angular /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
