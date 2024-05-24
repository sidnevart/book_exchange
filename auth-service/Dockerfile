FROM golang:1.22-alpine

WORKDIR /app

COPY go.mod ./
COPY go.sum ./
RUN go mod download

COPY *.go ./

ARG JWT_SECRET
ENV JWT_SECRET=${JWT_SECRET}

RUN go build -o auth-service .

EXPOSE 8081

CMD ["./auth-service"]

