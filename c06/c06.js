const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const axios = require('axios');

const app = express();
app.use(bodyParser.json());

const snmpValueSchema = new mongoose.Schema({
    osName: String,
    cpuUsage: Number,
    ramUsage: Number,
});

const SnmpValue = mongoose.model('SnmpValue', snmpValueSchema);

mongoose.connect('mongodb://localhost:27017/snmpdb', {
    useNewUrlParser: true,
    useUnifiedTopology: true,
})
    .then(() => {
        console.log('Connected to MongoDB');
    })
    .catch((err) => {
        console.error('Error connecting to MongoDB:', err);
    });

function saveSnmpValues(osName, cpuUsage, ramUsage) {
    return new Promise((resolve, reject) => {
        const snmpValue = new SnmpValue({ osName, cpuUsage, ramUsage });
        snmpValue.save()
            .then(() => {
                resolve();
            })
            .catch((err) => {
                reject(err);
            });
    });
}

function storeBmpImage(bmpImageData) {
    const mysql = require('mysql');
    const connection = mysql.createConnection({
        host: 'localhost',
        user: 'stud',
        password: 'stud',
        database: 'imagedb'
    });

    connection.connect((err) => {
        if (err) {
            console.error('Error connecting to MySQL:', err);
            res.status(500);
            return;
        }

        const insertQuery = 'INSERT INTO bmp_images (image_data) VALUES (?)';
        connection.query(insertQuery, [bmpImageData], (error, results) => {
            if (error) {
                console.error('Error storing BMP image in MySQL:', error);
                res.status(500);
            } else {
                const generatedId = results.insertId;
                console.log('BMP image saved successfully. ID:', generatedId);
                axios.post('http://c01:3001/images/imageStored', { imageId: generatedId })
                    .then(response => {
                        console.log('Notification sent to c01');
                    })
                    .catch(error => {
                        console.error('Failed to notify c01', error);
                    });
            }
            connection.end();
        });
    });
}


app.get('/api/download-image/:id', (req, res) => {
    const imageId = req.params.id;
    const selectQuery = 'SELECT image_data FROM bmp_images WHERE id = ?';

    connection.query(selectQuery, [imageId], (error, results) => {
        if (error) {
            console.error('Error retrieving BMP image from MySQL:', error);
            res.status(500).send('Internal Server Error');
        } else {
            if (results.length === 0) {
                res.status(404).send('Image not found');
            } else {
                res.setHeader('Content-Type', 'image/bmp');
                res.end(results[0].image_data);
            }
        }
    });
});

app.post('/api/store-data', (req, res) => {
    const { image, snmpData } = req.body;
    const { osName, cpuUsage, ramUsage } = snmpData;
    const image_data = Buffer.from(image, 'base64');

    saveSnmpValues(osName, cpuUsage, ramUsage);
    storeBmpImage(image_data);
});

const PORT = process.env.PORT || 3006;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
