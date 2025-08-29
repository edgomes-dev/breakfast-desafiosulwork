# Sulwork — Frontend (React + Tailwind)

Frontend simples e direto para o **Desafio Sulwork**. Implementado com **React + Vite + Tailwind** para rapidez.

> **Observação**: O desafio pedia Angular, mas aqui foi solicitado entregar **React + Tailwind** para ganhar velocidade. O backend (Spring Boot com NativeQuery/JPA) já está pronto e esta UI consome os endpoints REST.

## Funcionalidades

- Cadastro de participante: **nome**, **CPF**, **data do café** e **múltiplas opções** (pão, bolo, suco…).
- Lista de participantes por data, com status dos itens:
  - **Hoje**: é possível marcar/desmarcar se trouxe.
  - **Data passada**: itens não entregues aparecem como **Não trouxe**.
- Validações no front (básicas) + mensagens claras.
- Integração via `VITE_API_BASE_URL` (por padrão `http://localhost:8080`).

> Regras de negócio críticas (não repetir CPF, não repetir opção por data, data > atual) devem ser validadas **no backend**; o front mostra a mensagem de erro retornada.

## Endpoints esperados (ajuste conforme seu backend)

- `GET /participants?date=YYYY-MM-DD`
- `POST /participants` — body: `{ name, cpf, date, items: string[] }`
- `DELETE /participants/{id}`
- `PATCH /items/{itemId}/delivered` — body: `{ delivered: boolean }`

Se seus endpoints forem diferentes, edite `src/lib/api.js`.

## Como rodar (local)

```bash
# 1) Instale as dependências
npm install

# 2) Execute em modo dev
npm run dev

# 3) Abra http://localhost:5173
```

> Configure a URL da API no `.env` (opcional):
>
> ```env
> VITE_API_BASE_URL=http://localhost:8080
> ```

## Build e execução (produção local)

```bash
npm run build
npm run preview
```

## Docker (apenas front)

```bash
docker build -t sulwork-frontend .
docker run -it --rm -p 5173:80 -e VITE_API_BASE_URL=http://localhost:8080 sulwork-frontend
```

## Docker Compose (front + backend + db)

Edite o serviço `backend` para apontar para o seu projeto/imagem.

```bash
docker compose up --build
```

- Frontend: http://localhost:5173  
- Backend: http://localhost:8080  
- Postgres: localhost:5432

## Estrutura

```
src/
  components/
    ParticipantForm.jsx
    ParticipantList.jsx
  lib/
    api.js
    cpf.js
  App.jsx
  main.jsx
  styles.css
```

## Anotações de UX

- Ao salvar, a UI exibe mensagens objetivas de validação.
- Na lista, cada item mostra o status: **Pendente**, **Trouxe** ou **Não trouxe** (quando data já passou).
- Para hoje (`new Date().toISOString().slice(0,10)`), o botão **Marcar/Desmarcar** fica ativo.

## O que **não** está incluso

- Testes de UI/E2E (poderiam ser Cypress, conforme o enunciado; foram omitidos por prazo).
- Autenticação/Autorização.
- Confirmações de exclusão/edição (CRUD mínimo focado no cadastro + listagem).

## Licença

MIT
