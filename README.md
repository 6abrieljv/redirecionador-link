# 🔗 Redirecionador Link

Aplicacao Spring Boot para redirecionar URLs curtas com cache e tracking de cliques.

## 📌 Sumario

- Visao geral
- Como funciona
- Stack e dependencias
- Configuracao
- Como rodar
- Endpoints e erros
- Banco de dados
- Cache Redis
- GeoIP2
- Testes
- Estrutura do projeto

## 🧭 Visao geral

Este projeto recebe um `slug` curto e devolve um `302 Found` com o header `Location`.
Cada acesso gera um log de clique com informacoes do User-Agent e localizacao (opcional).

## ⚙️ Como funciona (fluxo)

1. Requisicao: `GET /{slug}`
2. Busca no banco e cache (Redis)
3. Valida a URL original (deve iniciar com `http://` ou `https://`)
4. Salva tracking do clique
5. Responde `302` com `Location`

## 🧰 Stack e dependencias

- Spring Boot 4
- Spring Web MVC
- Spring Data JPA (Postgres)
- Spring Cache + Redis
- Yauaa (User-Agent)
- MaxMind GeoIP2 (opcional)

## ⚙️ Configuracao

### 🧾 Variaveis de ambiente (principais)

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `SPRING_CACHE_TYPE`
- `SPRING_DATA_REDIS_HOST`
- `SPRING_DATA_REDIS_PORT`

### 🌍 GeoIP2 (opcional)

- `APP_GEOIP_DATABASE` ou `app.geoip.database` no `application.properties`

Exemplo local com `.env`:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/redirecionador
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_CACHE_TYPE=redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

## ▶️ Como rodar

### 💻 Local (Maven)

1. Suba Postgres/Redis (opcional):
```
docker compose up -d db redis
```

2. Rode a aplicacao:
```
mvn spring-boot:run
```

### 🐳 Docker (app + db + redis)

```
docker compose up --build
```

## 🌐 Dominio e DNS (basico)

### Cloudflare Tunnel (recomendado, sem abrir portas)

1. Adicione o dominio `6abriel.com` no Cloudflare.
2. Troque os nameservers no seu provedor pelo do Cloudflare.
3. Instale o `cloudflared`:
```
winget install Cloudflare.cloudflared
```
4. Login:
```
cloudflared tunnel login
```
5. Crie o tunnel:
```
cloudflared tunnel create redirecionador
```
6. Crie o arquivo `C:\Users\SEU_USUARIO\.cloudflared\config.yml`:
```
tunnel: redirecionador
credentials-file: C:\Users\SEU_USUARIO\.cloudflared\SEU_TUNNEL_ID.json

ingress:
  - hostname: 6abriel.com
    service: http://localhost:8080
  - hostname: www.6abriel.com
    service: http://localhost:8080
  - service: http_status:404
```
7. Crie o DNS automatico:
```
cloudflared tunnel route dns redirecionador 6abriel.com
cloudflared tunnel route dns redirecionador www.6abriel.com
```
8. Rode o tunnel:
```
cloudflared tunnel run redirecionador
```

### DNS direto (sem tunnel)

Use se tiver VPS/servidor com IP fixo:

1. Crie um registro `A` em `6abriel.com` apontando para o IP.
2. Rode a app no servidor e use Nginx/Apache para proxy.

## 🌐 Endpoints e erros

### `GET /{slug}`

- Sucesso: `302 Found`
  - `Location: https://destino.com`

### `POST /links`

Cria um novo link curto.

Request:

```
{
  "slug": "meu-link",
  "originalUrl": "https://youtube.com"
}
```

- `slug` e opcional. Se nao enviar, o sistema gera um automaticamente.

Response (`201 Created`):

```
{
  "slug": "meu-link",
  "originalUrl": "https://youtube.com"
}
```

Header `Location` aponta para `/{slug}`.

Exemplo completo com o dominio da aplicacao:

- Requisicao:
```
POST https://6abriel.com/links
{
  "slug": "meu-link",
  "originalUrl": "https://youtube.com"
}
```

- Resultado:
```
https://6abriel.com/meu-link
```

### `GET /links/{slug}/analytics`

Retorna um resumo dos cliques do link.
Inclui os ultimos 20 cliques com IP e localizacao.

Response (`200 OK`):

```
{
  "slug": "meu-link",
  "originalUrl": "https://youtube.com",
  "totalClicks": 12,
  "deviceClasses": [
    { "label": "Desktop", "count": 8 },
    { "label": "Mobile", "count": 4 }
  ],
  "osNames": [
    { "label": "Windows", "count": 6 },
    { "label": "Android", "count": 4 },
    { "label": "Linux", "count": 2 }
  ],
  "agentNames": [
    { "label": "Chrome", "count": 7 },
    { "label": "Firefox", "count": 3 },
    { "label": "Edge", "count": 2 }
  ],
  "countryNames": [
    { "label": "Brazil", "count": 10 },
    { "label": "Unknown", "count": 2 }
  ],
  "recentClicks": [
    {
      "ipAddress": "203.0.113.10",
      "countryName": "Brazil",
      "regionName": "SP",
      "cityName": "Sao Paulo",
      "deviceClass": "Desktop",
      "osName": "Windows",
      "agentName": "Chrome",
      "clickedAt": "2026-01-27T18:53:51.000Z"
    }
  ]
}
```

### Erros

- `404 Not Found` quando o slug nao existe
- `400 Bad Request` quando a URL salva nao inicia com `http://` ou `https://`
- `409 Conflict` quando o slug ja existe

Exemplo de erro:

```
{
  "error": "slug_not_found",
  "message": "Slug not found: abc",
  "timestamp": "2026-01-27T12:00:00Z"
}
```

## 🗄️ Banco de dados

Tabelas principais (criadas com `spring.jpa.hibernate.ddl-auto=update`).

### `short_links`

- `id` (PK)
- `slug` (unique)
- `original_url`
- `created_at`

### `click_logs`

- `id` (PK)
- `slug`
- `original_url`
- `ip_address`
- `user_agent`
- `device_class`
- `os_name`
- `agent_name`
- `agent_class`
- `country_iso`
- `country_name`
- `region_name`
- `city_name`
- `clicked_at`

## ⚡ Cache Redis

Cache com nome `shortLinkUrl` e chave igual ao `slug`.

```
spring.cache.type=redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## 🌎 GeoIP2

Baixe o banco MaxMind (ex: `GeoLite2-City.mmdb`) e configure:
```
app.geoip.database=classpath:GeoLite2-City.mmdb
```

Se o arquivo nao estiver configurado, o servico retorna "Unknown" e o redirect continua.

## ✅ Testes

```
mvn test
```

Testes incluidos:

- `RedirectControllerTest` (302 e erros 404/400)
- `RedirectServiceTest` (tracking, validacao de URL e IP)

## 🧱 Estrutura do projeto (principais pastas)

- `src/main/java/.../controller` controllers HTTP
- `src/main/java/.../service` regras de negocio
- `src/main/java/.../model` entidades JPA
- `src/main/java/.../repository` repositorios JPA
- `src/main/java/.../exception` erros e handler
- `src/main/resources` configs
- `src/test/java/...` testes
