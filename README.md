# API de Transações Bancárias

A API de Transações Bancárias realiza operações de crédito e débito em contas correntes, mantendo controle de saldo, validação de idempotência e prevenção de transações duplicadas. Suporta múltiplas transações por requisição e permite consultar o saldo atualizado de cada conta.

---

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Maven**
- **JPA / Hibernate**
- **H2** (para testes locais)

---

## Como Executar

1. **Clonar o repositório**
   ```sh
   git clone https://github.com/<seu-usuario>/lancamento-bancario.git
   cd lancamento-bancario
   ```

2. **Executar a aplicação**
   ```sh
   mvn spring-boot:run
   ```

3. **Acessar aplicação**
   - [http://localhost:8080](http://localhost:8080)

---

## Endpoints

### `POST /transacao/lancamentos`
Registra uma ou mais transações de crédito e débito em uma conta corrente.

**Request**
```json
[
  {
    "contaCorrente": "12345998-1",
    "tipo": "CREDITO",
    "valor": 5000.00,
    "chaveIdempotencia": "a214066977"
  }
]
```

**Response**
```json
{
  "idTransacao": "8d08d144-7de1-4dc3-a279-098bc2b2bbfb",
  "dataTransacao": "quarta-feira, 22/10/2025",
  "horario": "15h38",
  "valorCreditado": 5000.00,
  "valorDebitado": 0,
  "saldoEmConta": 5250.00
}
```

**Exemplo cURL**
```sh
curl --location 'http://localhost:8080/transacao/lancamentos' \
--header 'Content-Type: application/json' \
--data '[
    {
        "contaCorrente": "12345998-1",
        "tipo": "CREDITO",
        "valor": 5000.00,
        "chaveIdempotencia": "a214066977"
    }
]'
```

---

### `GET /transacao/{contaCorrente}/saldo`
Retorna o saldo atual da conta informada.

**Request**
```
GET http://localhost:8080/transacao/12345998-1/saldo
```

**Response**
```json
{
  "idConta": "33333333-3333-3333-3333-333333333333",
  "contaCorrente": "12345998-1",
  "saldo": 250.00,
  "versao": 0
}
```

**Exemplo cURL**
```sh
curl --location 'http://localhost:8080/transacao/12345998-1/saldo' \
--header 'Content-Type: application/json' \
--data ''
```

---

## Códigos de Resposta

| Código | Descrição                                    |
|--------|----------------------------------------------|
| 200    | Requisição processada com sucesso            |
| 400    | Dados inválidos ou saldo insuficiente        |
| 404    | Conta não encontrada                        |
| 409    | Transação duplicada detectada                |
| 500    | Erro interno inesperado                      |

---

## Regras de Negócio

- O campo `tipo` deve ser **CREDITO** ou **DEBITO**.
- Transações duplicadas são bloqueadas com base na `chaveIdempotencia`.
- Débitos só são permitidos se houver saldo suficiente.
- Todas as operações são transacionais (`@Transactional`).
- Para reenviar uma transação, altere o valor da `chaveIdempotencia`.

---
