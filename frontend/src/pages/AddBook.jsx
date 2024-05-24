import React, { useState } from 'react';
import api from '../axiosConfig';
import { useNavigate } from 'react-router-dom';
import { Container, TextField, Button, Typography, Box } from '@mui/material';

function AddBook() {
    const [title, setTitle] = useState('');
    const [author, setAuthor] = useState('');
    const [genre, setGenre] = useState('');
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        api.post('/books', { title, author, genre })
            .then(() => {
                alert('Книга добавлена!');
                navigate('/');
            })
            .catch(() => alert('Ошибка добавления книги'));
    };

    return (
        <Container maxWidth="sm">
            <Box mt={5}>
                <Typography variant="h4" gutterBottom>Добавить книгу</Typography>
                <form onSubmit={handleSubmit}>
                    <TextField
                        label="Название"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        fullWidth
                        margin="normal"
                        required
                    />
                    <TextField
                        label="Автор"
                        value={author}
                        onChange={(e) => setAuthor(e.target.value)}
                        fullWidth
                        margin="normal"
                        required
                    />
                    <TextField
                        label="Жанр"
                        value={genre}
                        onChange={(e) => setGenre(e.target.value)}
                        fullWidth
                        margin="normal"
                        required
                    />
                    <Button type="submit" variant="contained" color="primary" fullWidth>
                        Добавить
                    </Button>
                </form>
            </Box>
        </Container>
    );
}

export default AddBook;
