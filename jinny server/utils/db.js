const mongoose = require('mongoose');
const db_string = 'mongodb://localhost/oasis';
const db_param = {};

mongoose.connect(db_string, db_param, function (err) {
    if (err) {
        console.error(err);
    }
    console.log('Connected')
});

// mongoose.mongo.db.drop();

const user_schema = mongoose.Schema({
    login: {
        type: String,
        required: true
    },
    password: String,
    lang: String
});

const token_schema = mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'user'
    },
    token: {
        type: String,
        reqired: true
    },
    end_time: Date
});

const audio_schema = mongoose.Schema({
    author: String,
    name: String,
    text: {type: [String], text: true},
    album: {
        name: String
    },
    id: Number,
    genre: String,
    urls: {
        itunes: String,
        gplay: String,
        ya_music: String,
        spotify: String,
        vk: String,
        youtube: String
    }
});

const history_schema = mongoose.Schema({
    object: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'audio'
    },
    likes: Boolean,
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'user'
    }
});


var user = mongoose.model('user', user_schema);

var token = mongoose.model('token', token_schema);

var audio = mongoose.model('audio', audio_schema);

var history = mongoose.model('history', history_schema);


//audio_schema.index({text: 'text'});

const text_sa = "";  //Текст песни для добавления вручную

// var t = new audio({
//     author: 'T.A.T.U.',
//     name: "Космос",
//     id: 1,
//     genre: "Рок",
//     text: text_sa,
//     album: {
//         name: "..."
//     }
// });
//
// t.save(function (err) {
//     console.log("work");
//     if (err) {
//         console.error(err);
//     }
// });

// audio.find({}, null,function (err, res) {
//      console.log (res);
//     res.forEach(function (st, i) {
//     audio.findByIdAndRemove("5acbbdc2f1f8c82d54d03570", function (err) {
//         console.log(err)
//     })});//вот здесь будут все документы
// });

var salt = ""; //Соль для паролей

module.exports.user = user;
module.exports.token =  token;
module.exports.audio = audio;
module.exports.history = history;
module.exports.salt = salt;