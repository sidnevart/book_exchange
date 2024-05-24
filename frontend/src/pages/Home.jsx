import React, { useEffect, useState } from 'react';
import api from '../axiosConfig';
import BookCard from '../components/BookCard';
import { Container, Typography, Grid } from '@mui/material';

function Home() {
    const [books, setBooks] = useState([]);

    useEffect(() => {
        api.get('/books')
            .then(response => setBooks(response.data))
            .catch(error => console.error('Ошибка загрузки книг:', error));
    }, []);

    return (
        <Container>
            <Typography variant="h4" gutterBottom mt={4}>Доступные книги</Typography>
            <Grid container spacing={2}>
                {books.map(book => (
                    <Grid item xs={12} sm={6} md={4} key={book.id}>
                        <BookCard book={book} />
                    </Grid>
                ))}
            </Grid>
        </Container>
    );
}

export default Home;
