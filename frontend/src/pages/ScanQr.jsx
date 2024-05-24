import React, { useState } from 'react';
import { Container, Typography, Box, Button, Alert, CircularProgress } from '@mui/material';
import { QrReader } from '@blackbox-vision/react-qr-reader';
import api from '../axiosConfig';

function ScanQr() {
    const [scanResult, setScanResult] = useState(null);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleScan = async (data) => {
        if (data) {
            setScanResult(data);
            setError(null);
            setSuccess(null);
            setLoading(true);
            try {
                const parsed = JSON.parse(data);
                if (!parsed.exchangeId) throw new Error('QR-код не содержит exchangeId');
                await api.post(`/exchange/${parsed.exchangeId}/confirm-by-qr`);
                setSuccess('Обмен успешно подтверждён!');
            } catch (e) {
                let msg = 'Ошибка подтверждения: ';
                if (e.response && e.response.data && typeof e.response.data === 'string') {
                    msg += e.response.data;
                } else if (e.message) {
                    msg += e.message;
                } else {
                    msg += 'Не удалось обработать QR-код';
                }
                setError(msg);
            } finally {
                setLoading(false);
            }
        }
    };

    const handleError = (err) => {
        setError('Ошибка камеры: ' + err);
    };

    return (
        <Container>
            <Typography variant="h4" mt={4} mb={2}>Сканировать QR-код обмена</Typography>
            <Box sx={{ maxWidth: 400, mx: 'auto' }}>
                <QrReader
                    delay={300}
                    onError={handleError}
                    onScan={handleScan}
                    style={{ width: '100%' }}
                />
                {loading && <CircularProgress sx={{ mt: 2 }} />}
                {success && <Alert severity="success" sx={{ mt: 2 }}>{success}</Alert>}
                {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
            </Box>
        </Container>
    );
}

export default ScanQr; 