package main

import (
	"errors"
	"sync"
)

type User struct {
	ID       int64  `json:"id"`
	Email    string `json:"email"`
	Password string `json:"password"`
	Role     string `json:"role"`
}

type UserRepository struct {
	users   map[string]User
	counter int64
	mu      sync.RWMutex
}

func NewUserRepository() *UserRepository {
	return &UserRepository{
		users:   make(map[string]User),
		counter: 1,
	}
}

func (r *UserRepository) Create(user User) (User, error) {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, exists := r.users[user.Email]; exists {
		return User{}, errors.New("user already exists")
	}

	user.ID = r.counter
	r.counter++
	r.users[user.Email] = user
	return user, nil
}

func (r *UserRepository) GetByEmail(email string) (User, bool) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	user, exists := r.users[email]
	return user, exists
}
