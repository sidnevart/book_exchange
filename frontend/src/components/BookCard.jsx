import React from 'react';
import { Card, CardContent, Typography } from '@mui/material';

function BookCard({ book }) {
    return (
        <Card sx={{ m: { xs: 1, sm: 2 }, width: '100%' }}>
            <CardContent>
                <Typography variant="h6" gutterBottom>
                    {book.title}
                </Typography>
                <Typography color="text.secondary">Автор: {book.author}</Typography>
                <Typography color="text.secondary">Жанр: {book.genre}</Typography>
                <Typography color="text.secondary">Статус: {book.status}</Typography>
            </CardContent>
        </Card>
    );
}

export default BookCard;
