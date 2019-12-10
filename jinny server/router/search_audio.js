const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const db = require('../utils/db');
const err_str = require('../utils/str_err_lang_const');
const multer = require('multer');
const upload = multer({dest: './uploads/'});
const request = require('request');
const keys = require('../utils/keys.js');
const fs = require('fs');
const xml2js = require('xml2js');

const backend_server = 'http://localhost:8192';

// request.debug = true;

router.use(bodyParser.json()); // to support JSON-encoded bodies
router.use(bodyParser.urlencoded({extended: false})); // to support URL-encoded bodies

// Функция логирования
router.use('/', function logs(req, res, next) {
    console.log(req.body, req.headers, req.files);
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
                console.log(err_str['ru_RU.UTF-8']['2']);
                res.json(err_str['ru_RU.UTF-8']['2']);
            } else {
                if((new Date(token[0].end_time)).getTime() > (new Date()).getTime()) {
                    if(req.files.length === 1) {
                        console.log(req.files[0].originalname.split(".").pop());
                        if(req.files[0].originalname.split(".").pop() === "mp3" || req.files[0].originalname.split(".").pop() === "pcm") {
                            var yandex_url = 'http://asr.yandex.net/asr_xml?key=' + keys.yandex_api_key +
                                '&uuid=' + keys.yandex_api_uuid + '&topic=queries&disableAntimat=true';
                            fs.createReadStream(req.files[0].path).pipe(request.post({"url": yandex_url,
                                headers:{"Content-Type": (req.files[0].originalname.split(".").pop() === "mp3") ?
                                        "audio/x-mpeg-3" : "audio/x-pcm;bit=16;rate=16000"}},//TODO: поиск по двум языкам сразу не возможен, подумать
                                    function (err, httpResponse, xml) {
                                        if(err) {
                                            console.error(err);
                                            res.json(err_str['ru_RU.UTF-8']['300']);
                                            //TODO: Тут запрос ко второму лвл бэкенда, при любой ошибке или при не найдености
                                        } else {
                                            console.log(xml);
                                            xml2js.parseString(xml, function(err, result) {
                                                if(err) {
                                                    console.error(err);
                                                    res.json(err_str['ru_RU.UTF-8']['300']);
                                                } else {
                                                    if(result['recognitionResults']['$']['success'] === '1') {
                                                        db.audio.find({$text: {$search: result['recognitionResults']['variant'][0]['_']}}, {
                                                            score: {
                                                                $meta: 'textScore'
                                                            }}).sort({
                                                                score: {
                                                                    $meta: 'textScore'
                                                                }
                                                            }).exec(function (err, result) {
                                                                if(err) {
                                                                    console.error(err);
                                                                    res.json(err_str['ru_RU.UTF-8']['300']);
                                                                } else {
                                                                    console.log(result[0]);
                                                                    res.json(result[0]);
                                                                }
                                                            });
                                                    } else{
                                                        fs.createReadStream(req.files[0].path).pipe(request.post({"url": yandex_url + "&lang=en-US",
                                                                headers:{"Content-Type": (req.files[0].originalname.split(".").pop() === "mp3") ?
                                                                    "audio/x-mpeg-3" : "audio/x-pcm;bit=16;rate=16000"}},
                                                            function (err, httpResponse, xml) {
                                                                if(err) {
                                                                    console.error(err);
                                                                    res.json(err_str['ru_RU.UTF-8']['300']);
                                                                } else {
                                                                    console.log(xml);
                                                                    xml2js.parseString(xml, function(err, result) {
                                                                        if(err) {
                                                                            console.error(err);
                                                                            res.json(err_str['ru_RU.UTF-8']['300']);
                                                                        } else {
                                                                            if(result['recognitionResults']['$']['success'] === '1') {
                                                                                db.audio.find({$text: {$search: result['recognitionResults']['variant'][0]['_']}}, {
                                                                                    score: {
                                                                                        $meta: 'textScore'
                                                                                    }}).sort({
                                                                                    score: {
                                                                                        $meta: 'textScore'
                                                                                    }
                                                                                }).exec(function (err, result) {
                                                                                    if(err) {
                                                                                        console.error(err);
                                                                                        res.json(err_str['ru_RU.UTF-8']['300']);
                                                                                    } else {
                                                                                        console.log(result[0]);
                                                                                        res.json(result[0]);
                                                                                    }
                                                                                });
                                                                            } else{
                                                                                console.log(err_str['ru_RU.UTF-8']['5']);
                                                                                res.json(err_str['ru_RU.UTF-8']['5'])
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }));
                                                        //console.log(111);
                                                        //res.json(err_str['ru_RU.UTF-8']['5'])
                                                    }
                                                }
                                            });
                                            //res.send(xml);
                                        }
                            }));
                        }
                        else {
                            res.json(err_str['ru_RU.UTF-8']['-1'])
                        }
                    } else {
                        res.json(err_str['ru_RU.UTF-8']['-1'])
                    }
                } else {
                    res.json(err_str['ru_RU.UTF-8']['3']);
                }
            }
        });
    }
    else {
        console.log(err_str['ru_RU.UTF-8']['-1']);
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