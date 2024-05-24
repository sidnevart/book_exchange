import React, { useEffect, useState } from 'react';
import api from '../axiosConfig';
import { Container, Typography, FormControl, InputLabel, Select, MenuItem, Button, Box } from '@mui/material';

function ExchangeForm() {
    const [myBooks, setMyBooks] = useState([]);
    const [otherBooks, setOtherBooks] = useState([]);
    const [offeredBookId, setOfferedBookId] = useState('');
    const [requestedBookId, setRequestedBookId] = useState('');

    useEffect(() => {
        api.get('/books?mine=true')
            .then(response => setMyBooks(response.data))
            .catch(err => console.error('Ошибка загрузки своих книг:', err));

        api.get('/books?mine=false')
            .then(response => setOtherBooks(response.data))
            .catch(err => console.error('Ошибка загрузки чужих книг:', err));
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();

        const requestedBook = otherBooks.find(b => b.id === requestedBookId);
        const toUserId = requestedBook.ownerId;

        api.post('/exchange', {
            toUserId,
            offeredBookId,
            requestedBookId,
        })
            .then(() => alert('Заявка на обмен отправлена!'))
            .catch(() => alert('Ошибка при отправке заявки'));
    };

    return (
        <Container maxWidth="sm">
            <Box mt={5}>
                <Typography variant="h4" gutterBottom>Обмен книгами</Typography>
                <form onSubmit={handleSubmit}>
                    <FormControl fullWidth margin="normal" required>
                        <InputLabel>Выберите вашу книгу</InputLabel>
                        <Select
                            value={offeredBookId}
                            onChange={(e) => setOfferedBookId(e.target.value)}
                        >
                            {myBooks.map(book => (
                                <MenuItem key={book.id} value={book.id}>
                                    {book.title} — {book.author}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <FormControl fullWidth margin="normal" required>
                        <InputLabel>Книга, которую хотите получить</InputLabel>
                        <Select
                            value={requestedBookId}
                            onChange={(e) => setRequestedBookId(e.target.value)}
                        >
                            {otherBooks.map(book => (
                                <MenuItem key={book.id} value={book.id}>
                                    {book.title} — {book.author} (владелец #{book.ownerId})
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <Button type="submit" variant="contained" color="primary" fullWidth>
                        Отправить заявку
                    </Button>
                </form>
            </Box>
        </Container>
    );
}

export default ExchangeForm;
