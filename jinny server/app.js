const express = require('express');
const app = express();
const register = require('./router/register');
const login = require('./router/login');
const search_audio = require('./router/search_audio');
const logout = require('./router/logout');
const get_history = require('./router/get_history');
const err = require('./utils/err');

app.use('/register', register);
app.use('/login', login);
app.use('/search_audio', search_audio);
app.use('/logout', logout);
// app.use('/get_history', get_history);
app.use(err);

app.listen(8192);