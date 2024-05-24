import React, { useEffect, useState } from 'react';
import api from '../axiosConfig';
import {
    Container, Typography, Card, CardContent, Button, Grid, Divider
} from '@mui/material';

function AdminPanel() {
    const [books, setBooks] = useState([]);
    const [exchanges, setExchanges] = useState([]);

    const fetchAll = () => {
        api.get('/admin/books')
            .then(res => setBooks(res.data))
            .catch(err => console.error('Ошибка получения книг:', err));

        api.get('/admin/exchanges')
            .then(res => setExchanges(res.data))
            .catch(err => console.error('Ошибка получения обменов:', err));
    };

    useEffect(() => {
        fetchAll();
    }, []);

    const handleDelete = (bookId) => {
        api.delete(`/admin/books/${bookId}`)
            .then(() => {
                alert('Книга удалена');
                fetchAll();
            })
            .catch(() => alert('Ошибка при удалении книги'));
    };

    return (
        <Container>
            <Typography variant="h4" mt={4} gutterBottom>Админ-панель: Книги</Typography>
            <Grid container spacing={2}>
                {books.map(book => (
                    <Grid item xs={12} sm={6} md={4} key={book.id}>
                        <Card sx={{ m: { xs: 1, sm: 2 }, width: '100%' }}>
                            <CardContent sx={{ p: { xs: 1, sm: 2 } }}>
                                <Typography variant="h6">{book.title}</Typography>
                                <Typography>Автор: {book.author}</Typography>
                                <Typography>Жанр: {book.genre}</Typography>
                                <Typography>Владелец ID: {book.ownerId}</Typography>
                                <Button color="error" onClick={() => handleDelete(book.id)}>
                                    Удалить книгу
                                </Button>
                            </CardContent>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            <Divider sx={{ my: 4 }} />

            <Typography variant="h4" mt={4} gutterBottom>Админ-панель: Обмены</Typography>
            <Grid container spacing={2}>
                {exchanges.map(exchange => (
                    <Grid item xs={12} sm={6} md={4} key={exchange.id}>
                        <Card sx={{ m: { xs: 1, sm: 2 }, width: '100%' }}>
                            <CardContent sx={{ p: { xs: 1, sm: 2 } }}>
                                <Typography>От: {exchange.fromUserId}</Typography>
                                <Typography>Кому: {exchange.toUserId}</Typography>
                                <Typography>Книга A: {exchange.offeredBook.title}</Typography>
                                <Typography>Книга B: {exchange.requestedBook.title}</Typography>
                                <Typography>Статус: {exchange.status}</Typography>
                                <Typography>Дата: {new Date(exchange.timestamp).toLocaleString()}</Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                ))}
            </Grid>
        </Container>
    );
}

export default AdminPanel;
