// src/pages/ExchangeHistory.jsx
import React, { useEffect, useState } from 'react';
import api from '../axiosConfig';
import { Container, Typography, Card, CardContent, Grid, CircularProgress, Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField, Rating, Box } from '@mui/material';
import { useAuth } from '../hooks/useAuth';
import QRCode from 'qrcode.react';

export default function ExchangeHistory() {
  const { userId } = useAuth();
  const [exchanges, setExchanges] = useState([]);
  const [reviews, setReviews] = useState({});
  const [loading, setLoading] = useState(true);

  const [reviewOpen, setReviewOpen] = useState(false);
  const [qrOpen, setQrOpen] = useState(false);
  const [selected, setSelected] = useState(null);
  const [text, setText] = useState('');
  const [rating, setRating] = useState(5);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    api.get('/exchange/history')
      .then(res => {
        setExchanges(res.data);
        res.data.forEach(e => fetchReviews(e.exchangeId));
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const fetchReviews = (exchangeId) => {
    api.get(`/reviews/exchange/${exchangeId}`)
      .then(res => setReviews(prev => ({ ...prev, [exchangeId]: res.data })))
      .catch(console.error);
  };

  const openReview = (ex) => { setSelected(ex); setText(''); setRating(5); setReviewOpen(true); };
  const closeReview = () => setReviewOpen(false);

  const submitReview = async () => {
    setSubmitting(true);
    try {
      await api.post('/reviews', {
        exchangeId: selected.exchangeId,
        reviewerId: Number(userId),
        revieweeId: selected.direction === 'OUTGOING'
          ? selected.toUserId
          : selected.fromUserId,
        rating,
        text,
      });
      fetchReviews(selected.exchangeId);
      closeReview();
    } catch (e) {
      console.error(e);
    } finally {
      setSubmitting(false);
    }
  };

  const openQr = (ex) => { setSelected(ex); setQrOpen(true); };
  const closeQr = () => setQrOpen(false);

  if (loading) return <CircularProgress />;

  return (
    <Container>
      <Typography variant="h4" mt={4}>История обменов</Typography>
      <Grid container spacing={2}>
        {exchanges.map(ex => (
          <Grid item xs={12} sm={6} md={4} key={ex.exchangeId}>
            <Card>
              <CardContent>
                <Typography>От вас: {ex.offeredBookTitle}</Typography>
                <Typography>Вам: {ex.requestedBookTitle}</Typography>
                <Typography>Статус: {ex.status}</Typography>
                <Typography>Дата: {new Date(ex.createdAt).toLocaleString()}</Typography>

                {reviews[ex.exchangeId]?.length ? (
                  reviews[ex.exchangeId].map(r => (
                    <Box key={r.id} mt={1}>
                      <Rating value={r.rating} readOnly size="small" />
                      <Typography variant="body2">{r.text}</Typography>
                    </Box>
                  ))
                ) : (
                  <Typography variant="body2" color="text.secondary" mt={1}>Нет отзывов</Typography>
                )}

                {ex.completed && (
                  <Box mt={2} display="flex" gap={1}>
                    <Button variant="outlined" onClick={() => openReview(ex)}>Оставить отзыв</Button>
                    <Button variant="outlined" onClick={() => openQr(ex)}>Показать QR</Button>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={reviewOpen} onClose={closeReview}>
        <DialogTitle>Оставить отзыв</DialogTitle>
        <DialogContent>
          <Rating value={rating} onChange={(_, v) => setRating(v)} />
          <TextField
            label="Комментарий"
            fullWidth
            multiline
            minRows={2}
            value={text}
            onChange={e => setText(e.target.value)}
            margin="normal"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={closeReview}>Отмена</Button>
          <Button onClick={submitReview} disabled={submitting || !text}>Сохранить</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={qrOpen} onClose={closeQr}>
        <DialogTitle>QR-код</DialogTitle>
        <DialogContent>
          {selected && (
            <QRCode
              value={JSON.stringify({ exchangeId: selected.exchangeId })}
              size={200}
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={closeQr}>Закрыть</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}