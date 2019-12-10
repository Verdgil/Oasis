const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const db = require('../utils/db');
const crypto = require('crypto');
const err_str = require('../utils/str_err_lang_const');

router.use(bodyParser.json()); // to support JSON-encoded bodies
router.use(bodyParser.urlencoded({extended: true})); // to support URL-encoded bodies

// Функция логирования
router.use('/', function logs(req, res, next) {
    // console.log(req.body, req.headers);
    next();
});

// Выдача токенов
router.post('/', function (req, res, next) {
    login = req.body.login;
    password = req.body.password;
    lang = (req.body.lang !== undefined && req.body.lang in err_str) ? (req.body.lang) : ('ru_RU.UTF-8');

    if(login !== undefined && password !== undefined) {
        db.user.find({login: login}).exec(function (err, user) {
            if (err) {
                console.error(err);
                res.json(err_str[lang]['300']);
            }
            if(user.length === 0) {
                const new_user = new db.user({
                    login: login,
                    password: crypto.createHash('sha512').update(password + db.salt).digest('hex'),
                    lang: lang
                });
                new_user.save(function (err) {
                    if(err) {
                        console.error(err);
                        res.json(err_str[lang]['300']);
                    }

                    const new_token = new db.token({
                        user: new_user,
                        token: crypto.createHash('sha512').update(login + req.headers['user-agent'] +
                            (new Date()).getTime()).digest('hex'),
                        end_time: new Date((new Date()).getTime() + 2592000000) //Токен на 30 дней (в милисекундах)
                    });
                    new_token.save(function (err) {
                        if(err) {
                            console.error(err);
                            res.json(err_str[lang]['300']);
                        }
                        res.json({"token": new_token.token});
                    });
                });
            } else {
                res.json(err_str[lang]['0']);
            }
        });
    }
    else {
        res.json(err_str[lang]['-1']);
    }
});

// Отдача страницы регистрации
router.get('/', function (req, res, next) {
   res.send('no');
});

module.exports = router;
