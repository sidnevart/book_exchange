-- users
CREATE TABLE "user" (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255),
                        email VARCHAR(255) UNIQUE NOT NULL,
                        role VARCHAR(20) NOT NULL
);

-- books
CREATE TABLE book (
                      id SERIAL PRIMARY KEY,
                      title VARCHAR(255),
                      author VARCHAR(255),
                      genre VARCHAR(255),
                      owner_id BIGINT
);

-- exchange_request
CREATE TYPE exchange_status AS ENUM ('PENDING', 'ACCEPTED', 'DECLINED');

CREATE TABLE exchange_request (
                                  id SERIAL PRIMARY KEY,
                                  from_user_id BIGINT,
                                  to_user_id BIGINT,
                                  offered_book_id BIGINT,
                                  requested_book_id BIGINT,
                                  status exchange_status,
                                  created_at TIMESTAMP
);

-- exchange_history
CREATE TABLE exchange_history (
                                  id SERIAL PRIMARY KEY,
                                  book_id BIGINT,
                                  from_user_id BIGINT,
                                  to_user_id BIGINT,
                                  exchange_date VARCHAR(255)
);
