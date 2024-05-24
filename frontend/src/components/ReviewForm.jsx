import React, { useState } from 'react';
import { TextField, Button, Rating } from '@mui/material';
import api from '../axiosConfig';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { toast } from 'react-toastify';

const ReviewForm = ({ exchangeId, revieweeId, onDone }) => {
  const [rating, setRating] = useState(5);
  const [text, setText] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const { userId } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await api.post('/reviews', {
        exchangeId,
        reviewerId: userId,
        revieweeId,
        rating,
        text,
      });
      toast.success('Отзыв отправлен');
      onDone && onDone();
      navigate('/history');
    } catch (err) {
      console.error(err);
      toast.error('Ошибка при отправке отзыва');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginTop: 16 }}>
      <Rating
        name="rating"
        value={rating}
        onChange={(_, val) => setRating(val)}
      />
      <TextField
        label="Комментарий"
        fullWidth
        multiline
        required
        minRows={3}
        value={text}
        onChange={(e) => setText(e.target.value)}
        margin="normal"
      />
      <Button type="submit" variant="contained" disabled={submitting}>
        Отправить отзыв
      </Button>
    </form>
  );
};

export default ReviewForm;