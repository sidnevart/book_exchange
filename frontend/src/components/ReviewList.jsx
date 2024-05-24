// src/components/ReviewList.jsx
import React, { useEffect, useState } from 'react';
import { Card, CardContent, Typography, Rating, CircularProgress } from '@mui/material';
import api from '../axiosConfig';

const ReviewList = ({ exchangeId, userId }) => {
  const [reviews, setReviews] = useState(null);

  useEffect(() => {
    const url = exchangeId
      ? `/reviews/exchange/${exchangeId}`
      : `/reviews/user/${userId}`;
    api.get(url)
      .then(res => setReviews(res.data))
      .catch(console.error);
  }, [exchangeId, userId]);

  if (reviews === null) return <CircularProgress />;

  if (!reviews.length) {
    return <Typography align="center">Нет отзывов</Typography>;
  }

  return (
    <>
      {reviews.map(r => (
        <Card key={r.id} style={{ marginBottom: 12 }}>
          <CardContent>
            <Rating value={r.rating} readOnly />
            <Typography variant="body2" gutterBottom>
              {r.text}
            </Typography>
            <Typography variant="caption" color="textSecondary">
              {new Date(r.createdAt).toLocaleString()}
            </Typography>
          </CardContent>
        </Card>
      ))}
    </>
  );
};

export default ReviewList;
