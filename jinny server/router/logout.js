const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const db = require('../utils/db');
const err_str = require("../utils/str_err_lang_const");
const multer = require('multer');
const upload = multer({dest: './uploads/'});
const request = require('request');

const backend_server = 'http://localhost:8080';

router.use(bodyParser.json()); // to support JSON-encoded bodies
router.use(bodyParser.urlencoded({extended: false})); // to support URL-encoded bodies

// Функция логирования
router.use('/', function logs(req, res, next) {
    //console.log(req.body, req.headers);
    next();
});

// Поиск ответа
router.post('/', upload.any(), function (req, res, next) {
    token = req.body.token;
    if(token !== undefined) {
        db.token.find({token: token}).exec(function (err, token) {
            if(err) {
                console.error(err);
                res.json(err_str['ru_RU.UTF-8']['300']);
            }

            if(token.length === 0){
                res.json(err_str['ru_RU.UTF-8']['2']);
            } else {
                if((new Date(token[0].end_time)).getTime() > (new Date()).getTime()) {
                    token[0].end_time = (new Date).getTime() - 86400000;
                    token[0].save(function (err) {
                        if(err){
                            console.error(err);
                            res.json(err_str['ru_RU.UTF-8']['300']);
                        }

                        res.json({"anw": "Ok"});
                    });
                } else {
                    res.json(err_str['ru_RU.UTF-8']['3']);
                }
            }
        })
    }
    else {
        res.json(err_str['ru_RU.UTF-8']['-1']);
    }
});

// Отдача страницы поиска
router.get('/', function (req, res, next) {
    res.send('<!DOCTYPE html>\n' +
        '<html>\n' +
        '<head>\n' +
        '    <title>Регистрация</title>\n' +
        '    <meta charset="utf-8" />\n' +
        '</head>\n' +
        '<body>\n' +
        '    <h1>Введите данные</h1>\n' +
        '    <form action="/search_audio" method="post" enctype="multipart/form-data">\n' +
        '        <label>token</label><br>\n' +
        '        <input type="text" name="token" /><br><br>\n' +
        '        <label>file</label><br>\n' +
        '        <input type="file" name="audio" /><br><br>\n' +
        '        <input type="submit" value="Отправить" />\n' +
        '    </form>\n' +
        '</body>\n' +
        '<html>');
});

module.exports = router;