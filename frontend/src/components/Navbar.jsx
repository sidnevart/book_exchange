import React from 'react';
import { AppBar, Toolbar, Typography, Button, Stack, IconButton, Drawer, List, ListItem, ListItemText, Box } from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import MenuIcon from '@mui/icons-material/Menu';

function Navbar() {
    const { token, logout } = useAuth();
    const navigate = useNavigate();
    const [mobileOpen, setMobileOpen] = React.useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const menuItems = token ? [
        { label: 'Главная', to: '/' },
        { label: 'Добавить книгу', to: '/add-book' },
        { label: 'Мои обмены', to: '/history' },
        { label: 'Обменяться', to: '/exchange' },
        { label: 'Мои заявки', to: '/requests' },
        ...(localStorage.getItem('isAdmin') === 'true' ? [{ label: 'Админ', to: '/admin' }] : []),
        { label: 'Выйти', action: handleLogout }
    ] : [
        { label: 'Войти', to: '/login' },
        { label: 'Регистрация', to: '/signup' }
    ];

    const drawer = (
        <Box sx={{ width: 250 }} onClick={() => setMobileOpen(false)}>
            <List>
                {menuItems.map((item) => (
                    <ListItem button key={item.label} onClick={item.action ? item.action : () => navigate(item.to)}>
                        <ListItemText primary={item.label} />
                    </ListItem>
                ))}
            </List>
        </Box>
    );

    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    Book Exchange
                </Typography>
                <Stack direction="row" spacing={2} sx={{ display: { xs: 'none', md: 'flex' } }}>
                    {menuItems.map((item) =>
                        item.action ? (
                            <Button key={item.label} color="inherit" onClick={item.action}>{item.label}</Button>
                        ) : (
                            <Button key={item.label} color="inherit" component={Link} to={item.to}>{item.label}</Button>
                        )
                    )}
                </Stack>
                <IconButton
                    color="inherit"
                    edge="end"
                    sx={{ display: { xs: 'flex', md: 'none' } }}
                    onClick={() => setMobileOpen(true)}
                >
                    <MenuIcon />
                </IconButton>
                <Drawer
                    anchor="right"
                    open={mobileOpen}
                    onClose={() => setMobileOpen(false)}
                    ModalProps={{ keepMounted: true }}
                >
                    {drawer}
                </Drawer>
            </Toolbar>
        </AppBar>
    );
}

export default Navbar;