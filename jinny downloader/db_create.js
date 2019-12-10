const mongoose = require('mongoose');
const db_string = 'mongodb://localhost/oasis';
const db_param = {};

// Не закончено

mongoose.connect(db_string, db_param, function (err) {
    if (err) {
        console.error(err);
    }
    console.log('Connected')
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

var audio = mongoose.model('audio', audio_schema);

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
setTimeout(audio.find({}, null,function (err, res) {
    console.log (res);
    //res.forEach(function (st, i) {
    //audio.findByIdAndRemove("5acbbdc2f1f8c82d54d03570", function (err) {
    //    console.log(err)
    //})});//вот здесь будут все документы
}));