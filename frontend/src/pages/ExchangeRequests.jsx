import React, { useEffect, useState } from 'react';
import { Grid, Card, CardContent, Typography, Button, CircularProgress } from '@mui/material';
import api from '../axiosConfig';

const ExchangeRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchRequests = () => {
    setLoading(true);
    api.get('/exchange/incoming')
      .then(res => {
        setRequests(res.data);
        setError(null);
      })
      .catch(err => {
        console.error(err);
        setError('Не удалось загрузить заявки');
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const handleAccept = (id) => {
    api.post(`/exchange/${id}/confirm`, null, { params: { accept: true } })
      .then(() => fetchRequests())
      .catch(err => {
        console.error(err);
        setError('Ошибка при подтверждении');
      });
  };

  const handleDecline = (id) => {
    api.post(`/exchange/${id}/confirm`, null, { params: { accept: false } })
      .then(() => fetchRequests())
      .catch(err => {
        console.error(err);
        setError('Ошибка при отклонении');
      });
  };

  const handleCancel = (id) => {
    api.patch(`/exchange/${id}/cancel`)
      .then(() => fetchRequests())
      .catch(err => {
        console.error(err);
        setError('Ошибка при отмене заявки');
      });
  };

  if (loading) {
    return <CircularProgress />;
  }

  if (error) {
    return <Typography color="error" align="center">{error}</Typography>;
  }

  return (
    <Grid container spacing={2}>
      {requests.map(req => (
        <Grid item xs={12} md={6} key={req.id}>
          <Card>
            <CardContent>
              <Typography variant="h6">
                От {req.offeredBook?.title || '—'} → {req.requestedBook?.title || '—'}
              </Typography>
              <Typography variant="body2" color="textSecondary">
                Статус: {req.status}
              </Typography>
              <Typography variant="caption">
                Дата: {new Date(req.createdAt).toLocaleString()}
              </Typography>
              <div style={{ marginTop: 16 }}>
                <Button
                  variant="contained"
                  color="primary"
                  onClick={() => handleAccept(req.id)}
                  style={{ marginRight: 8 }}
                  disabled={req.status !== 'PENDING'}
                >
                  Принять
                </Button>
                <Button
                  variant="outlined"
                  color="error"
                  onClick={() => handleDecline(req.id)}
                  style={{ marginRight: 8 }}
                  disabled={req.status !== 'PENDING'}
                >
                  Отклонить
                </Button>
                <Button
                  variant="text"
                  onClick={() => handleCancel(req.id)}
                  disabled={req.status !== 'PENDING'}
                >
                  Отменить
                </Button>
              </div>
            </CardContent>
          </Card>
        </Grid>
      ))}
      {requests.length === 0 && (
        <Grid item xs={12}>
          <Typography align="center">Нет входящих заявок</Typography>
        </Grid>
      )}
    </Grid>
  );
};

export default ExchangeRequests;
